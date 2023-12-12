package edu.cmu.blockchain_vertx.controller;

import edu.cmu.blockchain_vertx.model.Block;
import edu.cmu.blockchain_vertx.model.Blockchain;
import edu.cmu.blockchain_vertx.model.Transaction;
import edu.cmu.blockchain_vertx.utility.Base64ZlibUtility;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Response {

  private boolean isValid;
  private final String TEAM_ID = "CloudQuest";
  private final String TEAM_AWS_ACCOUNT_ID = "146445828406";
  private Blockchain chain;
  private String errorMessage;

  public Response(Blockchain chain) {
    this.chain = chain;
    isValid = true;
  }

  public Response(String errorMessage) {
    this.errorMessage = errorMessage;
    isValid = false;
  }

  public void setIsValid(boolean isValid) {
    this.isValid = isValid;
  }

  public boolean isValid() {
    return isValid;
  }

  public String getTEAM_ID() {
    return this.TEAM_ID;
  }

  public String getTEAM_AWS_ACCOUNT_ID() {
    return this.TEAM_AWS_ACCOUNT_ID;
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append(TEAM_ID + "," + TEAM_AWS_ACCOUNT_ID + "\n");
    if (isValid()) {
      String chainjson = null;
      try {
        chainjson = chainToJson();
      } catch (JSONException e) {
        throw new RuntimeException(e);
      }
      String compressed = Base64ZlibUtility.compress(chainjson);
      sb.append(compressed);
    } else {
      sb.append("INVALID");
      sb.append("\n").append(errorMessage);
    }
    return sb.toString();
  }

  private String chainToJson() throws JSONException {
    JSONObject object = new JSONObject();
    JSONArray blocksjson = new JSONArray();
    List<Block> blocks = chain.getChain();
    for (int i = 0; i < blocks.size(); i++) {
      Block block = blocks.get(i);
      JSONObject blockjson = new JSONObject();
      // Parse all_tx
      List<Transaction> all_tx = block.getAll_tx();
      JSONArray all_txjson = new JSONArray();
      for (int j = 0; j < all_tx.size(); j++) {
        Transaction tx = all_tx.get(j);
        JSONObject txjson = new JSONObject();
        txjson.put("recv", tx.getRecv());
        txjson.put("amt", tx.getAmt());
        txjson.put("time", tx.getTime());
        txjson.put("hash", tx.getHash());
        if (tx.getSend() != null) {
          txjson.put("sig", tx.getSig());
          txjson.put("fee", tx.getFee());
          txjson.put("send", tx.getSend());
        }
        all_txjson.put(j, txjson);
      }
      blockjson.put("all_tx", all_txjson);

      // Parse other fields
      blockjson.put("pow", block.getPow());
      blockjson.put("id", block.getId());
      blockjson.put("hash", block.getHash());
      blockjson.put("target", block.getTarget());
      blocksjson.put(i, blockjson);
    }

    object.put("chain", blocksjson);

    return object.toString();
  }
  //   public static void main(String[] args) {
  //     String json =
  //       "{\"chain\":[{\"all_tx\":[{\"recv\":895456882897,\"amt\":500000000,\"time\":\"1582520400000000000\",\"hash\":\"4b277860\"}],\"pow\":\"0\",\"id\":0,\"hash\":\"07c98747\",\"target\":\"1\"},{\"all_tx\":[{\"sig\":1523500375459,\"recv\":831361201829,\"fee\":2408,\"amt\":126848946,\"time\":\"1582520454597521976\",\"send\":895456882897,\"hash\":\"c0473abd\"},{\"recv\":621452032379,\"amt\":500000000,\"time\":\"1582521002184738591\",\"hash\":\"ab56f1d8\"}],\"pow\":\"202\",\"id\":1,\"hash\":\"0055fd15\",\"target\":\"01\"},{\"all_tx\":[{\"sig\":829022340937,\"recv\":905790126919,\"fee\":78125,\"amt\":4876921,\"time\":\"1582521009246242025\",\"send\":831361201829,\"hash\":\"46b61f8e\"},{\"sig\":295281186908,\"recv\":1097844002039,\"fee\":0,\"amt\":83725981,\"time\":\"1582521016852310220\",\"send\":895456882897,\"hash\":\"b6c1b10f\"},{\"recv\":905790126919,\"amt\":250000000,\"time\":\"1582521603026667063\",\"hash\":\"b0750555\"}],\"pow\":\"12\",\"id\":2,\"hash\":\"00288a38\",\"target\":\"0a\"}],\"new_target\":\"007\",\"new_tx\":[{\"sig\":160392705122,\"recv\":658672873303,\"fee\":3536,\"amt\":34263741,\"time\":\"1582521636327155516\",\"send\":831361201829,\"hash\":\"1fb48c71\"},{\"recv\":895456882897,\"amt\":34263741,\"time\":\"1582521645744862608\"}]}";
  //     Request request = new Request(json);
  //     Response response = new Response(request.getChain());
  //     System.out.println(response.toString());
  //   }
}
