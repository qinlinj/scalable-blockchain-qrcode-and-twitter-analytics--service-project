package edu.cmu.twitter.utility;

import edu.cmu.twitter.model.Tweet;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InteractionScoreCalculator {

    public Map<Long, Double> calculateInteractionScores(Map<String, Map<Long, List<Tweet>>> combinedMaps) {
        Map<Long, Double> interactionScores = new HashMap<>();

        Map<Long, List<Tweet>> retweetTweetMap = combinedMaps.getOrDefault("retweet", Collections.emptyMap());
        Map<Long, List<Tweet>> replyTweetMap = combinedMaps.getOrDefault("reply", Collections.emptyMap());

        // Combine user IDs from both maps
        combinedMaps.forEach((type, map) -> map.keySet().forEach(userId -> {
            int retweetCount = retweetTweetMap.getOrDefault(userId, Collections.emptyList()).size();
            int replyCount = replyTweetMap.getOrDefault(userId, Collections.emptyList()).size();

            double score = calculateScore(replyCount, retweetCount);
            interactionScores.put(userId, score);
        }));

        return interactionScores;
    }

    private double calculateScore(int replyCount, int retweetCount) {
        return Math.log(1 + 2 * replyCount + retweetCount);
    }
}



