package edu.cmu.twitter.model;
import org.apache.spark.sql.SparkSession;

public class SparkEnvironment {
    public static SparkSession createSparkSession() {
        return SparkSession.builder()
                .appName("Twitter Data Analysis")
                .master("local")
                .getOrCreate();
    }
}
