package edu.cmu.blockchain_vertx.utility;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class CCHash {
  // /**
  //  * Hash combined string
  //  * @param timestamp string representation of long integer of time
  //  * @param sender long integer
  //  * @param recipient long integer
  //  * @param amount integer
  //  * @param fee integer
  //  * @return the string of the first 8 hex characters of the SHA-256 result
  //  */
  // public static String hash(
  //   String timestamp,
  //   long sender,
  //   long recipient,
  //   int amount,
  //   int fee
  // ) {
  //   String unhashed =
  //     timestamp + "|" + sender + "|" + recipient + "|" + amount + "|" + fee;

  //   try {
  //     MessageDigest md = MessageDigest.getInstance("SHA-256");
  //     md.update(unhashed.getBytes());
  //     byte[] hashBytes = md.digest();
  //     StringBuilder hexString = new StringBuilder();
  //     // Convert hashBytes to hex string
  //     for (byte hashByte : hashBytes) {
  //       String hex = Integer.toHexString(0xff & hashByte);
  //       if (hex.length() == 1) {
  //         hexString.append(0);
  //       }
  //       hexString.append(hex);
  //     }
  //     String hashed = hexString.toString().substring(0, 8);
  //     return hashed;
  //   } catch (NoSuchAlgorithmException e) {
  //     return "";
  //   }
  // }

  // public static String hash(String blockHash, String PoW) {
  //   String unhashed = blockHash + "|" + PoW;

  //   try {
  //     MessageDigest md = MessageDigest.getInstance("SHA-256");
  //     md.update(unhashed.getBytes());
  //     byte[] hashBytes = md.digest();

  //     StringBuilder hexString = new StringBuilder();
  //     // Convert hashBytes to hex string
  //     for (byte hashByte : hashBytes) {
  //       String hex = Integer.toHexString(0xff & hashByte);
  //       if (hex.length() == 1) {
  //         hexString.append(0);
  //       }
  //       hexString.append(hex);
  //     }
  //     String hashed = hexString.toString().substring(0, 8);
  //     return hashed;
  //   } catch (NoSuchAlgorithmException e) {
  //     return "";
  //   }
  // }
}
