package edu.cmu.twitter.controller;

import edu.cmu.twitter.model.FetchResult;
import edu.cmu.twitter.model.Tweet;
import edu.cmu.twitter.model.User;
import edu.cmu.twitter.utility.DBConnector;
import org.json.JSONArray;
import org.json.JSONException;

import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DataMapFetcher {
    private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private static final Date DEFAULT_DATE;
    static {
        try {
            DEFAULT_DATE = sdf.parse("1970-01-01 00:00:00");
        } catch (ParseException e) {
            throw new RuntimeException("Failed to parse default date", e);
        }
    }
    public FetchResult fetchDataMaps(Long requestId) throws SQLException {
        Map<Long, List<Tweet>> replyTweetMap = new HashMap<>();
        Map<Long, List<Tweet>> retweetTweetMap = new HashMap<>();
        Map<Long, User> userInfoMap = new HashMap<>();
        User requestUser = new User(requestId, "", "", null, 0.0, new JSONArray());
        long startTime = System.currentTimeMillis();

        Connection connection = DBConnector.getConnection();
        long afterConnectionTime = System.currentTimeMillis();
        System.out.println("Time to establish connection: " + (afterConnectionTime - startTime) + " ms");

        String query = "SELECT * FROM CompleteTweets WHERE user_id = ? OR reply_id = ? OR retweet_id = ?";

        boolean isRequestUserSet = false;
        try (connection; PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setLong(1, requestId);
            pstmt.setLong(2, requestId);
            pstmt.setLong(3, requestId);

            try (ResultSet rs = pstmt.executeQuery()) {
                System.out.println(rs);
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

                    long id = rs.getLong("user_id");
                    long replyId = rs.getLong("reply_id");
                    long retweetId = rs.getLong("retweet_id");

                    boolean shouldCallCreateSenderUser = (id != requestId) || (id == requestId && (replyId == requestId || retweetId == requestId));
                    boolean shouldCallCreateContactUser = ((replyId != 0) && (replyId != requestId)) || ((retweetId != 0) && (retweetId != requestId));

                    if (shouldCallCreateSenderUser) {
                        User senderUser = createUserFromResultSet(id, rs, "sender");
                        userInfoMap.put(id, senderUser);
                    }

                    if (shouldCallCreateContactUser) {
                        Long contact_user_id = rs.getLong("reply_id") != 0 ? rs.getLong("reply_id") : rs.getLong("retweet_id");
                        User contactUser = createUserFromResultSet(contact_user_id, rs, "contact");
                        userInfoMap.put(contact_user_id, contactUser);
                    }

                    if (!isRequestUserSet && (requestUser.getDescription().equals("") || requestUser.getScreenName().equals("") || requestUser.getHashtagCounts().equals(null))) {
                        if (id == requestId) {
                            requestUser = createUserFromResultSet(requestId, rs, "sender");
                        } else if (replyId == requestId || replyId == retweetId) {
                            requestUser = createUserFromResultSet(requestId, rs, "contact");
                        }
                        isRequestUserSet = true;
                    }

                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        Map<String, Map<Long, List<Tweet>>> combinedMap = new HashMap<>();
        combinedMap.put("reply", replyTweetMap);
        combinedMap.put("retweet", retweetTweetMap);
        FetchResult dataset = new FetchResult(combinedMap, userInfoMap, requestUser);
        return dataset;
    }

    private User createUserFromResultSet(Long id, ResultSet rs, String userType) throws SQLException {
        String screenName = rs.getString(userType + "_screen_name");
        String description = rs.getString(userType + "_description");
        String hashtagCountsString = rs.getString(userType + "_hashtag_counts");
        JSONArray hashtagCounts;
        try {
            hashtagCounts = new JSONArray(hashtagCountsString);
        } catch (JSONException e) {
            hashtagCounts = new JSONArray();
        }

        return new User(id, screenName, description, null, 0.0, hashtagCounts);
    }

    private Tweet createTweetFromResultSet(ResultSet rs) throws SQLException {
        Date createdAt;
        try {
            createdAt = sdf.parse(rs.getString("tweet_created_at"));
        } catch (Exception e) {
            createdAt = DEFAULT_DATE;
        }

        Long id = rs.getLong("tweet_id");
        Long userId = rs.getLong("user_id");
        String text = rs.getString("text");
        String hashtag = rs.getString("entities_hashtags");
        Long in_reply_to_user_id = getLongOrNull(rs, "reply_id");
        Long retweeted_status_user_id = getLongOrNull(rs, "retweet_id");

        return new Tweet(createdAt, id, userId, text, hashtag, in_reply_to_user_id, retweeted_status_user_id, null);
    }

    private Long getLongOrNull(ResultSet rs, String column) throws SQLException {
        Long value = rs.getLong(column);
        return rs.wasNull() ? null : value;
    }
}


