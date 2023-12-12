package edu.cmu.blockchain.model;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;

public class Block {

  private List<Transaction> all_tx;
  private String pow;
  private int id;
  private String hash;

  private String previousBlockHash;

  private String target;

  public Block(
    List<Transaction> all_tx,
    String pow,
    int id,
    String hash,
    String previousBlockHash,
    String target
  ) {
    this.all_tx = all_tx;
    this.pow = pow;
    this.id = id;
    this.hash = hash;
    this.previousBlockHash = previousBlockHash;
    this.target = target;
  }

  public Block() {}

  public String getPreviousBlockHash() {
    return previousBlockHash;
  }

  public void setPreviousBlockHash(String previousBlockHash) {
    this.previousBlockHash = previousBlockHash;
  }

  public List<Transaction> getAll_tx() {
    return all_tx;
  }

  public void setAll_tx(List<Transaction> all_tx) {
    this.all_tx = all_tx;
  }

  public String getPow() {
    return pow;
  }

  public void setPow(String pow) {
    this.pow = pow;
  }

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public String getHash() {
    return hash;
  }

  public void setHash(String hash) {
    this.hash = hash;
  }

  public String getTarget() {
    return target;
  }

  public void setTarget(String target) {
    this.target = target;
  }

  /**
   * Compute the hash for the block using block details and PoW.
   * @return Computed block's hash.
   */
  public String computeBlockHash() {
    StringBuilder input = new StringBuilder();

    input.append(id).append("|");
    if (previousBlockHash == null) {
      input.append("00000000"); // Initial hash for the first block.
    } else {
      input.append(previousBlockHash).append("|");
    }

    for (Transaction tx : all_tx) {
      input.append(tx.getHash()).append("|");
    }

    String sha256Hash = getSHA256(input.toString());

    String toHash = sha256Hash + pow;

    // return CHash.hash(toHash);
    return toHash;
  }

  /**
   * Mining process: Determines the PoW for the block to have a hash
   * lexicographically smaller than the block's hash target.
   * @return Computed PoW string.
   */
  public String mine() {
    int nonce = 0;

    while (true) {
      this.pow = String.valueOf(nonce);
      String currentHash = computeBlockHash();

      if (currentHash.compareTo(target) < 0) {
        this.hash = currentHash;
        return this.pow; // Found PoW
      }

      nonce++;
    }
  }

  /**
   * Computes SHA-256 hash for a given input string.
   * @param input Input string to be hashed.
   * @return The SHA-256 hash.
   */
  private String getSHA256(String input) {
    try {
      MessageDigest md = MessageDigest.getInstance("SHA-256");
      byte[] hashBytes = md.digest(input.getBytes());
      StringBuilder hexString = new StringBuilder();

      for (byte hashByte : hashBytes) {
        String hex = Integer.toHexString(0xff & hashByte);
        if (hex.length() == 1) hexString.append('0');
        hexString.append(hex);
      }

      return hexString.toString();
    } catch (NoSuchAlgorithmException e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * Calculate the miner's reward based on block's ID and transaction fees.
   * @return Total reward for the miner.
   */
  public int getTotalMinerReward() {
    int reward = 500000000 >> (this.id / 2); // Calculate base reward

    // Add transaction fees from all transactions
    for (Transaction tx : this.all_tx) {
      if (tx.getFee() != null) {
        reward += tx.getFee();
      }
    }

    return reward;
  }

  // TODO: return the full hex string of SHA-256("block_id|previous_block_hash|tx1_hash|tx2_hash|tx3_hash...")
  public String hashString() {
    return "";
  }
}
