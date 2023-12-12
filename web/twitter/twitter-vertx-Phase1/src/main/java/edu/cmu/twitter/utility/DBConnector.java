package edu.cmu.twitter.utility;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnector {
    private static final String DB_URL = "";
    private static final String USER = "";
    private static final String PASSWORD = "";

    public static Connection getConnection() {
        try {
            return DriverManager.getConnection(DB_URL, USER, PASSWORD);
        } catch (SQLException e) {
            throw new RuntimeException("Failed to connect to the database", e);
        }
    }

    public static void main(String[] args) {
        try {
            Connection connection = DBConnector.getConnection();
            System.out.println("Successfully connected to the database.");

            connection.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
