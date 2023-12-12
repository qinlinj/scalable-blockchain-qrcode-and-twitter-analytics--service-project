package org.example;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileReader;


import java.sql.*;
import java.util.HashSet;
import java.util.Set;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


public class UpdateMysqlDB {

    private static final String JDBC_URL = "jdbc:mysql://localhost:3306/twitter_db?useSSL=false&allowPublicKeyRetrieval=true";
    private static final String JDBC_USER = "root";
    private static final String JDBC_PASSWORD = "Cc88888888@";
    private static final Set<String> restrictedHashtags = new HashSet<>();


    public static void main(String[] args) {
        loadRestrictedHashtags();  // Load restricted hashtags
        try (Connection connection = DriverManager.getConnection(JDBC_URL, JDBC_USER, JDBC_PASSWORD);
             BufferedReader br = new BufferedReader(new FileReader("./part-r-00000-etl"))) {

            String line;
            while ((line = br.readLine()) != null) {
                JSONObject jsonObject = new JSONObject(line);

                // Insert/update User
                JSONObject userObject = jsonObject.getJSONObject("user");
                insertOrUpdateUser(connection, userObject);

                // Insert/update Tweet
                insertOrUpdateTweet(connection, jsonObject);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static String convertToMySqlDate(String twitterDate) {
        try {
            // Twitter date format: "EEE MMM dd HH:mm:ss ZZZZZ yyyy"
            SimpleDateFormat twitterFormat = new SimpleDateFormat("EEE MMM dd HH:mm:ss Z yyyy", Locale.ENGLISH);
            SimpleDateFormat mySqlFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

            Date date = twitterFormat.parse(twitterDate);
            return mySqlFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }


    private static void loadRestrictedHashtags() {
        try (BufferedReader br = new BufferedReader(new FileReader("./popular_hashtags.txt"))) {
            String line;
            while ((line = br.readLine()) != null) {
                restrictedHashtags.add(line.trim().toLowerCase());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void insertOrUpdateUser(Connection connection, JSONObject userObject) throws Exception {
        String sql = "INSERT INTO Users (user_id, screen_name, name, description, created_at) VALUES (?, ?, ?, ?, ?) ON DUPLICATE KEY UPDATE screen_name=?, name=?, description=?, created_at=?";
        PreparedStatement stmt = connection.prepareStatement(sql);
        stmt.setString(1, userObject.getString("id_str"));
        stmt.setString(2, userObject.getString("screen_name"));
        stmt.setString(3, userObject.optString("name"));
        stmt.setString(4, userObject.optString("description", ""));
        stmt.setString(5, convertToMySqlDate(userObject.getString("created_at")));
        stmt.setString(6, userObject.getString("screen_name"));
        stmt.setString(7, userObject.optString("name"));
        stmt.setString(8, userObject.optString("description", ""));
        stmt.setString(9, convertToMySqlDate(userObject.getString("created_at")));
        stmt.executeUpdate();
    }

    private static void insertOrUpdateTweet(Connection connection, JSONObject jsonObject) throws Exception {
        String sql = "INSERT INTO Tweets (tweet_id, user_id, text, hashtags, created_at, in_reply_to_user_id, retweet_status_user_id) VALUES (?, ?, ?, ?, ?, ?, ?) ON DUPLICATE KEY UPDATE user_id=?, text=?, hashtags=?, created_at=?, in_reply_to_user_id=?, retweet_status_user_id=?";
        PreparedStatement stmt = connection.prepareStatement(sql);
        stmt.setString(1, jsonObject.getString("id_str"));
        stmt.setString(2, jsonObject.getJSONObject("user").getString("id_str"));
        stmt.setString(3, jsonObject.getString("text"));

        // Handle hashtags
        JSONArray hashtagsArray = jsonObject.getJSONObject("entities").getJSONArray("hashtags");
        StringBuilder hashtagsStringBuilder = new StringBuilder();
        for (int i = 0; i < hashtagsArray.length(); i++) {
            String hashtagText = hashtagsArray.getJSONObject(i).getString("text").toLowerCase();
            if (!restrictedHashtags.contains(hashtagText)) {
                hashtagsStringBuilder.append(hashtagText);
                if (i < hashtagsArray.length() - 1) {
                    hashtagsStringBuilder.append(",");  // Separate hashtags with commas
                }
            }
        }
        stmt.setString(4, hashtagsStringBuilder.toString());  // Set hashtags as a comma-separated string

        stmt.setString(5, convertToMySqlDate(jsonObject.getString("created_at")));
        stmt.setString(6, jsonObject.optString("in_reply_to_user_id_str", null));
        if (jsonObject.has("retweeted_status")) {
            stmt.setString(7, jsonObject.getJSONObject("retweeted_status").getJSONObject("user").getString("id_str"));
        } else {
            stmt.setNull(7, Types.VARCHAR);
        }
        stmt.setString(8, jsonObject.getJSONObject("user").getString("id_str"));
        stmt.setString(9, jsonObject.getString("text"));
        stmt.setString(10, hashtagsStringBuilder.toString());  // Update hashtags as a comma-separated string
        stmt.setString(11, convertToMySqlDate(jsonObject.getString("created_at")));
        stmt.setString(12, jsonObject.optString("in_reply_to_user_id_str", null));
        if (jsonObject.has("retweeted_status")) {
            stmt.setString(13, jsonObject.getJSONObject("retweeted_status").getJSONObject("user").getString("id_str"));
        } else {
            stmt.setNull(13, Types.VARCHAR);
        }
        stmt.executeUpdate();
    }
}
