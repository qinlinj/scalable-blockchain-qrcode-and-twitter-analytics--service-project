package edu.cmu.twitter.controller;

import edu.cmu.twitter.model.Tweet;
import edu.cmu.twitter.utility.DBConnector;

import java.sql.*;
import java.util.*;

import java.util.Date;

import java.text.SimpleDateFormat;

public class RetweetTweetMapFetcher {
    private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public Map<Long, List<Tweet>> fetchRetweetTweetMap(Long requestId) {
        Map<Long, List<Tweet>> replyTweetMap = new HashMap<>();

        String query1 = "SELECT created_at, id, user_id, text, entities_hashtags, retweeted_status_user_id, in_reply_to_user_id FROM Tweets WHERE retweeted_status_user_id = ?";
        String query2 = "SELECT created_at, retweeted_status_user_id, user_id, text, entities_hashtags, id, in_reply_to_user_id FROM Tweets WHERE user_id = ?";

        Connection connection = DBConnector.getConnection();

        try (connection) {
            // Execute the first query
            try (PreparedStatement pstmt = connection.prepareStatement(query1)) {
                pstmt.setLong(1, requestId);
                ResultSet rs = pstmt.executeQuery();
                while (rs.next()) {
                    Tweet tweet = createTweetFromResultSet(rs);
                    if (tweet.getRetweeted_status_user_id() != null && tweet.getRetweeted_status_user_id() != 0) {
                        replyTweetMap.computeIfAbsent(tweet.getUser_id(), k -> new ArrayList<>()).add(tweet);
                    }
                }
            }

            // Execute the second query
            try (PreparedStatement pstmt = connection.prepareStatement(query2)) {
                pstmt.setLong(1, requestId);
                ResultSet rs = pstmt.executeQuery();
                while (rs.next()) {
                    Tweet tweet = createTweetFromResultSet(rs);
                    if (tweet.getRetweeted_status_user_id() != null && tweet.getRetweeted_status_user_id() != 0) {
                        replyTweetMap.computeIfAbsent(tweet.getRetweeted_status_user_id(), k -> new ArrayList<>()).add(tweet);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return replyTweetMap;
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

        return new Tweet(createdAt, id, userId, text, hashtag, in_reply_to_user_id, retweeted_status_user_id);
    }

    public static void main(String[] args) {
        RetweetTweetMapFetcher fetcher = new RetweetTweetMapFetcher();
        Map<Long, List<Tweet>> retweetTweetMap = fetcher.fetchRetweetTweetMap(242381934L);

        // Print the replyTweetMap for debugging
        retweetTweetMap.forEach((key, value) -> {
            System.out.println("Key: " + key);
            value.forEach(System.out::println);
        });
    }
}

