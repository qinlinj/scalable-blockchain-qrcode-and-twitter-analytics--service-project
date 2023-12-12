package edu.cmu.twitter.controller;

import edu.cmu.twitter.model.Tweet;
import edu.cmu.twitter.model.Type;
import edu.cmu.twitter.model.User;
import edu.cmu.twitter.utility.CalculateFinalScores;
import edu.cmu.twitter.utility.HashtagScoreCalculator;
import edu.cmu.twitter.utility.InteractionScoreCalculator;
import edu.cmu.twitter.utility.KeywordsScoreCalculator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.*;

@RestController
public class TwitterController {


    private final String TEAM_ID = "CloudQuest";
    private final String TEAM_AWS_ACCOUNT_ID = "146445828406";
    /**
     * logger for Blockchain controller.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(
            TwitterController.class
    );

    @GetMapping("/twitter")
    public String getData(
            @RequestParam(name="user_id",required=false) Long user_id,
            @RequestParam(name="type",required=false) String type,
            @RequestParam(name="phrase",required=false) String phrase,
            @RequestParam(name="hashtag",required=false) String hashtag
    ) {
        StringBuilder sb = new StringBuilder();
        List<String> timeMeasurements = new ArrayList<>();

        long startTime, endTime;

        sb.append(TEAM_ID + "," + TEAM_AWS_ACCOUNT_ID + "\n");

        // check if the request query has all params
        startTime = System.currentTimeMillis();
        if (user_id == null || type == null || phrase == null || hashtag == null) {
            sb.append("INVALID");
            System.out.println("Invalid request: Missing parameters");
            endTime = System.currentTimeMillis();
            timeMeasurements.add("Parameter Check: " + (endTime - startTime) + " ms");
            return sb.toString();
        }
        endTime = System.currentTimeMillis();
        timeMeasurements.add("Parameter Check: " + (endTime - startTime) + " ms");

        // check if the type is valid
        startTime = System.currentTimeMillis();
        try {
            Type.valueOf(type.toLowerCase(Locale.ENGLISH));
        } catch (Exception e) {
            sb.append("INVALID");
            System.out.println("Invalid request: Incorrect 'type' parameter");
            endTime = System.currentTimeMillis();
            timeMeasurements.add("Type Validation: " + (endTime - startTime) + " ms");
            return sb.toString();
        }
        endTime = System.currentTimeMillis();
        timeMeasurements.add("Type Validation: " + (endTime - startTime) + " ms");

        // Fetch Retweet Tweet Map
        startTime = System.currentTimeMillis();
        RetweetTweetMapFetcher retweetFetcher = new RetweetTweetMapFetcher();
        Map<Long, List<Tweet>> retweetTweetMap = retweetFetcher.fetchRetweetTweetMap(user_id);
        endTime = System.currentTimeMillis();
        timeMeasurements.add("RetweetTweetMapFetcher: " + (endTime - startTime) + " ms");

        // Fetch Reply Tweet Map
        startTime = System.currentTimeMillis();
        ReplyTweetMapFetcher replyFetcher = new ReplyTweetMapFetcher();
        Map<Long, List<Tweet>> replyTweetMap = replyFetcher.fetchReplyTweetMap(user_id);
        endTime = System.currentTimeMillis();
        timeMeasurements.add("ReplyTweetMapFetcher: " + (endTime - startTime) + " ms");

        // Generate Other User IDs
        startTime = System.currentTimeMillis();
        Set<Long> otherUserIds = generateOtherUserIdList(retweetTweetMap, replyTweetMap);
        endTime = System.currentTimeMillis();
        timeMeasurements.add("GenerateOtherUserIdList: " + (endTime - startTime) + " ms");

        // Fetch User Tweets
        startTime = System.currentTimeMillis();
        UserTweetsFetcher usersTweetsFetcher = new UserTweetsFetcher();
        Map<Long, List<Tweet>> userTweetsMap = usersTweetsFetcher.fetchUserTweets(otherUserIds);
        endTime = System.currentTimeMillis();
        timeMeasurements.add("UserTweetsFetcher: " + (endTime - startTime) + " ms");

        // Fetch User Infos
        startTime = System.currentTimeMillis();
        UserInfoFetcher usersInfofetcher = new UserInfoFetcher();
        Map<Long, List<User>> userInfoMap = usersInfofetcher.fetchUserInfos(otherUserIds);
        endTime = System.currentTimeMillis();
        timeMeasurements.add("UserInfoFetcher: " + (endTime - startTime) + " ms");

        // Fetch Tweets for Request User
        startTime = System.currentTimeMillis();
        UserTweetsFetcher requestUserTweetsFetcher = new UserTweetsFetcher();
        Set<Tweet> requestUserTweets = requestUserTweetsFetcher.fetchTweetsForUser(user_id);
        endTime = System.currentTimeMillis();
        timeMeasurements.add("RequestUserTweetsFetcher: " + (endTime - startTime) + " ms");

        // Calculate Interaction Scores
        startTime = System.currentTimeMillis();
        InteractionScoreCalculator interactionCalculator = new InteractionScoreCalculator();
        Map<Long, Double> interactionScores = interactionCalculator.calculateInteractionScores(retweetTweetMap, replyTweetMap, otherUserIds);
        System.out.println(interactionScores);
        endTime = System.currentTimeMillis();
        timeMeasurements.add("InteractionScoreCalculator: " + (endTime - startTime) + " ms");

        // Calculate Hashtag Scores
        startTime = System.currentTimeMillis();
        HashtagScoreCalculator hashtagCalculator = new HashtagScoreCalculator();
        Map<Long, Double> hashtagScores = hashtagCalculator.calculateHashtagScores(requestUserTweets, userTweetsMap);
        System.out.println(hashtagScores);

        hashtagCalculator.printUserHashtags(2421636439L, userTweetsMap);
        endTime = System.currentTimeMillis();
        timeMeasurements.add("HashtagScoreCalculator: " + (endTime - startTime) + " ms");

        // Calculate Keywords Scores
        startTime = System.currentTimeMillis();
        KeywordsScoreCalculator keywordsCalculator = new KeywordsScoreCalculator();
        Map<Long, Double> keywordsScores = keywordsCalculator.calculateKeywordsScores(requestUserTweets, retweetTweetMap, type, phrase, hashtag);
        endTime = System.currentTimeMillis();
        timeMeasurements.add("KeywordsScoreCalculator: " + (endTime - startTime) + " ms");

        // Final score calculation
        startTime = System.currentTimeMillis();
        CalculateFinalScores finalScoreCalculator = new CalculateFinalScores();
        List<User> sortedUsers = finalScoreCalculator.calculateFinalScores(user_id, otherUserIds, keywordsScores, interactionScores, hashtagScores, userInfoMap, retweetTweetMap, replyTweetMap, type);
        endTime = System.currentTimeMillis();
        timeMeasurements.add("CalculateFinalScores: " + (endTime - startTime) + " ms");

        // Append all timing results
        for (String measurement : timeMeasurements) {
            sb.append(measurement).append("\n");
        }
        printTimingResults(timeMeasurements);

        return generateResponse(sortedUsers);
    }

    private static List<Long> extractPositiveScoreIds(Map<Long, Double> interactionScores) {
        //      ArrayList<Long> positiveScoreIds = (ArrayList<Long>) extractPositiveScoreIds(interactionScores);
        List<Long> positiveScoreIds = new ArrayList<>();
        interactionScores.forEach((id, score) -> {
            if (score > 0) {
                positiveScoreIds.add(id);
            }
        });
        return positiveScoreIds;
    }

    // Print timing results
    private void printTimingResults(List<String> timingResults) {
        System.out.println("Timing Results:");
        for (String result : timingResults) {
            System.out.println(result);
        }
    }

    public String generateResponse(List<User> sortedUsers) {
        StringBuilder sb = new StringBuilder();
        sb.append(TEAM_ID).append(",").append(TEAM_AWS_ACCOUNT_ID).append("\n");

        for (int i = 0; i < sortedUsers.size(); i++) {
            User user = sortedUsers.get(i);
            if (user.getFinalScore() > 0) {
                sb.append(user.getUserId()).append("\t")
                        .append(user.getScreenName()).append("\t")
                        .append(user.getDescription()).append("\t")
                        .append(user.getContactTweetText());

                // Append "\n" if this is not the last user
                if (i < sortedUsers.size() - 1) {
                    sb.append("\n");
                }
            }
        }

        return sb.toString();
    }

    public Set<Long> generateOtherUserIdList(Map<Long, List<Tweet>> retweetTweetMap, Map<Long, List<Tweet>> replyTweetMap) {
        Set<Long> otherUserIds = new HashSet<>();

        otherUserIds.addAll(retweetTweetMap.keySet());

        otherUserIds.addAll(replyTweetMap.keySet());

        return otherUserIds;
    }

    @GetMapping("/")
    public String index(){
        return "Healthy Twitter Service!";
    }

}