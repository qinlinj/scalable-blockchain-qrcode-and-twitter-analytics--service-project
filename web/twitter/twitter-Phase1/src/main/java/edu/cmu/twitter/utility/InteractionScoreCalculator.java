package edu.cmu.twitter.utility;

import edu.cmu.twitter.model.Tweet;

import java.util.*;

public class InteractionScoreCalculator {

    public Map<Long, Double> calculateInteractionScores(Map<Long, List<Tweet>> retweetTweetMap, Map<Long, List<Tweet>> replyTweetMap, Set<Long> otherUserIds) {
        Map<Long, Double> interactionScores = new HashMap<>();

        otherUserIds.addAll(replyTweetMap.keySet());

        for (Long userId : otherUserIds) {
            int retweetCount = retweetTweetMap.getOrDefault(userId, Collections.emptyList()).size();
            int replyCount = replyTweetMap.getOrDefault(userId, Collections.emptyList()).size();

            double score = calculateScore(replyCount, retweetCount);
            interactionScores.put(userId, score);
        }

        return interactionScores;
    }

    private double calculateScore(int replyCount, int retweetCount) {
        return Math.log(1 + 2 * replyCount + retweetCount);
    }
}


