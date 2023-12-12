package edu.cmu.twitter.utility;

import edu.cmu.twitter.model.Tweet;
import edu.cmu.twitter.model.User;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

public class CalculateFinalScores {

    public Map<Long, User> calculateFinalScores(Map<Long, Double> keywordsScores,
                                           Map<Long, Double> interactionScores,
                                           Map<Long, Double> hashtagScores,
                                           Map<Long, User> userInfoMap,
                                           Map<Long, List<Tweet>> userTweetsMap) {
        Map<Long, User> userMap = new HashMap<>();

        for (Map.Entry<Long, User> entry : userInfoMap.entrySet()) {
            Long userId = entry.getKey();
            User latestUser = entry.getValue();

            if (latestUser == null) {
                continue; // Skip if user info is not available
            }

            Tweet latestTweet = findLatestTweet(userId, userTweetsMap);
            String contactTweetText = (latestTweet != null) ? latestTweet.getText() : "Unknown";
            latestUser.setContactTweetText(contactTweetText);

            if ("Unknown".equals(contactTweetText)) {
                continue; // Skip if the latest tweet text is "Unknown"
            }
            double interactionScore = interactionScores.getOrDefault(userId, 0.0);
            double hashtagScore = hashtagScores.getOrDefault(userId, 1.0);
            double keywordsScore = keywordsScores.getOrDefault(userId, 1.0);

            double finalScore = interactionScore * hashtagScore * keywordsScore;
            finalScore = round(finalScore, 5);
            latestUser.setFinalScore(finalScore);

            userMap.put(userId, latestUser);
        }

        return userMap;
    }

    private Tweet findLatestTweet(Long userId, Map<Long, List<Tweet>> userTweetsMap) {
        List<Tweet> tweets = userTweetsMap.getOrDefault(userId, Collections.emptyList());

        return tweets.stream()
                .max(Comparator.comparing(Tweet::getCreated_at)
                        .thenComparing(Tweet::getId))
                .orElse(null);
    }

    private double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        BigDecimal bd = BigDecimal.valueOf(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

}

