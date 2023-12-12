package edu.cmu.blockchain_vertx;

import edu.cmu.blockchain_vertx.controller.Request;
import edu.cmu.blockchain_vertx.controller.Response;
import edu.cmu.blockchain_vertx.utility.Base64ZlibUtility;
import edu.cmu.blockchain_vertx.utility.BlockchainHandler;
import edu.cmu.blockchain_vertx.utility.BlockchainValidation;
import io.vertx.ext.web.Route;
import org.json.JSONException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;

public class MainVerticle extends AbstractVerticle {

  private static final Logger LOGGER = LoggerFactory.getLogger(MainVerticle.class);

  @Override
  public void start() {

    Router router = Router.router(vertx);

    router.get("/blockchain").handler(this::verifyAdapter);
    router.get("/").handler(this::index);

    vertx.createHttpServer().requestHandler(router).listen(8080);
  }

  private void verifyAdapter(RoutingContext ctx) {
    try {
      verify(ctx);
    } catch (JSONException e) {
      ctx.fail(500, e);
    }
  }

  private void verify(RoutingContext ctx) throws JSONException {
    String cc = ctx.request().getParam("cc");

    if (cc == null || cc.isEmpty()) {
      respond(ctx, new Response("").toString());
      return;
    }

    String decompressedRequest = Base64ZlibUtility.decompress(cc);

    if (decompressedRequest == null) {
      respond(ctx, new Response("").toString());
      return;
    }

//    System.out.println("requestJson");
//    System.out.println(decompressedRequest);

    Request request = new Request(decompressedRequest);

    boolean isValid = BlockchainValidation.isValidChain(request.getChain().getChain(), request);

    if (isValid) {
      BlockchainHandler.addBlockToChain(request);
    } else {
      LOGGER.error("Invalid blockchain. Cannot add new block.");
      respond(ctx, new Response("").toString());
      return;
    }

    String updatedJson = BlockchainHandler.blockchainToJson(request.getChain());
//    System.out.println("updatedJson");
//    System.out.println(updatedJson);

    Response response = new Response(request.getChain());
    String responseString = response.toString();

    respond(ctx, responseString);
  }

  private void index(RoutingContext ctx) {
    respond(ctx, "Healthy Blockchain Service!");
  }

  private void respond(RoutingContext ctx, String message) {
    HttpServerResponse response = ctx.response();
    response.putHeader("content-type", "application/json");
    response.end(message);
  }

  public static void main(String[] args) {
    Vertx vertx = Vertx.vertx();
    vertx.deployVerticle(new MainVerticle());
  }
}
