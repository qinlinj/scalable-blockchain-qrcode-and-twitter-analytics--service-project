package edu.cmu.twitter.controller;
import edu.cmu.twitter.model.Tweet;
import edu.cmu.twitter.utility.DBConnector;

import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Date;

public class UserTweetsFetcher {
    private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public Map<Long, List<Tweet>> fetchUserTweets(Set<Long> userIds) throws SQLException {
        Map<Long, List<Tweet>> userTweetsMap = new HashMap<>();

        Connection connection = DBConnector.getConnection();

        try (connection) {
            for (Long userId : userIds) {
                List<Tweet> tweets = getUserTweets(connection, userId);
                userTweetsMap.put(userId, tweets);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return userTweetsMap;
    }

    public Set<Tweet> fetchTweetsForUser(Long userId) throws SQLException {
        Set<Tweet> tweets = new HashSet<>();

        Connection connection = DBConnector.getConnection();

        try (connection) {
            tweets.addAll(getUserTweets(connection, userId));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return tweets;
    }

    private List<Tweet> getUserTweets(Connection connection, Long userId) throws SQLException {
        List<Tweet> tweets = new ArrayList<>();
        String query = "SELECT created_at, id, user_id, text, entities_hashtags, in_reply_to_user_id, retweeted_status_user_id FROM Tweets WHERE user_id = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setLong(1, userId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Tweet tweet = createTweetFromResultSet(rs);
                tweets.add(tweet);
            }
        }

        return tweets;
    }

    private Tweet createTweetFromResultSet(ResultSet rs) throws SQLException {
        Date createdAt = null;
        try {
            createdAt = sdf.parse(rs.getString("created_at"));
        } catch (Exception e) {
            e.printStackTrace();
        }

        Long id = rs.getLong("id");
        Long userId = rs.getLong("user_id");
        String text = rs.getString("text");
        String hashtag = rs.getString("entities_hashtags");
        Long in_reply_to_user_id = rs.getLong("in_reply_to_user_id");
        if (rs.wasNull()) {
            in_reply_to_user_id = null;
        }

        Long retweeted_status_user_id = rs.getLong("retweeted_status_user_id");
        if (rs.wasNull()) {
            retweeted_status_user_id = null;
        }
        String type = "";
        return new Tweet(createdAt, id, userId, text, hashtag, in_reply_to_user_id, retweeted_status_user_id, type);
    }

//    public static void main(String[] args) {
////        UserTweetsFetcher fetcher = new UserTweetsFetcher();
////        Set<Long> otherUserIds = new HashSet<>();
////        otherUserIds.add(526986111L);
////        otherUserIds.add(558780329L);
////        Map<Long, List<Tweet>> userTweetsMap = fetcher.fetchUserTweets(otherUserIds);
////
////        // Print the replyTweetMap for debugging
////        userTweetsMap.forEach((key, value) -> {
////            System.out.println("Key: " + key);
////            value.forEach(System.out::println);
////        });
//
//        UserTweetsFetcher fetcher = new UserTweetsFetcher();
//        Long userId = 526986111L;
//        Set<Tweet> tweets = fetcher.fetchTweetsForUser(userId);
//
//        tweets.forEach(System.out::println);
//    }
}
