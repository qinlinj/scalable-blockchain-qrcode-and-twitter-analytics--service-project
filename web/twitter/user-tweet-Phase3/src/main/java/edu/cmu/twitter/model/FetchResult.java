package edu.cmu.twitter.model;

import java.util.List;
import java.util.Map;

public class FetchResult {
    private Map<String, Map<Long, List<Tweet>>> tweetMap;
    private Map<Long, User> userMap;
    private User targetUser;

    public Map<String, Map<Long, List<Tweet>>> getTweetMap() {
        return tweetMap;
    }

    public void setTweetMap(Map<String, Map<Long, List<Tweet>>> tweetMap) {
        this.tweetMap = tweetMap;
    }

    public Map<Long, User> getUserMap() {
        return userMap;
    }

    public void setUserMap(Map<Long, User> userMap) {
        this.userMap = userMap;
    }

    public User getTargetUser() {
        return targetUser;
    }

    public void setTargetUser(User targetUser) {
        this.targetUser = targetUser;
    }

    public FetchResult(Map<String, Map<Long, List<Tweet>>> tweetMap, Map<Long, User> userMap, User targetUser) {
        this.tweetMap = tweetMap;
        this.userMap = userMap;
        this.targetUser = targetUser;
    }

    @Override
    public String toString() {
        return "FetchResult{" +
                "tweetMap=" + tweetMap +
                ", userMap=" + userMap +
                ", targetUser=" + targetUser +
                '}';
    }
}
