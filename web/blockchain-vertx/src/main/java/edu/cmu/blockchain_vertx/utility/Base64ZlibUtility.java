package edu.cmu.blockchain_vertx.utility;

import edu.cmu.blockchain_vertx.controller.Request;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

public class Base64ZlibUtility {

  public static String decompress(String base64EncodedString) {
    StringBuilder decompressedText = new StringBuilder();

    try {
      // Decode the URL-safe Base64-encoded string
      byte[] decodedBytes = Base64.getUrlDecoder().decode(base64EncodedString);

      // Decompress the decoded data using zlib
      Inflater inflater = new Inflater();
      inflater.setInput(decodedBytes);

      // Create a buffer to hold the decompressed data
      byte[] buffer = new byte[1024];

      while (!inflater.finished()) {
        int length = inflater.inflate(buffer);
        decompressedText.append(new String(buffer, 0, length, "UTF-8"));
      }

      inflater.end();
    } catch (DataFormatException e) {
      e.printStackTrace();
    } catch (Exception e) {
      e.printStackTrace();
    }

    return decompressedText.toString();
  }

  public static String compress(String original) {
    byte[] originalBytes = original.getBytes();

    Deflater deflater = new Deflater();
    deflater.setInput(originalBytes);

    ByteArrayOutputStream outputStream = new ByteArrayOutputStream(
      originalBytes.length
    );

    deflater.finish();
    byte[] buffer = new byte[1024];
    while (!deflater.finished()) {
      int count = deflater.deflate(buffer); // returns the generated code... index
      outputStream.write(buffer, 0, count);
    }
    try {
      outputStream.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
    byte[] output = outputStream.toByteArray();

    String encoded = Base64.getUrlEncoder().encodeToString(output);
    return encoded;
  }

  public static void main(String[] args) {

    String jsonString = new String("{\n" +
            "  \"chain\": [\n" +
            "    {\n" +
            "      \"all_tx\": [{\n" +
            "        \"recv\": 895456882897,\n" +
            "        \"amt\": 500000000,\n" +
            "        \"time\": \"1582520400000000000\",\n" +
            "        \"hash\": \"4b277860\"\n" +
            "      }],\n" +
            "      \"pow\": \"0\",\n" +
            "      \"id\": 0,\n" +
            "      \"hash\": \"07c98747\",\n" +
            "      \"target\": \"1\"\n" +
            "    },\n" +
            "    {\n" +
            "      \"all_tx\": [\n" +
            "        {\n" +
            "          \"sig\": 1523500375459,\n" +
            "          \"recv\": 831361201829,\n" +
            "          \"fee\": 2408,\n" +
            "          \"amt\": 126848946,\n" +
            "          \"time\": \"1582520454597521976\",\n" +
            "          \"send\": 895456882897,\n" +
            "          \"hash\": \"c0473abd\"\n" +
            "        },\n" +
            "        {\n" +
            "          \"recv\": 621452032379,\n" +
            "          \"amt\": 500000000,\n" +
            "          \"time\": \"1582521002184738591\",\n" +
            "          \"hash\": \"ab56f1d8\"\n" +
            "        }\n" +
            "      ],\n" +
            "      \"pow\": \"202\",\n" +
            "      \"id\": 1,\n" +
            "      \"hash\": \"0055fd15\",\n" +
            "      \"target\": \"01\"\n" +
            "    },\n" +
            "    {\n" +
            "      \"all_tx\": [\n" +
            "        {\n" +
            "          \"sig\": 829022340937,\n" +
            "          \"recv\": 905790126919,\n" +
            "          \"fee\": 78125,\n" +
            "          \"amt\": 4876921,\n" +
            "          \"time\": \"1582521009246242025\",\n" +
            "          \"send\": 831361201829,\n" +
            "          \"hash\": \"46b61f8e\"\n" +
            "        },\n" +
            "        {\n" +
            "          \"sig\": 295281186908,\n" +
            "          \"recv\": 1097844002039,\n" +
            "          \"fee\": 0,\n" +
            "          \"amt\": 83725981,\n" +
            "          \"time\": \"1582521016852310220\",\n" +
            "          \"send\": 895456882897,\n" +
            "          \"hash\": \"b6c1b10f\"\n" +
            "        },\n" +
            "        {\n" +
            "          \"recv\": 905790126919,\n" +
            "          \"amt\": 250000000,\n" +
            "          \"time\": \"1582521603026667063\",\n" +
            "          \"hash\": \"b0750555\"\n" +
            "        }\n" +
            "      ],\n" +
            "      \"pow\": \"12\",\n" +
            "      \"id\": 2,\n" +
            "      \"hash\": \"00288a38\",\n" +
            "      \"target\": \"0a\"\n" +
            "    }\n" +
            "  ],\n" +
            "  \"new_target\": \"007\",\n" +
            "  \"new_tx\": [\n" +
            "    {\n" +
            "      \"sig\": 160392705122,\n" +
            "      \"recv\": 658672873303,\n" +
            "      \"fee\": 3536,\n" +
            "      \"amt\": 34263741,\n" +
            "      \"time\": \"1582521636327155516\",\n" +
            "      \"send\": 831361201829,\n" +
            "      \"hash\": \"1fb48c71\"\n" +
            "    },\n" +
            "    {\n" +
            "      \"recv\": 895456882897,\n" +
            "      \"amt\": 34263741,\n" +
            "      \"time\": \"1582521645744862608\"\n" +
            "    }\n" +
            "  ]\n" +
            "}");
    jsonString = compress(jsonString);

    System.out.println(jsonString);
  }
}
