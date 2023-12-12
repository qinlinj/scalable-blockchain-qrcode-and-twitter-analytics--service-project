package edu.cmu.twitter.controller;

import edu.cmu.twitter.model.User;
import edu.cmu.twitter.utility.DBConnector;
import org.json.JSONArray;
import org.json.JSONException;

import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Date;

public class UserInfoFetcher {

    public Map<Long, User> getUsersInfo(Set<Long> userIds) throws SQLException {
        Connection connection = DBConnector.getConnection();
        String baseQuery = "SELECT id, screen_name, description, created_at, hashtag_counts FROM Users WHERE id IN (";
        StringJoiner joiner = new StringJoiner(",");
        for (Long userId : userIds) {
            joiner.add("?");
        }
        String query = baseQuery + joiner.toString() + ")";
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date defaultDate = null;
        try {
            defaultDate = dateFormat.parse("1970-01-01 00:00:00");
        } catch (ParseException e) {
            e.printStackTrace();
        }

        Map<Long, User> usersMap = new HashMap<>();
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            int index = 1;
            for (Long userId : userIds) {
                pstmt.setLong(index++, userId);
            }
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Long id = rs.getLong("id");
                String screenName = rs.getString("screen_name");
                String description = rs.getString("description");
                String hashtagCountsString = rs.getString("hashtag_counts");
                JSONArray hashtagCounts;
                try {
                    hashtagCounts = new JSONArray(hashtagCountsString);
                } catch (JSONException e) {
                    hashtagCounts = new JSONArray();
                }
                Date created_at = rs.getDate("created_at");
                if (rs.wasNull()) {
                    created_at = defaultDate;
                }

                User user = new User(created_at, id, screenName, description, null, 0.0, hashtagCounts);
                usersMap.put(id, user);
            }
        }

        return usersMap;
    }


    public Map<Long, User> fetchUserInfos(Set<Long> userIds) throws SQLException {
        Map<Long, User> userInfoMap = new HashMap<>();
        Connection connection = DBConnector.getConnection();
        try (connection) {
            for (Long userId : userIds) {
                User userInfo = getUserInfo(connection, userId);
                userInfoMap.put(userId, userInfo);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return userInfoMap;
    }

    public User fetchUserInfo(Long userId) throws SQLException {
        Map<Long, User> userInfoMap = new HashMap<>();
        Connection connection = DBConnector.getConnection();
        User userInfo;
        try (connection) {
            userInfo = getUserInfo(connection, userId);
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
        return userInfo;
    }

    private User getUserInfo(Connection connection, Long userId) throws SQLException {
        String query = "SELECT id, screen_name, description, created_at, hashtag_counts FROM Users WHERE id = ?";
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date defaultDate = null;
        try {
            defaultDate = dateFormat.parse("1970-01-01 00:00:00");
        } catch (ParseException e) {
            e.printStackTrace();
        }

        User latestUser = null;
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setLong(1, userId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Long id = rs.getLong("id");
                String screenName = rs.getString("screen_name");
                String description = rs.getString("description");
                String hashtagCountsString = rs.getString("hashtag_counts");
                JSONArray hashtagCounts;
                try {
                    hashtagCounts = new JSONArray(hashtagCountsString);
                } catch (JSONException e) {
                    hashtagCounts = new JSONArray();
                }
                Date created_at = rs.getDate("created_at");
                if (rs.wasNull()) {
                    created_at = defaultDate;
                }

                User user = new User(created_at, id, screenName, description, null, 0.0, hashtagCounts);

                if (latestUser == null || user.getCreated_at().after(latestUser.getCreated_at())) {
                    latestUser = user;
                }
            }
        }

        return latestUser != null ? latestUser : new User(defaultDate, userId, "", "", null, 0.0, new JSONArray());
    }
}
