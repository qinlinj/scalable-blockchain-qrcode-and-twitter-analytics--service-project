package org.example;

import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class CompareETLResult {

    public static void main(String[] args) throws IOException {
        // Set to store id_str from both files
        Set<String> originalTweetIDs = new HashSet<>();
        Set<String> etlTweetIDs = new HashSet<>();

        // Read all id_str from ./microservice3_ref into originalTweetIDs set
        try (BufferedReader reader = new BufferedReader(new FileReader("./microservice3_ref"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                try {
                    JSONObject tweetObject = new JSONObject(line);
                    if (tweetObject.has("id_str")) {
                        originalTweetIDs.add(tweetObject.getString("id_str"));
                    }
                } catch (Exception e) {
                    System.out.println("Error parsing JSON from original file: " + e.getMessage() + " for line: " + line);
                }
            }
        }

        // Read all id_str from ./part-r-00000-etl into etlTweetIDs set
        try (BufferedReader reader = new BufferedReader(new FileReader("./part-r-00000-etl"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                try {
                    JSONObject tweetObject = new JSONObject(line);
                    if (tweetObject.has("id_str")) {
                        etlTweetIDs.add(tweetObject.getString("id_str"));
                    }
                } catch (Exception e) {
                    System.out.println("Error parsing JSON from ETL file: " + e.getMessage() + " for line: " + line);
                }
            }
        }

        // Write JSON objects which are in original but not in ETL
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("./missing-in-etl.json"));
             BufferedReader reader = new BufferedReader(new FileReader("./microservice3_ref"))) {

            String line;
            while ((line = reader.readLine()) != null) {
                try {
                    JSONObject tweetObject = new JSONObject(line);
                    if (tweetObject.has("id_str") && !etlTweetIDs.contains(tweetObject.getString("id_str"))) {
                        writer.write(tweetObject.toString());
                        writer.newLine();
                    }
                } catch (Exception e) {
                    System.out.println("Error parsing JSON from original file: " + e.getMessage() + " for line: " + line);
                }
            }
        }

        // Write JSON objects which are in ETL but not in original
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("./missing-in-original.json"));
             BufferedReader reader = new BufferedReader(new FileReader("./part-r-00000-etl"))) {

            String line;
            while ((line = reader.readLine()) != null) {
                try {
                    JSONObject tweetObject = new JSONObject(line);
                    if (tweetObject.has("id_str") && !originalTweetIDs.contains(tweetObject.getString("id_str"))) {
                        writer.write(tweetObject.toString());
                        writer.newLine();
                    }
                } catch (Exception e) {
                    System.out.println("Error parsing JSON from ETL file: " + e.getMessage() + " for line: " + line);
                }
            }
        }
    }
}

