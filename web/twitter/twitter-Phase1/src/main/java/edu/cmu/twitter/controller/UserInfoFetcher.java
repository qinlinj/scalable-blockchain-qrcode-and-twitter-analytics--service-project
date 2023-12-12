package edu.cmu.twitter.controller;

import edu.cmu.twitter.model.User;
import edu.cmu.twitter.utility.DBConnector;

import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Date;

public class UserInfoFetcher {

    public Map<Long, List<User>> fetchUserInfos(Set<Long> userIds) {
        Map<Long, List<User>> userInfoMap = new HashMap<>();
        Connection connection = DBConnector.getConnection();
        try (connection) {
            for (Long userId : userIds) {
                List<User> userInfo = getUserInfo(connection, userId);
                userInfoMap.put(userId, userInfo);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return userInfoMap;
    }

    private List<User> getUserInfo(Connection connection, Long userId) throws SQLException {
        List<User> userInfo = new ArrayList<>();
        String query = "SELECT id, screen_name, description, created_at FROM Users WHERE id = ?";
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date defaultDate = null;
        try {
            defaultDate = dateFormat.parse("1970-01-01 00:00:00");
        } catch (ParseException e) {
            e.printStackTrace();
        }
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setLong(1, userId);
            ResultSet rs = pstmt.executeQuery();

            boolean found = false;
            while (rs.next()) {
                found = true;
                Long id = rs.getLong("id");
                String screenName = rs.getString("screen_name");
                String description = rs.getString("description");
                Date created_at = rs.getDate("created_at");
                if (rs.wasNull()) {
                    created_at = defaultDate;
                }

                userInfo.add(new User(created_at, id, screenName, description, null, 0.0));
            }

            if (!found) {
                userInfo.add(new User(defaultDate, userId, null, null, null, 0.0));
            }
        }

        return userInfo;
    }

    public static void main(String[] args) {
        UserInfoFetcher fetcher = new UserInfoFetcher();
        Set<Long> userIds = new HashSet<>();
        userIds.add(1881405254L);
        userIds.add(2419074925L);

        Map<Long, List<User>> userInfoMap = fetcher.fetchUserInfos(userIds);

        userInfoMap.forEach((key, value) -> {
            System.out.println("Key: " + key);
            value.forEach(System.out::println);
        });
    }
}
