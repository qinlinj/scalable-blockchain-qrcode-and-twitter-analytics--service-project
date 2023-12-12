package edu.cmu;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
//import java.util.Properties;

import org.apache.spark.api.java.function.FilterFunction;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.functions;
import org.json.JSONObject;

public class ETLSpark {

//    // start spark session
//    // connect to MySQL jdbc
//    /**
//     * JDBC driver of MySQL Connector/J.
//     */
//    private static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
//    /**
//     * Database name.
//     */
//    private static final String DB_NAME = "twitter_db";
//
//    /**
//     * The endpoint of the database.
//     *
//     * To avoid hardcoding credentials, use environment variables to include
//     * the credentials.
//     *
//     * e.g., before running "mvn clean package exec:java" to start the server
//     * run the following commands to set the environment variables.
//     * export MYSQL_HOST=...
//     * export MYSQL_NAME=...
//     * export MYSQL_PWD=...
//     */
//    private static String mysqlHost = System.getenv("MYSQL_HOST");
//    /**
//     * MySQL username.
//     */
//    private static String mysqlName = System.getenv("MYSQL_NAME");
//    /**
//     * MySQL Password.
//     */
//    private static String mysqlPwd = System.getenv("MYSQL_PWD");
//
//    /**
//     * MySQL URL.
//     */
//    private static final String URL =
//            "jdbc:mysql://" +
//                    mysqlHost +
//                    ":3306/" +
//                    DB_NAME +
//                    "?useSSL=false&serverTimezone=UTC";
//
//    private static Properties connProperties;

    /**
     * Initialize SQL connection. Standard constructor
     */
    public ETLSpark() {
//        connProperties.setProperty("user", mysqlName);
//        connProperties.setProperty("password", mysqlPwd);
//        connProperties.setProperty("driver", JDBC_DRIVER);
    }

    // spark.read -> filter -> write to DB
    public void dataETL(){
        String inputFile="/home/azureuser/test.txt";
        SparkSession spark = SparkSession.builder().appName("Parse and filter JSONs to MySQL DB").getOrCreate();
        Dataset<String> jsons = spark.read().textFile(inputFile);
        // Filter out jsons that can't be parsed to json
        jsons=jsons.filter((FilterFunction<String>) line ->{
            try{
                JSONObject tweetObject = new JSONObject(line);
                return true;
            }catch (Exception e){
                return false;
            }
        });
        Dataset<Row> unfiltered=spark.read().json(jsons);
        // Filter out jsons with non-null id/id_str
        Dataset<Row> filtered=unfiltered.filter(
                unfiltered.col("id_str")
                        .isNotNull()
                        .and(
                                unfiltered.col("id").isNotNull()
                        )
        );
        // Filter out jsons without user.id/user.id_str
        filtered=filtered.filter(
                filtered.col("user.id")
                        .isNotNull()
                        .and(filtered.col("user.id_str")
                                .isNotNull()
                        )
        );
        // Filter out null created_at
        filtered=filtered.filter(filtered.col("created_at").isNotNull());
        // Filter out empty or null text
        filtered=filtered.filter(filtered.col("text").isNotNull())
                .filter(filtered.col("text").notEqual(""));
        // Filter out empty or null hashtag
        filtered=filtered.filter(filtered.col("entities.hashtags").isNotNull())
                .filter(functions.size(functions.col("entities.hashtags")).gt(0));
        // Filter out lang not in the list
        String[] targetLang=new String[]{"ar","en","fr","in","pt","es","tr","ja"};
        filtered=filtered.filter(filtered.col("lang").isin(targetLang));
        // Drop duplicate ids
        filtered=filtered.dropDuplicates("id_str");

        // Create a user table
        Dataset<Row> users=filtered.select("user");
        users=users.union(filtered.select(
                filtered.col("retweeted_status.user").alias("user")
        ));
        users=users.groupBy("user.id").agg(functions.max("user.created_at"));

        // Clean twitter table
        Dataset<Row> twitter=filtered.select("id","id_str","user.id_str","user.id","text","created_at","in_reply_to_user_id","in_reply_to_user_id_str","created_at","retweeted_status.user.id","retweeted_status.user.id_str");

        users.show();
        twitter.show();
        // Write to database
//        users.write().mode("overwrite").jdbc(URL,"users",connProperties);
//        twitter.write().mode("overwrite").jdbc(URL, "twitter", connProperties);
        spark.stop();
    }

    public static void main(String[] args) {
        ETLSpark etl=new ETLSpark();
        // TODO: replace args[0] with the actual path of the json
        etl.dataETL();
    }
}
