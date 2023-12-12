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

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
                    RetweetTweetMapFetcher retweetFetcher = new RetweetTweetMapFetcher();
                    Map<Long, List<Tweet>> retweetTweetMap = retweetFetcher.fetchRetweetTweetMap(userId);

                    ReplyTweetMapFetcher replyFetcher = new ReplyTweetMapFetcher();
                    Map<Long, List<Tweet>> replyTweetMap = replyFetcher.fetchReplyTweetMap(userId);

                    Set<Long> otherUserIds = generateOtherUserIdList(retweetTweetMap, replyTweetMap);

                    UserTweetsFetcher usersTweetsFetcher = new UserTweetsFetcher();
                    Map<Long, List<Tweet>> userTweetsMap = usersTweetsFetcher.fetchUserTweets(otherUserIds);

                    UserInfoFetcher usersInfofetcher = new UserInfoFetcher();
                    Map<Long, List<User>> userInfoMap = usersInfofetcher.fetchUserInfos(otherUserIds);

                    UserTweetsFetcher requestUserTweetsFetcher = new UserTweetsFetcher();
                    Set<Tweet> requestUserTweets = requestUserTweetsFetcher.fetchTweetsForUser(userId);

                    InteractionScoreCalculator interactionCalculator = new InteractionScoreCalculator();
                    Map<Long, Double> interactionScores = interactionCalculator.calculateInteractionScores(retweetTweetMap, replyTweetMap, otherUserIds);

                    HashtagScoreCalculator hashtagCalculator = new HashtagScoreCalculator();
                    Map<Long, Double> hashtagScores = hashtagCalculator.calculateHashtagScores(requestUserTweets, userTweetsMap);

                    KeywordsScoreCalculator keywordsCalculator = new KeywordsScoreCalculator();
                    Map<Long, Double> keywordsScores = keywordsCalculator.calculateKeywordsScores(requestUserTweets, userTweetsMap, type, phrase, hashtag);

                    CalculateFinalScores finalScoreCalculator = new CalculateFinalScores();
                    List<User> sortedUsers = finalScoreCalculator.calculateFinalScores(userId, otherUserIds, keywordsScores, interactionScores, hashtagScores, userInfoMap, retweetTweetMap, replyTweetMap, type);

                    String response = generateResponse(sortedUsers);
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

}