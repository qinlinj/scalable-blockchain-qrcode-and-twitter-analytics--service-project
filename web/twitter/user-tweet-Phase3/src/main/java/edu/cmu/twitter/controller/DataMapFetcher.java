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
import java.util.*;
import java.util.Date;
import java.util.stream.Collectors;

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

        Connection connection = DBConnector.getConnection();

        String tweetsQuery = "SELECT * FROM Tweets WHERE user_id = ? OR in_reply_to_user_id = ? OR retweeted_status_user_id = ?";
        Set<Long> userIds = new HashSet<>();

        try (PreparedStatement pstmt = connection.prepareStatement(tweetsQuery)) {
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

                    Long userId = rs.getLong("user_id");
                    Long replyId = getLongOrNull(rs, "in_reply_to_user_id");
                    Long retweetId = getLongOrNull(rs, "retweeted_status_user_id");
//                    System.out.println("userId: " + userId + ", replyId: " + replyId + ", retweetId: " + retweetId);
                    if (userId != 0 && !userId.equals(requestId)) {
                        userIds.add(userId);
                    }
                    if (replyId != 0 && !replyId.equals(requestId)) {
                        userIds.add(replyId);
                    } else if (retweetId != 0 && !retweetId.equals(requestId)) {
                        userIds.add(retweetId);
                    }
                    if (userId.equals(requestId) && ((replyId != 0 && replyId.equals(requestId)) || (retweetId != 0 && retweetId.equals(requestId)))) {
                        userIds.add(requestId);
                    }
                }
            }
        }

        String usersQuery = "SELECT * FROM Users WHERE id IN (" +
                userIds.stream().map(String::valueOf).collect(Collectors.joining(", ")) + ")";


        try (PreparedStatement pstmt = connection.prepareStatement(usersQuery);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                User user = createUserFromResultSet(rs.getLong("id"), rs);
                userInfoMap.put(user.getUserId(), user);
            }
        }

        Map<String, Map<Long, List<Tweet>>> combinedMap = new HashMap<>();
        combinedMap.put("reply", replyTweetMap);
        combinedMap.put("retweet", retweetTweetMap);
        FetchResult dataset = new FetchResult(combinedMap, userInfoMap, requestUser);
        return dataset;
    }

    private User createUserFromResultSet(Long id, ResultSet rs) throws SQLException {
        String screenName = rs.getString("screen_name");
        String description = rs.getString("description");
        String hashtagCountsString = rs.getString("hashtag_counts");
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
            createdAt = sdf.parse(rs.getString("created_at"));
        } catch (Exception e) {
            createdAt = DEFAULT_DATE;
        }

        Long id = rs.getLong("id");
        Long userId = rs.getLong("user_id");
        String text = rs.getString("text");
        String hashtags = rs.getString("entities_hashtags");
        Long inReplyToUserId = getLongOrNull(rs, "in_reply_to_user_id");
        Long retweetedStatusUserId = getLongOrNull(rs, "retweeted_status_user_id");

        return new Tweet(createdAt, id, userId, text, hashtags, inReplyToUserId, retweetedStatusUserId, null);
    }

    private Long getLongOrNull(ResultSet rs, String column) throws SQLException {
        Long value = rs.getLong(column);
        return rs.wasNull() ? null : value;
    }
}
