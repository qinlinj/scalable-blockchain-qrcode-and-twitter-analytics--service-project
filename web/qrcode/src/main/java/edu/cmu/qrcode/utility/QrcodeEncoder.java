package edu.cmu.qrcode.utility;

import java.util.Arrays;

public class QrcodeEncoder {

    public static void main(String[] args) {
        String input = "CC%20Team";
        byte[] encoded = encodeStringToBits(input);
        System.out.println(encoded[0]);
        System.out.println(Arrays.toString(encoded));
        // Print encoded bytes
        for (byte b : encoded) {
            System.out.println(String.format("%8s", Integer.toBinaryString(b & 0xFF)).replace(' ', '0'));
        }
    }

    public static byte[] encodeStringToBits(String input) {
        // Replace %20 with space
        String decodedString = input.replace("%20", " ");

        // Convert string to bytes
        byte[] messageBytes = decodedString.getBytes();

        // Calculate the length of final payload
        int finalLength = 1 + 2 * messageBytes.length;  // 1 for length + 2 for each character (1 for character, 1 for error code)

        // Create payload byte array
        byte[] payload = new byte[finalLength];

        // First byte is the length of the message
        payload[0] = (byte) messageBytes.length;

        // Fill in the rest of the payload
        for (int i = 0; i < messageBytes.length; i++) {
            byte characterByte = messageBytes[i];

            // Insert character byte into payload
            payload[2 * i + 1] = characterByte;

            // Calculate error correction byte and insert into payload
            byte errorCode = calculateErrorCorrectionByte(characterByte);
            payload[2 * i + 2] = errorCode;
        }

        return payload;
    }

    public static byte calculateErrorCorrectionByte(byte input) {
        int bitsSetCount = 0;

        for (int i = 0; i < 8; i++) {
            if ((input & (1 << i)) != 0) {
                bitsSetCount++;
            }
        }

        return (byte) (bitsSetCount % 2);
    }
}
