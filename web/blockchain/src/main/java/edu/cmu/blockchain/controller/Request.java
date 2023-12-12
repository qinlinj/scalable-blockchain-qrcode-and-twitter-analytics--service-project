package edu.cmu.blockchain.controller;

import edu.cmu.blockchain.model.*;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Request {

  private Blockchain chain;
  private String new_target;
  private List<Transaction> new_tx;

  @Override
  public String toString() {
    return "Request{" +
            "chain=" + chain +
            ", new_target='" + new_target + '\'' +
            ", new_tx=" + new_tx +
            '}';
  }

  // Constructor
  public Request(String json) {
    // Init variables
    chain = new Blockchain();
    new_tx = new ArrayList<>();

    try {
      JSONObject object = new JSONObject(json);
      // Parse new_target
      this.new_target = object.get("new_target").toString();

      // Parse new_tx list
      JSONArray new_txjson = object.getJSONArray("new_tx");
      for (int i = 0; i < new_txjson.length(); i++) {
        JSONObject txjson = new_txjson.getJSONObject(i);
        Transaction tx = new Transaction();
        if (txjson.has("send")) {
          tx.setSend(Long.valueOf(txjson.get("send").toString()));
          tx.setFee(Integer.valueOf(txjson.get("fee").toString()));
          tx.setSig(Long.valueOf(txjson.get("sig").toString()));
          tx.setHash(txjson.get("hash").toString());
        }
        tx.setRecv(Long.valueOf(txjson.get("recv").toString()));
        tx.setAmt(Integer.valueOf(txjson.get("amt").toString()));
        tx.setTime(txjson.get("time").toString());
        new_tx.add(tx);
      }

      // Parse chain list
      List<Block> blocks = new ArrayList<>();
      JSONArray chainjson = object.getJSONArray("chain");
      for (int i = 0; i < chainjson.length(); i++) {
        // Parse each block
        Block block = new Block();
        JSONObject blockjson = chainjson.getJSONObject(i);
        block.setPow(blockjson.get("pow").toString());
        block.setHash(blockjson.get("hash").toString());
        block.setTarget(blockjson.get("target").toString());
        block.setId(Integer.valueOf(blockjson.get("id").toString()));

        // Parse all_tx
        JSONArray all_txjson = blockjson.getJSONArray("all_tx");
        List<Transaction> all_tx = new ArrayList<>();
        for (int j = 0; j < all_txjson.length(); j++) {
          JSONObject txjson = all_txjson.getJSONObject(j);
          Transaction tx = new Transaction();
          if (txjson.has("send")) {
            tx.setSend(Long.valueOf(txjson.get("send").toString()));
            tx.setFee(Integer.valueOf(txjson.get("fee").toString()));
            tx.setSig(Long.valueOf(txjson.get("sig").toString()));
          }
          tx.setRecv(Long.valueOf(txjson.get("recv").toString()));
          tx.setAmt(Integer.valueOf(txjson.get("amt").toString()));
          tx.setTime(txjson.get("time").toString());
          tx.setHash(txjson.get("hash").toString());
          all_tx.add(tx);
        }
        block.setAll_tx(all_tx);

        blocks.add(block);
      }
      chain.setChain(blocks);
    } catch (JSONException e) {
      throw e;
    }
  }

  public static void main(String[] args) {
    // parse the JSON object to a new request
    String json =
            "{\"chain\":[{\"all_tx\":[{\"recv\":895456882897,\"amt\":500000000,\"time\":\"1582520400000000000\",\"hash\":\"4b277860\"}],\"pow\":\"0\",\"id\":0,\"hash\":\"07c98747\",\"target\":\"1\"},{\"all_tx\":[{\"sig\":1523500375459,\"recv\":831361201829,\"fee\":2408,\"amt\":126848946,\"time\":\"1582520454597521976\",\"send\":895456882897,\"hash\":\"c0473abd\"},{\"recv\":621452032379,\"amt\":500000000,\"time\":\"1582521002184738591\",\"hash\":\"ab56f1d8\"}],\"pow\":\"202\",\"id\":1,\"hash\":\"0055fd15\",\"target\":\"01\"},{\"all_tx\":[{\"sig\":829022340937,\"recv\":905790126919,\"fee\":78125,\"amt\":4876921,\"time\":\"1582521009246242025\",\"send\":831361201829,\"hash\":\"46b61f8e\"},{\"sig\":295281186908,\"recv\":1097844002039,\"fee\":0,\"amt\":83725981,\"time\":\"1582521016852310220\",\"send\":895456882897,\"hash\":\"b6c1b10f\"},{\"recv\":905790126919,\"amt\":250000000,\"time\":\"1582521603026667063\",\"hash\":\"b0750555\"}],\"pow\":\"12\",\"id\":2,\"hash\":\"00288a38\",\"target\":\"0a\"}],\"new_target\":\"007\",\"new_tx\":[{\"sig\":160392705122,\"recv\":658672873303,\"fee\":3536,\"amt\":34263741,\"time\":\"1582521636327155516\",\"send\":831361201829,\"hash\":\"1fb48c71\"},{\"recv\":895456882897,\"amt\":34263741,\"time\":\"1582521645744862608\"}]}";
    Request test = new Request(json);
  }

  public Blockchain getChain() {
    return this.chain;
  }

  public void setChain(Blockchain chain) {
    this.chain = chain;
  }

  public String getNew_target() {
    return this.new_target;
  }

  public void setNew_target(String new_target) {
    this.new_target = new_target;
  }

  public List<Transaction> getNew_tx() {
    return this.new_tx;
  }

  public void setNew_tx(List<Transaction> new_tx) {
    this.new_tx = new_tx;
  }
}
