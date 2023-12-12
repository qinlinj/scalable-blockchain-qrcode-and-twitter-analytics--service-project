package edu.cmu.twitter.utility;

import edu.cmu.twitter.model.Tweet;
import java.util.*;
import java.util.logging.Logger;

public class KeywordsScoreCalculator {
    private static final Logger LOGGER = Logger.getLogger(KeywordsScoreCalculator.class.getName());

    public Map<Long, Double> calculateKeywordsScores(
            Map<Long, List<Tweet>> userTweetsMap, String phrase, String hashtag) {

        Map<Long, Integer> matchCounts = new HashMap<>();
        Map<Long, Double> keywordsScores = new HashMap<>();

        for (Long otherUserId : userTweetsMap.keySet()) {
            int matchCount = 0;

            List<Tweet> otherUserTweets = userTweetsMap.get(otherUserId);
            for (Tweet tweet : otherUserTweets) {
                matchCount += calculateMatchCount(tweet, phrase, hashtag);
            }

            if (matchCount > 0) {
                keywordsScores.put(otherUserId, 1 + Math.log(1 + matchCount));
            } else {
                keywordsScores.put(otherUserId, 1.0);
            }
        }

        return keywordsScores;
    }

    private int calculateMatchCount(Tweet tweet, String phrase, String hashtag) {
        int count = 0;
        count += calculatePhraseMatchCount(tweet.getText(), phrase);
        count += calculateHashtagMatchCount(tweet.getHashtag(), hashtag);
        return count;
    }

    private int calculatePhraseMatchCount(String text, String phrase) {
        int count = 0;
        if (text != null) {
            int index = 0;
            while ((index = text.indexOf(phrase, index)) != -1) {
                count++;
                index++;
            }
        }
        return count;
    }

    private int calculateHashtagMatchCount(String hashtagsString, String targetHashtag) {
        if (hashtagsString == null) return 0;

        String[] hashtagsArray = hashtagsString.replaceAll("[\\[\\]\"]", "").split(",");
        return (int) Arrays.stream(hashtagsArray)
                .filter(hashtag -> hashtag.trim().equalsIgnoreCase(targetHashtag))
                .count();
    }
}
