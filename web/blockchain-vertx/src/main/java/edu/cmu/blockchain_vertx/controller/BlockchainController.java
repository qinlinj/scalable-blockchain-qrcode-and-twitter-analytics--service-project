//package edu.cmu.blockchain_vertx.controller;
//
//import edu.cmu.blockchain_vertx.utility.Base64ZlibUtility;
//import edu.cmu.blockchain_vertx.utility.BlockchainHandler;
//import edu.cmu.blockchain_vertx.utility.BlockchainValidation;
//import org.json.JSONException;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.RequestParam;
//import org.springframework.web.bind.annotation.RestController;
//
//@RestController
//public class BlockchainController {
//
//  /**
//   * logger for Blockchain controller.
//   */
//  private static final Logger LOGGER = LoggerFactory.getLogger(
//    BlockchainController.class
//  );
//
//  @GetMapping("/blockchain")
//  public String verify(@RequestParam("cc") String cc) {
//    if (cc == null) return new Response("").toString();
//
//    //decompress cc
//    String decompressedRequest = Base64ZlibUtility.decompress(cc);
//    if (decompressedRequest == null) return new Response("").toString();
//
//    System.out.println("requestJson");
//    System.out.println(decompressedRequest);
//
//    //parse JSON to Java Objects
//    Request request = null;
//    try {
//      request = new Request(decompressedRequest);
//    } catch (JSONException e) {
//      return new Response(
//              "Request Parse Error: " + e.getMessage() + "\n" + decompressedRequest
//      )
//              .toString();
//    }
//
//    // TODO: verify blockchain
//    boolean isValid = BlockchainValidation.isValidChain(
//      request.getChain().getChain(), request
//    );
//
//    // TODO: add new block
//    if (isValid) {
//      BlockchainHandler.addBlockToChain(request);
//    } else {
//      LOGGER.error("Invalid blockchain. Cannot add new block.");
//      return new Response("").toString(); // Return error or appropriate message
//    }
//    String updatedJson = BlockchainHandler.blockchainToJson(request.getChain());
//    System.out.println("updatedJson");
//    System.out.println(updatedJson);
//    Response response = new Response(request.getChain());
//    String responseString = response.toString();
//
//    // Response response = new Response(request.getChain());
//    // LOGGER.info(decompressedRequest);
//
//    return responseString;
//  }
//
//  @GetMapping("/")
//  public String index() {
//    return "Healthy Blockchain Service!";
//  }
//}
