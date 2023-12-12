package org.example;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

public class InitMysqlDB {

    private static final String JDBC_URL = "jdbc:mysql://localhost:3306/twitter_db?useSSL=false&allowPublicKeyRetrieval=true";
    private static final String JDBC_USER = "root";
    private static final String JDBC_PASSWORD = "Cc88888888@";

    public static void main(String[] args) {
        try (Connection connection = DriverManager.getConnection(JDBC_URL, JDBC_USER, JDBC_PASSWORD)) {
            Statement stmt = connection.createStatement();

            // Drop existing tables
            stmt.executeUpdate("DROP TABLE IF EXISTS Tweet_Hashtags");
            stmt.executeUpdate("DROP TABLE IF EXISTS Tweets");
            stmt.executeUpdate("DROP TABLE IF EXISTS Hashtags");
            stmt.executeUpdate("DROP TABLE IF EXISTS Users");

            // Create Users table
            String createUsersTable = "CREATE TABLE Users (" +
                    "user_id VARCHAR(255) PRIMARY KEY, " +
                    "screen_name VARCHAR(255), " +
                    "name VARCHAR(255), " +
                    "description TEXT, " +
                    "created_at DATETIME)";
            stmt.executeUpdate(createUsersTable);

            // Create Tweets table with a new hashtags column
            String createTweetsTable = "CREATE TABLE Tweets (" +
                    "tweet_id VARCHAR(255) PRIMARY KEY, " +
                    "user_id VARCHAR(255), " +
                    "text TEXT, " +
                    "hashtags VARCHAR(255), " +
                    "created_at DATETIME, " +
                    "in_reply_to_user_id VARCHAR(255), " +
                    "retweet_status_user_id VARCHAR(255), " +
                    "FOREIGN KEY(user_id) REFERENCES Users(user_id))";
            stmt.executeUpdate(createTweetsTable);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

