package edu.cmu.blockchain.utility;

import edu.cmu.blockchain.controller.Request;
import edu.cmu.blockchain.model.Block;
import edu.cmu.blockchain.model.Blockchain;
import edu.cmu.blockchain.model.Transaction;
import org.json.JSONArray;
import org.json.JSONObject;

import java.security.MessageDigest;
import java.util.List;

import static edu.cmu.blockchain.utility.RSAUtility.sign;

public class BlockchainHandler {

    private static final long REWARD_INTERVAL = 2;
    private static final int INITIAL_REWARD = 500000000;
    private static final long TEAM_ACCOUNT = 1097844002039L;
    private static final long TEN_MINUTES_NANOSECONDS = 10L * 60L * 1000000000L;

    public static void addBlockToChain(Request request) {
        Blockchain blockchain = request.getChain();

        List<Block> chain = blockchain.getChain();
        List<Transaction> newTransactions = request.getNew_tx();
//        if (newTransactions == null || newTransactions.isEmpty()) {
//            return;
//        }
        // Get the last block's ID
        int lastBlockId = chain.get(chain.size() - 1).getId();
        int newBlockId = lastBlockId + 1;

        // Calculate reward
        int rewardAmount = INITIAL_REWARD / (int) Math.pow(2, newBlockId / REWARD_INTERVAL);

        // Identify miner's data
        for (Transaction tx : newTransactions) {
            if (tx.getSend() == null && tx.getFee() == null && tx.getRecv() != null && tx.getAmt() != null) {
                tx.setSend(TEAM_ACCOUNT);
                tx.setFee(0);
                tx.setSig(sign(computeTransactionHash(tx)));
            }
        }

        Block lastBlock = chain.get(chain.size() - 1);

        // Get all transactions of the last block
        List<Transaction> allTransactions = lastBlock.getAll_tx();

        if (!allTransactions.isEmpty()) {
            // Process the miner's data to ensure "send" and "hash" fields are set
            //            minerData = processNewTransaction(minerData);
            Transaction lastTransaction = allTransactions.get(allTransactions.size() - 1);
            String lastTransactionTime = lastTransaction.getTime();
            // Create reward transaction for the miner
            String rewardTxTime = String.valueOf(Long.parseLong(lastTransactionTime) + TEN_MINUTES_NANOSECONDS);
            Transaction rewardTransaction = Transaction.createRewardTransaction(rewardTxTime, TEAM_ACCOUNT, rewardAmount);

            // Process the reward transaction to ensure "send" and "hash" fields are set
            rewardTransaction = processNewTransaction(rewardTransaction);

            newTransactions.add(rewardTransaction); // append reward transaction for miner
        }

        // Process other new transactions, ensuring they have all the required fields set
        for (int i = 0; i < newTransactions.size(); i++) {
            newTransactions.set(i, processNewTransaction(newTransactions.get(i)));
        }

        // Get the previous block's hash
        String previousHash;
        if (chain.size() == 0) {
            previousHash = "00000000"; // For the genesis block
        } else {
            previousHash = chain.get(chain.size() - 1).getHash(); // Get hash of the last block
        }

        // Create a new block with the new set of transactions
        Block newBlock = new Block();
        newBlock.setAll_tx(newTransactions);
        newBlock.setId(newBlockId);
        newBlock.setTarget(request.getNew_target());
        // Compute POW and hash for new block with the previous block's hash
        newBlock.setPow(computePOW(newBlock, previousHash));
        newBlock.setHash(computeHash(newBlock, previousHash));

        // Append new block to blockchain
        chain.add(newBlock);
    }



    public static String computePOW(Block block, String previousHash) {
        String target = block.getTarget();
        int nonce = 0;

        while (true) {
            String hashInput = block.getId() + "|" + previousHash;
            for (Transaction tx : block.getAll_tx()) {
                hashInput += "|" + tx.getHash();
            }

            String potentialBlockHash = CCHash(SHA256(SHA256(hashInput) + nonce));
            if (potentialBlockHash.compareTo(target) < 0) {
                return String.valueOf(nonce);
            }

            nonce++;
        }
    }


    public static String computeHash(Block block, String previousHash) {
        String hashInput = block.getId() + "|" + previousHash;
        for (Transaction tx : block.getAll_tx()) {
            hashInput += "|" + tx.getHash();
        }
        return CCHash(SHA256(SHA256(hashInput) + block.getPow()));
    }


    public static String CCHash(String input) {
        return input.substring(0, 8);
    }

    public static String SHA256(String base) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(base.getBytes("UTF-8"));
            StringBuilder hexString = new StringBuilder();

            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }

            return hexString.toString();
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    public static String blockchainToJson(Blockchain blockchain) {
        JSONArray chainJson = new JSONArray();

        for (Block block : blockchain.getChain()) {
            JSONObject blockJson = new JSONObject();
            blockJson.put("id", block.getId());
            blockJson.put("hash", block.getHash());
            blockJson.put("target", block.getTarget());
            blockJson.put("pow", block.getPow());

            JSONArray transactionsJson = new JSONArray();
            for (Transaction tx : block.getAll_tx()) {
                JSONObject txJson = new JSONObject();
                txJson.put("time", tx.getTime());
                txJson.put("recv", tx.getRecv());
                txJson.put("amt", tx.getAmt());
                txJson.put("hash", tx.getHash());
                if (tx.getSend() != null) {
                    txJson.put("send", tx.getSend());
                    txJson.put("fee", tx.getFee());
                    txJson.put("sig", tx.getSig());
                }
                transactionsJson.put(txJson);
            }
            blockJson.put("all_tx", transactionsJson);
            chainJson.put(blockJson);
        }

        JSONObject finalJson = new JSONObject();
        finalJson.put("chain", chainJson);

        return finalJson.toString(2);  // Pretty printed JSON
    }
    public static Transaction processNewTransaction(Transaction transaction) {
        // Set the team as the sender
//        if (transaction.getSend() == null) {
//            transaction.setSend(TEAM_ACCOUNT);
//        }
//        // Set fee to 0 as per the requirement
//        if (transaction.getFee() == null) {
//            transaction.setFee(0);
//        }
        transaction.setHash(computeTransactionHash(transaction));
        return transaction;
    }
    private static String computeTransactionHash(Transaction transaction) {
        String sendValue = (transaction.getSend() != null) ? transaction.getSend().toString() : "";
        String feeValue = (transaction.getFee() != null) ? transaction.getFee().toString() : "";
        String timeValue = (transaction.getTime() != null) ? transaction.getTime() : "";
        String recvValue = (transaction.getRecv() != null) ? transaction.getRecv().toString() : "";
        String amtValue = (transaction.getAmt() != null) ? transaction.getAmt().toString() : "";

        String inputData = timeValue + "|" + sendValue + "|" +
                recvValue + "|" + amtValue + "|" +
                feeValue;
        return CCHash(SHA256(inputData));
    }

    public static void main(String[] args) {

        String json = "{\"chain\":[{\"all_tx\":[{\"recv\":895456882897,\"amt\":500000000,\"time\":\"1582520400000000000\",\"hash\":\"4b277860\"}],\"pow\":\"0\",\"id\":0,\"hash\":\"07c98747\",\"target\":\"1\"},{\"all_tx\":[{\"sig\":1523500375459,\"recv\":831361201829,\"fee\":2408,\"amt\":126848946,\"time\":\"1582520454597521976\",\"send\":895456882897,\"hash\":\"c0473abd\"},{\"recv\":621452032379,\"amt\":500000000,\"time\":\"1582521002184738591\",\"hash\":\"ab56f1d8\"}],\"pow\":\"202\",\"id\":1,\"hash\":\"0055fd15\",\"target\":\"01\"},{\"all_tx\":[{\"sig\":829022340937,\"recv\":905790126919,\"fee\":78125,\"amt\":4876921,\"time\":\"1582521009246242025\",\"send\":831361201829,\"hash\":\"46b61f8e\"},{\"sig\":295281186908,\"recv\":1097844002039,\"fee\":0,\"amt\":83725981,\"time\":\"1582521016852310220\",\"send\":895456882897,\"hash\":\"b6c1b10f\"},{\"recv\":905790126919,\"amt\":250000000,\"time\":\"1582521603026667063\",\"hash\":\"b0750555\"}],\"pow\":\"12\",\"id\":2,\"hash\":\"00288a38\",\"target\":\"0a\"}],\"new_target\":\"007\",\"new_tx\":[{\"sig\":160392705122,\"recv\":658672873303,\"fee\":3536,\"amt\":34263741,\"time\":\"1582521636327155516\",\"send\":831361201829,\"hash\":\"1fb48c71\"},{\"recv\":895456882897,\"amt\":34263741,\"time\":\"1582521645744862608\"}]}";
        Request request = new Request(json);
        addBlockToChain(request);
        String updatedJson = blockchainToJson(request.getChain());
        System.out.println(updatedJson);
    }
}
