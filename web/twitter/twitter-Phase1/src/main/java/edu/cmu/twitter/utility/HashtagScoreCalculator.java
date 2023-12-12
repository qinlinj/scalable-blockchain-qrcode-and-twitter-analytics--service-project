package edu.cmu.twitter.utility;

import edu.cmu.twitter.model.Tweet;

import java.io.*;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class HashtagScoreCalculator {
    private static final Logger LOGGER = Logger.getLogger(HashtagScoreCalculator.class.getName());
//    private static final Set<String> restrictedHashtags = loadRestrictedHashtags();

    public Map<Long, Double> calculateHashtagScores(Set<Tweet> requestUserTweets, Map<Long, List<Tweet>> userTweetsMap) {
        Map<Long, Double> hashtagScores = new HashMap<>();
        Map<String, Integer> requestHashtags = extractHashtags(requestUserTweets);

        for (Map.Entry<Long, List<Tweet>> entry : userTweetsMap.entrySet()) {
            Long otherUserId = entry.getKey();
            Map<String, Integer> otherUserHashtags = extractHashtags(new HashSet<>(entry.getValue()));

            int sameTagCount = calculateSameTagCount(requestHashtags, otherUserId, otherUserHashtags);
            double hashtagScore = sameTagCount > 10 ? 1 + Math.log(1 + sameTagCount - 10) : 1;

            hashtagScores.put(otherUserId, hashtagScore);
        }

        return hashtagScores;
    }

    private int calculateSameTagCount(Map<String, Integer> hashtags1, Long userId2, Map<String, Integer> hashtags2) {
        int count = 0;
        for (String hashtag : hashtags1.keySet()) {
            if (hashtags2.containsKey(hashtag)) {
                int hashtagCount = hashtags1.get(hashtag) + hashtags2.get(hashtag);
                System.out.println("UserID1: " + ", Hashtag: " + hashtag + ", UserID2: " + userId2 + ", Hashtag: " + hashtag + ", Count: " + hashtagCount);
                count += hashtagCount;
            }
        }
        return count;
    }


    private Map<String, Integer> extractHashtags(Set<Tweet> tweets) {
        Map<String, Integer> hashtags = new HashMap<>();
        for (Tweet tweet : tweets) {
            if (tweet.getHashtag() != null && !tweet.getHashtag().isEmpty()) {
                for (String hashtag : tweet.getHashtag().split(",")) {
                    hashtag = hashtag.replace("[", "").replace("]", "");
                    hashtag = hashtag.toLowerCase(Locale.ENGLISH).trim();
                    hashtags.put(hashtag, hashtags.getOrDefault(hashtag, 0) + 1);
                }
            }
        }
        return hashtags;
    }

    public void printUserHashtags(Long userId, Map<Long, List<Tweet>> userTweetsMap) {
        if (userTweetsMap.containsKey(userId)) {
            Set<Tweet> userTweets = new HashSet<>(userTweetsMap.get(userId));
            Map<String, Integer> userHashtags = extractHashtags(userTweets);

            System.out.println("User ID: " + userId + " has used the following hashtags:");
            for (Map.Entry<String, Integer> entry : userHashtags.entrySet()) {
                System.out.println(entry.getKey() + ": " + entry.getValue());
            }
        } else {
            System.out.println("User ID: " + userId + " not found in the user tweets map.");
        }
    }

//    private static Set<String> loadRestrictedHashtags() {
//        Set<String> restricted = new HashSet<>();
//        String resourcePath = "/popular_hashtags.txt";
//
//        try (InputStream is = HashtagScoreCalculator.class.getResourceAsStream(resourcePath);
//             BufferedReader br = new BufferedReader(new InputStreamReader(is))) {
//            String line;
//            while ((line = br.readLine()) != null) {
//                restricted.add(line.trim().toLowerCase());
//            }
//        } catch (IOException | NullPointerException e) {
//            LOGGER.log(Level.SEVERE, "Error loading restricted hashtags", e);
//        }
//
//        return restricted;
//    }

}