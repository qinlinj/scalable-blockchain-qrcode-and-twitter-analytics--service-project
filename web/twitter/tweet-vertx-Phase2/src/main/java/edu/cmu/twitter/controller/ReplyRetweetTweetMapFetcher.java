package edu.cmu.twitter.controller;

import edu.cmu.twitter.model.Tweet;
import edu.cmu.twitter.utility.DBConnector;

import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReplyRetweetTweetMapFetcher {
    private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public Map<String, Map<Long, List<Tweet>>> fetchReplyAndRetweetMaps(Long requestId) throws SQLException {
        long startTime = System.currentTimeMillis();

        Map<Long, List<Tweet>> replyTweetMap = new HashMap<>();
        Map<Long, List<Tweet>> retweetTweetMap = new HashMap<>();
        Connection connection = DBConnector.getConnection();
        long afterConnectionTime = System.currentTimeMillis();
        System.out.println("Time to establish connection: " + (afterConnectionTime - startTime) + " ms");

        String query = "SELECT created_at, id, user_id, text, entities_hashtags, in_reply_to_user_id, retweeted_status_user_id " +
                "FROM Tweets WHERE user_id = ? OR in_reply_to_user_id = ? OR retweeted_status_user_id = ?";

        try (connection; PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setLong(1, requestId);
            pstmt.setLong(2, requestId);
            pstmt.setLong(3, requestId);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Tweet tweet = createTweetFromResultSet(rs);
                    if (tweet.getIn_reply_to_user_id() != null && tweet.getIn_reply_to_user_id() != 0) {
                        tweet.setType("reply");
                        if(tweet.getUser_id().equals(requestId)) {
                            replyTweetMap.computeIfAbsent(tweet.getIn_reply_to_user_id(), k -> new ArrayList<>()).add(tweet);
                        } else if (tweet.getIn_reply_to_user_id().equals(requestId)) {
                            replyTweetMap.computeIfAbsent(tweet.getUser_id(), k -> new ArrayList<>()).add(tweet);
                        } else if (tweet.getIn_reply_to_user_id().equals(tweet.getUser_id())){
                            replyTweetMap.computeIfAbsent(tweet.getIn_reply_to_user_id(), k -> new ArrayList<>()).add(tweet);
                        }
                    } else if (tweet.getRetweeted_status_user_id() != null && tweet.getRetweeted_status_user_id() != 0) {
                        tweet.setType("retweet");
                        if (tweet.getRetweeted_status_user_id() != null && tweet.getRetweeted_status_user_id() != 0) {
                            if(tweet.getUser_id().equals(requestId)) {
                                retweetTweetMap.computeIfAbsent(tweet.getRetweeted_status_user_id(), k -> new ArrayList<>()).add(tweet);
                            } else if (tweet.getRetweeted_status_user_id().equals(requestId)) {
                                retweetTweetMap.computeIfAbsent(tweet.getUser_id(), k -> new ArrayList<>()).add(tweet);
                            } else if (tweet.getRetweeted_status_user_id().equals(tweet.getUser_id())){
                                retweetTweetMap.computeIfAbsent(tweet.getRetweeted_status_user_id(), k -> new ArrayList<>()).add(tweet);
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        Map<String, Map<Long, List<Tweet>>> combinedMap = new HashMap<>();
        combinedMap.put("reply", replyTweetMap);
        combinedMap.put("retweet", retweetTweetMap);
        return combinedMap;
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
        Long in_reply_to_user_id = getLongOrNull(rs, "in_reply_to_user_id");
        Long retweeted_status_user_id = getLongOrNull(rs, "retweeted_status_user_id");

        return new Tweet(createdAt, id, userId, text, hashtag, in_reply_to_user_id, retweeted_status_user_id, null);
    }

    private Long getLongOrNull(ResultSet rs, String column) throws SQLException {
        Long value = rs.getLong(column);
        return rs.wasNull() ? null : value;
    }

//    public static void main(String[] args) {
//        ReplyRetweetTweetMapFetcher fetcher = new ReplyRetweetTweetMapFetcher();
//        Map<String, Map<Long, List<Tweet>>> combinedMaps = fetcher.fetchReplyAndRetweetMaps(294450894L);
//
//        // Print the maps for debugging
//        combinedMaps.forEach((type, map) -> {
//            System.out.println("Type: " + type);
//            map.forEach((key, value) -> {
//                System.out.println("Key: " + key);
//                value.forEach(System.out::println);
//            });
//        });
//    }
}


