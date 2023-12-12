package edu.cmu.twitter.controller;

import edu.cmu.twitter.model.Tweet;
import edu.cmu.twitter.utility.DBConnector;

import java.sql.*;
import java.util.*;

import java.util.Date;

import java.text.SimpleDateFormat;

public class ReplyTweetMapFetcher {
    private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public Map<Long, List<Tweet>> fetchReplyTweetMap(Long requestId) {
        Map<Long, List<Tweet>> replyTweetMap = new HashMap<>();
        Connection connection = DBConnector.getConnection();
        String query1 = "SELECT created_at, id, user_id, text, entities_hashtags, in_reply_to_user_id, retweeted_status_user_id FROM Tweets WHERE in_reply_to_user_id = ?";
        String query2 = "SELECT created_at, in_reply_to_user_id, user_id, text, entities_hashtags, id, retweeted_status_user_id FROM Tweets WHERE user_id = ?";

        try (connection) {
            // Execute the first query
            try (PreparedStatement pstmt = connection.prepareStatement(query1)) {
                pstmt.setLong(1, requestId);
                ResultSet rs = pstmt.executeQuery();
                while (rs.next()) {
                    Tweet tweet = createTweetFromResultSet(rs);
                    if (tweet.getIn_reply_to_user_id() != null && tweet.getIn_reply_to_user_id() != 0) {
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
                    if (tweet.getIn_reply_to_user_id() != null && tweet.getIn_reply_to_user_id() != 0) {
                        replyTweetMap.computeIfAbsent(tweet.getIn_reply_to_user_id(), k -> new ArrayList<>()).add(tweet);
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
        ReplyTweetMapFetcher fetcher = new ReplyTweetMapFetcher();
        Map<Long, List<Tweet>> replyTweetMap = fetcher.fetchReplyTweetMap(294450894L);

        // Print the replyTweetMap for debugging
        replyTweetMap.forEach((key, value) -> {
            System.out.println("Key: " + key);
            value.forEach(System.out::println);
        });
    }
}

