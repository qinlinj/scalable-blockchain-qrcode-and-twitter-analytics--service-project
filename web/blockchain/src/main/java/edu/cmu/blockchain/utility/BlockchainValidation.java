package edu.cmu.blockchain.utility;

import edu.cmu.blockchain.controller.Request;
import edu.cmu.blockchain.model.Block;
import edu.cmu.blockchain.model.Transaction;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.util.List;

public class BlockchainValidation {
  private static final long REWARD_INTERVAL = 2;
  private static final int INITIAL_REWARD = 500000000;
  public static void main(String[] args) {
    String json =
            "{\"chain\":[{\"all_tx\":[{\"recv\":895456882897,\"amt\":500000000,\"time\":\"1582520400000000000\",\"hash\":\"4b277860\"}],\"pow\":\"0\",\"id\":0,\"hash\":\"07c98747\",\"target\":\"1\"},{\"all_tx\":[{\"sig\":1523500375459,\"recv\":831361201829,\"fee\":2408,\"amt\":126848946,\"time\":\"1582520454597521976\",\"send\":895456882897,\"hash\":\"c0473abd\"},{\"recv\":621452032379,\"amt\":500000000,\"time\":\"1582521002184738591\",\"hash\":\"ab56f1d8\"}],\"pow\":\"202\",\"id\":1,\"hash\":\"0055fd15\",\"target\":\"01\"},{\"all_tx\":[{\"sig\":829022340937,\"recv\":905790126919,\"fee\":78125,\"amt\":4876921,\"time\":\"1582521009246242025\",\"send\":831361201829,\"hash\":\"46b61f8e\"},{\"sig\":295281186908,\"recv\":1097844002039,\"fee\":0,\"amt\":83725981,\"time\":\"1582521016852310220\",\"send\":895456882897,\"hash\":\"b6c1b10f\"},{\"recv\":905790126919,\"amt\":250000000,\"time\":\"1582521603026667063\",\"hash\":\"b0750555\"}],\"pow\":\"12\",\"id\":2,\"hash\":\"00288a38\",\"target\":\"0a\"}],\"new_target\":\"007\",\"new_tx\":[{\"sig\":160392705122,\"recv\":658672873303,\"fee\":3536,\"amt\":34263741,\"time\":\"1582521636327155516\",\"send\":831361201829,\"hash\":\"1fb48c71\"},{\"recv\":895456882897,\"amt\":34263741,\"time\":\"1582521645744862608\"}]}";
    Request request = new Request(json);

    List<Block> chain = request.getChain().getChain();

    if (isValidChain(chain, request)) {
      System.out.println("The blockchain is valid.");
    } else {
      System.out.println("The blockchain is not valid.");
    }
  }

  public static boolean isValidChain(List<Block> chain, Request request) {

    String previousHash = "00000000";
    // check block id validation
    for (int i = 0; i < chain.size(); i++) {
      if (chain.get(i).getId() != i) {
        System.out.println("Block IDs are not sequential.");
        return false;
      }
    }

    Block lastBlock = chain.get(chain.size() - 1);
    if (lastBlock.getId() != chain.size() - 1) {
      System.out.println("Chain length doesn't match the last block's ID.");
      return false;
    }

//    List<Transaction> newTransactions = request.getNew_tx();

//    boolean hasTxInfo = false;
//    boolean hasMinerInfo = false;
//
//    for (Transaction tx : newTransactions) {
//      // check hasTxInfo
//      System.out.println(tx.toString());
//
//      if (tx.getSig() != null && tx.getRecv() != null && tx.getFee() != null && tx.getAmt() != null && tx.getTime() != null && tx.getSend() != null && tx.getHash() != null) {
//        hasTxInfo = true;
//      }
//    }
//
//    for (Transaction tx : newTransactions) {
//      // check hasMinerInfo
//      if (tx.getRecv() != null && tx.getAmt() != null && tx.getTime() != null && tx.getSig() == null && tx.getFee() == null && tx.getSend() == null && tx.getHash() == null){
//        hasMinerInfo = true;
//      }
//    }
//    System.out.println(hasTxInfo);
//    System.out.println(hasMinerInfo);
//
//    if (hasTxInfo && !hasMinerInfo) {
//      System.out.println("Not all required transaction types are present.");
//      return false;
//    }
    long lastTimestamp = -1L;
    for (Block block : chain) {
      int foundTxWithoutSend = 0;
      String hashInput = block.getId() + "|" + previousHash;
      for (Transaction tx : block.getAll_tx()) {
        System.out.println(tx.toString());

        if (tx.getSend() == null) {
          foundTxWithoutSend = foundTxWithoutSend + 1;
        }

        if (tx.getFee() != null && tx.getFee() < 0) {
          System.out.println("Invalid Fee " + block.getId());
          return false;
        }
        if (tx.getAmt() != null && tx.getAmt() < 0) {
          System.out.println("Invalid Amount " + block.getId());
          return false;
        }
//        if (tx.getRecv() != null && tx.getSend() != null && tx.getRecv().equals(tx.getSend())) {
//          System.out.println("Invalid tx " + block.getId());
//          return false;
//        }


        // check if tx has the correct hash
        String txHashInput =
                tx.getTime() +
                        "|" +
                        (tx.getSend() == null ? "" : tx.getSend()) +
                        "|" +
                        tx.getRecv() +
                        "|" +
                        tx.getAmt() +
                        "|" +
                        (tx.getFee() == null ? "" : tx.getFee());
        String txHash = CCHash(SHA256(txHashInput));
        if (!txHash.equals(tx.getHash())) {
          System.out.println(txHashInput);
          System.out.println(
                  txHash + " vs. " + tx.getHash() + " for block " + block.getId()
          );
          return false;
        }

        // check if tx has valid signature
        if (tx.getSig() != null) {
          BigInteger sig = new BigInteger(tx.getSig().toString());
          // Redundancy check
//          if (tx.getSig() == null) return false;
          BigInteger send = new BigInteger(tx.getSend().toString());
          BigInteger verifiedSig = RSAUtility.verify(sig, send);
          BigInteger hashInteger = new BigInteger(tx.getHash(), 16);
          if (verifiedSig.compareTo(hashInteger) != 0) {
            System.out.println(tx.toString());
            System.out.println(
                    verifiedSig +
                            " vs. " +
                            hashInteger +
                            " for block " +
                            block.getId()
            );
            return false;
          }
        }

        hashInput += "|" + tx.getHash();
        if (tx.getSend() == null && tx.getFee() == null && tx.getRecv() != null && tx.getAmt() != null) {
          int blockId = block.getId();
          int rewardAmountForBlock = INITIAL_REWARD / (int) Math.pow(2, blockId / REWARD_INTERVAL);
          if (tx.getAmt() != rewardAmountForBlock) {
            System.out.println("Reward amount mismatch for block " + block.getId());
            return false;
          }
        }

        long currentTimestamp = Long.parseLong(tx.getTime());
        if (currentTimestamp <= lastTimestamp) {
          System.out.println("Invalid timestamp order for block " + block.getId());
          return false;
        }
        lastTimestamp = currentTimestamp;
      }
      if (foundTxWithoutSend != 1) {
        System.out.println("The miner tx in the block is not equal to 1 " + block.getId());
        return false;
      }

      String blockHash = CCHash(SHA256(SHA256(hashInput) + block.getPow()));
      if (!blockHash.equals(block.getHash())) {
        System.out.println(
                blockHash + " vs. " + block.getHash() + " for block " + block.getId()
        );
        return false;
      }
      if (
              blockHash
                      .substring(0, block.getTarget().length())
                      .compareTo(block.getTarget()) >=
                      0
      ) {
        return false;
      }
      previousHash = block.getHash();
    }

    return true;
  }

  public static String CCHash(String input) {
    return input.substring(0, 8);
  }

  public static String SHA256(String base) {
    try {
      MessageDigest digest = MessageDigest.getInstance("SHA-256");
      byte[] hash = digest.digest(base.getBytes("UTF-8"));
      StringBuffer hexString = new StringBuffer();

      for (int i = 0; i < hash.length; i++) {
        String hex = Integer.toHexString(0xff & hash[i]);
        if (hex.length() == 1) hexString.append('0');
        hexString.append(hex);
      }

      return hexString.toString();
    } catch (Exception ex) {
      throw new RuntimeException(ex);
    }
  }
}