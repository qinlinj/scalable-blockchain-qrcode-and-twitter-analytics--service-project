package edu.cmu.blockchain_vertx.model;

public class Transaction {

  private String time; // timestamp as a string representation of long integer
  private Long send; // sender's account number/public key; can be null for reward transactions
  private Long recv; // recipient's account number/public key
  private Integer amt; // transaction amount
  private Integer fee; // transaction fee; can be null for reward transactions
  private String hash; // hex string of the transaction hash
  private Long sig; // RSA signature of the transaction hash; can be null for reward transactions

  // Constructors, getters, setters, and other utility methods can be added
  public Transaction() {}

  // Create a complete transaction from the all_tx or from new_tx
  public Transaction(
    String time,
    Long send,
    Long recv,
    Integer amt,
    Integer fee,
    String hash,
    Long sig
  ) {
    this.time = time;
    this.send = send;
    this.recv = recv;
    this.amt = amt;
    this.fee = fee;
    this.hash = hash;
    this.sig = sig;
  }

  // Getter and Setter methods for each attribute

  public String getTime() {
    return time;
  }

  public void setTime(String time) {
    this.time = time;
  }

  public Long getSend() {
    return send;
  }

  public void setSend(Long send) {
    this.send = send;
  }

  public Long getRecv() {
    return recv;
  }

  public void setRecv(Long recv) {
    this.recv = recv;
  }

  public Integer getAmt() {
    return amt;
  }

  public void setAmt(Integer amt) {
    this.amt = amt;
  }

  public Integer getFee() {
    return fee;
  }

  public void setFee(Integer fee) {
    this.fee = fee;
  }

  public String getHash() {
    return hash;
  }

  public void setHash(String hash) {
    this.hash = hash;
  }

  public Long getSig() {
    return sig;
  }

  public void setSig(Long sig) {
    this.sig = sig;
  }

  /**
   * Create a new reward transaction for the miner.
   * @param time The timestamp of the transaction.
   * @param recv The recipient's account (miner's account).
   * @param amt The reward amount for the miner.
   * @return A new reward transaction.
   */
  public static Transaction createRewardTransaction(
    String time,
    Long recv,
    int amt
  ) {
    return new Transaction(time, null, recv, amt, null, null, null);
  }


  /**
   * Create a new transaction where the team is the sender.
   * @param time The timestamp of the transaction.
   * @param recv The recipient's account.
   * @param amt The transaction amount.
   * @return A new transaction.
   */
  public static Transaction createTeamTransaction(
    String time,
    Long recv,
    int amt
  ) {
    Long teamAccount = 1097844002039L; // The team's account number
    return new Transaction(time, teamAccount, recv, amt, 0, null, null); // fee = 0 as per spec.
  }

  @Override
  public String toString() {
    return (
      "Transaction { " +
      "time='" +
      time +
      '\'' +
      ", send=" +
      send +
      ", recv=" +
      recv +
      ", amt=" +
      amt +
      ", fee=" +
      fee +
      ", hash='" +
      hash +
      '\'' +
      ", sig=" +
      sig +
      " }"
    );
  }
}
