package edu.cmu.blockchain_vertx.utility;

import java.math.BigInteger;

public class RSAUtility {

    // Constants for our RSA encryption.
    private static final BigInteger N = new BigInteger("1561906343821");
    private static final BigInteger E = new BigInteger("1097844002039"); // public exponent
    private static final BigInteger D = new BigInteger("343710770439"); // private exponent

    /**
     * Signs the given hash using RSA private key.
     *
     * @param hash The hash value to sign.
     * @return The signature.
     */
    public static long sign(String hash) {
        BigInteger hashInt = new BigInteger(hash, 16);
        // Signature = (Hash ^ d) % n
        BigInteger signature = hashInt.modPow(D, N);
        return signature.longValue();
    }

    /**
     * Verifies the signature using RSA public key.
     *
     * @param signature The digital signature to verify.
     * @return The hash value from the signature.
     */
    public static BigInteger verify(BigInteger signature) {
        // Hash = (Signature ^ e) % n
        return signature.modPow(E, N);
    }

    public static BigInteger verify(BigInteger signature, BigInteger publicKey) {
        // Hash = (Signature ^ e) % n
        return signature.modPow(publicKey, N);
    }
}
