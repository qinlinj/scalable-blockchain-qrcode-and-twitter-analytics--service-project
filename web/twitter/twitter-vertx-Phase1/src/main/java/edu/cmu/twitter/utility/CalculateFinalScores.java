package edu.cmu.twitter.utility;

import edu.cmu.twitter.model.Tweet;
import edu.cmu.twitter.model.User;


import java.util.*;
import java.util.stream.Collectors;

public class CalculateFinalScores {

    public List<User> calculateFinalScores(Long requestId, Set<Long> otherUserIds, Map<Long, Double> keywordsScores, Map<Long, Double> interactionScores, Map<Long, Double> hashtagScores, Map<Long, List<User>> userInfoMap, Map<Long, List<Tweet>> retweetTweetMap, Map<Long, List<Tweet>> replyTweetMap, String type) {
        Map<Long, User> userMap = new HashMap<>();

        for (Long userId : otherUserIds) {
            User latestUser = findLatestUserInfo(userInfoMap.get(userId));
            Tweet latestTweet = findLatestTweet(userId, retweetTweetMap, replyTweetMap, type);

            latestUser.setContactTweetText(latestTweet != null ? latestTweet.getText() : "Unknown");

            double interactionScore = interactionScores.getOrDefault(userId, 0.0);
            double hashtagScore = hashtagScores.getOrDefault(userId, 1.0);
            double keywordsScore = keywordsScores.getOrDefault(userId, 1.0);

            double finalScore = interactionScore * hashtagScore * keywordsScore;
            latestUser.setFinalScore(finalScore);

            userMap.put(userId, latestUser);
        }

        return userMap.values().stream()
                .filter(user -> user.getFinalScore() > 0)
                .sorted(Comparator.comparing(User::getFinalScore).reversed()
                        .thenComparing(User::getUserId).reversed())
                .collect(Collectors.toList());
    }

    private User findLatestUserInfo(List<User> users) {
        return users.stream().max(Comparator.comparing(User::getCreated_at)).orElse(null);
    }

    private Tweet findLatestTweet(Long userId, Map<Long, List<Tweet>> retweetTweetMap, Map<Long, List<Tweet>> replyTweetMap, String type) {
        List<Tweet> tweets;
        if ("retweet".equalsIgnoreCase(type)) {
            tweets = retweetTweetMap.getOrDefault(userId, Collections.emptyList());
        } else if ("reply".equalsIgnoreCase(type)) {
            tweets = replyTweetMap.getOrDefault(userId, Collections.emptyList());
        } else {
            tweets = new ArrayList<>();
            tweets.addAll(retweetTweetMap.getOrDefault(userId, Collections.emptyList()));
            tweets.addAll(replyTweetMap.getOrDefault(userId, Collections.emptyList()));
        }

        return tweets.stream().max(Comparator.comparing(Tweet::getCreated_at)).orElse(null);
    }

}
