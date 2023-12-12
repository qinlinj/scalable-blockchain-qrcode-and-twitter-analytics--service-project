

package edu.cmu.twitter.controller;

import edu.cmu.twitter.model.Tweet;
import edu.cmu.twitter.model.User;
import edu.cmu.twitter.utility.CalculateFinalScores;
import edu.cmu.twitter.utility.HashtagScoreCalculator;
import edu.cmu.twitter.utility.InteractionScoreCalculator;
import edu.cmu.twitter.utility.KeywordsScoreCalculator;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;

import java.util.*;
import java.util.stream.Collectors;

public class TwitterController extends AbstractVerticle {


    private final String TEAM_ID = "CloudQuest";
    private final String TEAM_AWS_ACCOUNT_ID = "146445828406";

    @Override
    public void start(Promise<Void> startPromise) throws Exception {
        Router router = Router.router(vertx);

        router.route().handler(BodyHandler.create());
        router.get("/twitter").handler(this::handleGetData);
        router.get("/").handler(this::handleIndex);

        vertx.createHttpServer()
                .requestHandler(router)
                .listen(8080, result -> {
                    if (result.succeeded()) {
                        startPromise.complete();
                    } else {
                        startPromise.fail(result.cause());
                    }
                });
    }

    private void handleGetData(RoutingContext context) {
        try {
            Long userId = Long.valueOf(context.request().getParam("user_id"));
            String type = context.request().getParam("type");
            String phrase = context.request().getParam("phrase");
            String hashtag = context.request().getParam("hashtag");

            if (userId == null || type == null || phrase == null || hashtag == null) {
                sendInvalidResponse(context);
                return;
            }

            vertx.executeBlocking(promise -> {
                try {
                    StringBuilder sb = new StringBuilder();
                    List<String> timeMeasurements = new ArrayList<>();
                    long startTime, endTime;

                    startTime = System.currentTimeMillis();
                    ReplyRetweetTweetMapFetcher replyRetweetMapFetcher = new ReplyRetweetTweetMapFetcher();
                    Map<String, Map<Long, List<Tweet>>> replyRetweetTweetMap = replyRetweetMapFetcher.fetchReplyAndRetweetMaps(userId);
//        replyRetweetTweetMap.forEach((tweetsType, map) -> {
//            System.out.println("Type: " + tweetsType);
//            map.forEach((key, value) -> {
//                System.out.println("Key: " + key);
//                value.forEach(System.out::println);
//            });
//        });
                    endTime = System.currentTimeMillis();
                    timeMeasurements.add("ReplyRetweetTweetMapFetcher: " + (endTime - startTime) + " ms");

                    // Generate Other User IDs
                    startTime = System.currentTimeMillis();
                    Set<Long> otherUserIds = generateOtherUserIdList(replyRetweetTweetMap);
                    endTime = System.currentTimeMillis();
                    timeMeasurements.add("GenerateOtherUserIdList: " + (endTime - startTime) + " ms");

                    // Fetch User Tweets
//        startTime = System.currentTimeMillis();
//        UserTweetsFetcher usersTweetsFetcher = new UserTweetsFetcher();
//        Map<Long, List<Tweet>> userTweetsMap = usersTweetsFetcher.fetchUserTweets(otherUserIds);
//        endTime = System.currentTimeMillis();
//        timeMeasurements.add("UserTweetsFetcher: " + (endTime - startTime) + " ms");

                    // Fetch User Infos
                    startTime = System.currentTimeMillis();
                    UserInfoFetcher usersInfoFetcher = new UserInfoFetcher();
                    Map<Long, User> userInfoMap = usersInfoFetcher.fetchUserInfos(otherUserIds);
                    User requestUser = usersInfoFetcher.fetchUserInfo(userId);
                    endTime = System.currentTimeMillis();
                    timeMeasurements.add("UserInfoFetcher: " + (endTime - startTime) + " ms");

                    // Fetch Tweets for Request User
//        startTime = System.currentTimeMillis();
//        UserTweetsFetcher requestUserTweetsFetcher = new UserTweetsFetcher();
//        Set<Tweet> requestUserTweets = requestUserTweetsFetcher.fetchTweetsForUser(user_id);
//        endTime = System.currentTimeMillis();
//        timeMeasurements.add("RequestUserTweetsFetcher: " + (endTime - startTime) + " ms");

                    // Calculate Interaction Scores
                    startTime = System.currentTimeMillis();
                    InteractionScoreCalculator interactionCalculator = new InteractionScoreCalculator();
                    Map<Long, Double> interactionScores = interactionCalculator.calculateInteractionScores(replyRetweetTweetMap);
                    System.out.println(interactionScores);
                    endTime = System.currentTimeMillis();
                    timeMeasurements.add("InteractionScoreCalculator: " + (endTime - startTime) + " ms");

                    // Calculate Hashtag Scores
                    startTime = System.currentTimeMillis();
                    HashtagScoreCalculator hashtagCalculator = new HashtagScoreCalculator();
                    Map<Long, Double> hashtagScores = hashtagCalculator.calculateHashtagScores(requestUser, userInfoMap);
                    System.out.println(hashtagScores);
                    endTime = System.currentTimeMillis();
                    timeMeasurements.add("HashtagScoreCalculator: " + (endTime - startTime) + " ms");

                    Map<Long, List<Tweet>> transformedMap = transformReplyRetweetMap(replyRetweetTweetMap, type);

                    // Calculate Keywords Scores
                    startTime = System.currentTimeMillis();
                    KeywordsScoreCalculator keywordsCalculator = new KeywordsScoreCalculator();
                    Map<Long, Double> keywordsScores = keywordsCalculator.calculateKeywordsScores(transformedMap, phrase, hashtag);
                    System.out.println(keywordsScores);
                    endTime = System.currentTimeMillis();
                    timeMeasurements.add("KeywordsScoreCalculator: " + (endTime - startTime) + " ms");

                    // Final score calculation
                    startTime = System.currentTimeMillis();
                    CalculateFinalScores finalScoreCalculator = new CalculateFinalScores();
                    Map<Long, User> unsortedUsers = finalScoreCalculator.calculateFinalScores(keywordsScores, interactionScores, hashtagScores, userInfoMap, transformedMap);
                    endTime = System.currentTimeMillis();
                    timeMeasurements.add("CalculateFinalScores: " + (endTime - startTime) + " ms");

                    // Append all timing results
                    for (String measurement : timeMeasurements) {
                        sb.append(measurement).append("\n");
                    }
                    printTimingResults(timeMeasurements);

                    String response = generateResponse(unsortedUsers);
                    promise.complete(response);
                } catch (Exception e) {
                    promise.fail(e);
                }
            }, res -> {
                if (res.succeeded()) {
                    context.response()
                            .putHeader("content-type", "text/plain")
                            .end((String) res.result());
                } else {
                    sendInvalidResponse(context);
                }
            });
        } catch (NumberFormatException e) {
            sendInvalidResponse(context);
        }
    }

    private void handleIndex(RoutingContext context) {
        context.response()
                .putHeader("content-type", "text/plain")
                .end("Healthy Twitter Service!");
    }

    private void sendInvalidResponse(RoutingContext context) {
        context.response()
                .putHeader("content-type", "text/plain")
                .end(TEAM_ID + "," + TEAM_AWS_ACCOUNT_ID + "\nINVALID");
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

    // Print timing results
    private void printTimingResults(List<String> timingResults) {
        System.out.println("Timing Results:");
        for (String result : timingResults) {
            System.out.println(result);
        }
    }

    public String generateResponse(Map<Long, User> userMap) {
        StringBuilder sb = new StringBuilder();
        sb.append(TEAM_ID).append(",").append(TEAM_AWS_ACCOUNT_ID).append("\n");

        List<User> sortedUsers = userMap.values().stream()
                .sorted(Comparator.comparing(User::getFinalScore, Comparator.reverseOrder())
                        .thenComparing(User::getUserId, Comparator.reverseOrder()))
                .collect(Collectors.toList());

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


    public Set<Long> generateOtherUserIdList(Map<String, Map<Long, List<Tweet>>> replyRetweetTweetMap) {
        Set<Long> otherUserIds = new HashSet<>();

        for (Map<Long, List<Tweet>> tweetMap : replyRetweetTweetMap.values()) {
            otherUserIds.addAll(tweetMap.keySet());
        }

        return otherUserIds;
    }


    private Map<Long, List<Tweet>> transformReplyRetweetMap(Map<String, Map<Long, List<Tweet>>> replyRetweetTweetMap, String type) {
        Map<Long, List<Tweet>> transformedMap = new HashMap<>();

        if ("both".equalsIgnoreCase(type)) {
            // Merge both reply and retweet tweets
            for (Map.Entry<String, Map<Long, List<Tweet>>> entry : replyRetweetTweetMap.entrySet()) {
                entry.getValue().forEach((userId, tweets) ->
                        transformedMap.computeIfAbsent(userId, k -> new ArrayList<>()).addAll(tweets));
            }
        } else {
            // Select either reply or retweet tweets
            Map<Long, List<Tweet>> selectedMap = replyRetweetTweetMap.get(type.toLowerCase());
            if (selectedMap != null) {
                selectedMap.forEach((userId, tweets) ->
                        transformedMap.computeIfAbsent(userId, k -> new ArrayList<>()).addAll(tweets));
            }
        }

        return transformedMap;
    }

}