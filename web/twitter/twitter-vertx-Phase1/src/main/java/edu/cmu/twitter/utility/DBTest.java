package edu.cmu.twitter.utility;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBTest {

    private static final String DB_URL = "";
    private static final String USER = "";
    private static final String PASSWORD = "";


    public static void main(String[] args) {
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(DB_URL, USER, PASSWORD);
            if (conn != null) {
                System.out.println("Successfully connected to the database!");
            }
        } catch (SQLException e) {
            System.out.println("An error occurred while connecting to the database.");
            e.printStackTrace();
        } finally {
            try {
                if (conn != null && !conn.isClosed()) {
                    conn.close();
                    System.out.println("The database connection was closed successfully.");
                }
            } catch (SQLException ex) {
                System.out.println("An error occurred while closing the database connection.");
                ex.printStackTrace();
            }
        }
    }
}
