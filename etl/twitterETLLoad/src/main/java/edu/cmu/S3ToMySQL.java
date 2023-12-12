import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;
import java.util.List;

import java.io.File;
import java.io.FileOutputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;

public class S3ToMySQL {

    private static String aws_accesskey = System.getenv("aws_accesskey");
    private static String aws_secretkey = System.getenv("aws_secretkey");
    private static String s3BucketName = System.getenv("s3BucketName");
    private static String mysql_user = System.getenv("mysql_user");
    private static String mysql_pwd = System.getenv("mysql_pwd");
    private static String mysql_host= System.getenv("mysql_host");
    private static String jdbcUrl = "jdbc:mysql://"+mysql_host+":3306/twitter_db?" +
            "useUnicode=true" +
//            "&characterSetResults=utf8mb4" +
            "&characterEncoding=UTF-8" +
            "&connectionCollation=utf8mb4_unicode_ci" +
            "&allowLoadLocalInfile=true";

    public static void loadTable(String s3DirectoryKey, S3Client s3Client){

        // Initialize the MySQL connection
        try (Connection connection = DriverManager.getConnection(jdbcUrl, mysql_user, mysql_pwd)) {

            // List object
            ListObjectsRequest listObjects = ListObjectsRequest
                    .builder()
                    .bucket(s3BucketName)
                    .prefix(s3DirectoryKey)
                    .build();
            ListObjectsResponse res = s3Client.listObjects(listObjects);
            List<S3Object> objects = res.contents();
            for (S3Object myValue : objects) {
                // get object
                String keyName=myValue.key();
                System.out.println(keyName);
                GetObjectRequest objectRequest = GetObjectRequest
                        .builder()
                        .key(keyName)
                        .bucket(s3BucketName)
                        .build();

                // copy data to local file
                File localFile = new File("temp_file");
                ResponseBytes<GetObjectResponse> objectBytes = s3Client.getObjectAsBytes(objectRequest);
                byte[] data = objectBytes.asByteArray();
                FileOutputStream os = new FileOutputStream(localFile);
                os.write(data);
                os.close();

                // Load the data from the local file into the MySQL database
                String sql="LOAD DATA LOCAL INFILE '"+localFile.getAbsolutePath()+
                        "' INTO TABLE "+ s3DirectoryKey +" "+
                        "CHARACTER SET utf8mb4 " +
                        "FIELDS ENCLOSED BY '\"' " +
                        "TERMINATED BY '\\t' " +
                        "ESCAPED BY '\\\\' " +
                        "LINES TERMINATED BY '\\n';";

                try (PreparedStatement stmt = connection
                        .prepareStatement(sql)) {
                    stmt.executeUpdate();
                }

                // Clean up: delete the local file
                localFile.delete();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {

        // Initialize the S3 client
        Region region = Region.US_EAST_1;

        AwsBasicCredentials awsCreds = AwsBasicCredentials.create(aws_accesskey, aws_secretkey);

        S3Client s3Client = S3Client.builder()
                .region(region)
                .credentialsProvider(StaticCredentialsProvider.create(awsCreds))
                .build();

        System.out.println("Start loading CompleteTweets");
        loadTable("CompleteTweets",s3Client);
        System.out.println("Finished loading CompleteTweets");

//        System.out.println("Start loading Users");
//        loadTable("Users",s3Client);
//        System.out.println("Finished loading Users");

//        System.out.println("Start loading Tweets");
//        loadTable("Tweets",s3Client);
//        System.out.println("Finished loading Tweets");

        System.exit(0);
    }
}
