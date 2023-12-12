//package edu.cmu.twitter.utility;
//
//import java.sql.Connection;
//import java.sql.DriverManager;
//import java.sql.SQLException;
//
//public class DBConnector {
//private static final String DB_URL = "";
//private static final String USER = "";
//private static final String PASSWORD = "";
//
////    Time to establish connection: 757 ms
//    public static Connection getConnection() {
//        try {
//            return DriverManager.getConnection(DB_URL, USER, PASSWORD);
//        } catch (SQLException e) {
//            throw new RuntimeException("Failed to connect to the database", e);
//        }
//    }
//
//    public static void main(String[] args) {
//        try {
//            Connection connection = DBConnector.getConnection();
//            System.out.println("Successfully connected to the database.");
//
//            connection.close();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//}


package edu.cmu.twitter.utility;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.SQLException;

public class DBConnector {
    private static final String DB_URL = "jdbc:mysql://twitter-db-cluster.cluster-ro-ctw6uemppxrg.us-east-1.rds.amazonaws.com:3306/twitter_db";
    private static final String USER = "admin";
    private static final String PASSWORD = "admindbroot";
//    private static final String DB_URL = "jdbc:mysql://localhost:3306/twitter_db";
//    private static final String USER = "root";
//    private static final String PASSWORD = "Cc88888888@";

    private static HikariDataSource dataSource;

    static {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(DB_URL);
        config.setUsername(USER);
        config.setPassword(PASSWORD);


//        config.setMaximumPoolSize(15);
//        config.setMinimumIdle(5);
//        config.setConnectionTimeout(30000);
//        config.setIdleTimeout(600000);
//        config.setMaxLifetime(1800000);

        dataSource = new HikariDataSource(config);
    }

    public static Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

    public static void closeDataSource() {
        if (dataSource != null && !dataSource.isClosed()) {
            dataSource.close();
        }
    }

    public static void main(String[] args) {
        try (Connection connection = DBConnector.getConnection()) {
            System.out.println("Successfully connected to the database.");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DBConnector.closeDataSource();
        }
    }
}
