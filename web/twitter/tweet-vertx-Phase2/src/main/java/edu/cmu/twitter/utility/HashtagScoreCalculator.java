package edu.cmu.twitter.utility;

import edu.cmu.twitter.model.User;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class HashtagScoreCalculator {

    public Map<Long, Double> calculateHashtagScores(User requestUser, Map<Long, User> otherUsers) {
        Map<Long, Double> hashtagScores = new HashMap<>();
        Map<String, Integer> requestHashtags = extractHashtagsFromUser(requestUser);

        for (Map.Entry<Long, User> entry : otherUsers.entrySet()) {
            Long otherUserId = entry.getKey();
            Map<String, Integer> otherUserHashtags = extractHashtagsFromUser(entry.getValue());

            int sameTagCount = calculateSameTagCount(requestHashtags, otherUserHashtags);
            double hashtagScore = sameTagCount > 10 ? 1 + Math.log(1 + sameTagCount - 10) : 1;

            hashtagScores.put(otherUserId, hashtagScore);
        }

        return hashtagScores;
    }

    private int calculateSameTagCount(Map<String, Integer> hashtags1, Map<String, Integer> hashtags2) {
        int count = 0;
        for (String hashtag : hashtags1.keySet()) {
            if (hashtags2.containsKey(hashtag)) {
                count += hashtags1.get(hashtag) + hashtags2.get(hashtag);
            }
        }
        return count;
    }

    private Map<String, Integer> extractHashtagsFromUser(User user) {
        Map<String, Integer> hashtags = new HashMap<>();
        JSONArray hashtagCounts = user.getHashtagCounts();
        for (int i = 0; i < hashtagCounts.length(); i++) {
            JSONObject hashtagCount = hashtagCounts.getJSONObject(i);
            String hashtag = hashtagCount.getString("hashtag");
            int count = hashtagCount.getInt("count");
            hashtags.put(hashtag, hashtags.getOrDefault(hashtag, 0) + count);
        }
        return hashtags;
    }
}
