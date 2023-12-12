package edu.cmu.blockchain.model;

import java.util.List;

public class Blockchain {

  private List<Block> chain; // list of blocks in the chain
  private List<Transaction> new_tx; // list of new transactions to process
  private String new_target; // hex string of the new hash target

  //   public ChainRequest(List<Block> chain, List<Transaction> new_tx, String new_target) {
  //       this.chain = chain;
  //       this.new_tx = new_tx;
  //       this.new_target = new_target;
  //   }

  // Getter and Setter methods

  public List<Block> getChain() {
    return chain;
  }

  public void setChain(List<Block> chain) {
    this.chain = chain;
  }

  public List<Transaction> getNew_tx() {
    return new_tx;
  }

  public void setNew_tx(List<Transaction> new_tx) {
    this.new_tx = new_tx;
  }

  public String getNew_target() {
    return new_target;
  }

  public void setNew_target(String new_target) {
    this.new_target = new_target;
  }

  @Override
  public String toString() {
    return (
      "Blockchain { " +
      "chain=" +
      chain +
      ", new_tx=" +
      new_tx +
      ", new_target='" +
      new_target +
      '\'' +
      " }"
    );
  }
}
