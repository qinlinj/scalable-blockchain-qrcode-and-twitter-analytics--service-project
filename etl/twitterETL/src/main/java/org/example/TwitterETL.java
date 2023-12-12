package org.example;

import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class TwitterETL {

    public static void main(String[] args) throws IOException {
        Set<String> languages = Set.of("ar", "en", "fr", "in", "pt", "es", "tr", "ja");
        Set<String> processedTweetIDs = new HashSet<>();

        try (BufferedReader reader = new BufferedReader(new FileReader("./part-r-00000"));
             BufferedWriter writer = new BufferedWriter(new FileWriter("./part-r-00000-etl"))) {

            String line;
            while ((line = reader.readLine()) != null) {
                try {
                    JSONObject tweetObject = new JSONObject(line);

                    // Check for valid ID
                    if (!tweetObject.has("id_str") || tweetObject.isNull("id_str")) {
                        System.out.println("Format Error: Tweet missing id_str - " + line);
                        continue;
                    }
                    String tweetId = tweetObject.getString("id_str");

                    // Check for duplicate tweets
                    if (processedTweetIDs.contains(tweetId)) {
                        System.out.println("Duplicate Tweet: " + tweetId);
                        continue;
                    }

                    // Check for valid User ID
                    if (!tweetObject.has("user") || tweetObject.isNull("user") ||
                            !tweetObject.getJSONObject("user").has("id_str") ||
                            tweetObject.getJSONObject("user").isNull("id_str")) {
                        System.out.println("Format Error: Tweet missing user.id_str - " + tweetId);
                        continue;
                    }

                    // Check for valid created_at
                    if (!tweetObject.has("created_at") || tweetObject.isNull("created_at")) {
                        System.out.println("Format Error: Tweet missing created_at - " + tweetId);
                        continue;
                    }

                    // Check for valid text
                    if (!tweetObject.has("text") || tweetObject.isNull("text") || tweetObject.getString("text").isEmpty()) {
                        System.out.println("Format Error: Tweet text is missing or empty - " + tweetId);
                        continue;
                    }

                    // Check for valid hashtags
                    if (!tweetObject.has("entities") || tweetObject.isNull("entities") ||
                            !tweetObject.getJSONObject("entities").has("hashtags") ||
                            tweetObject.getJSONObject("entities").getJSONArray("hashtags").length() == 0) {
                        System.out.println("Format Error: Tweet has missing or empty hashtags - " + tweetId);
                        continue;
                    }

                    // Check for valid language
                    if (!tweetObject.has("lang") || !languages.contains(tweetObject.getString("lang"))) {
                        System.out.println("Invalid Language: " + tweetObject.optString("lang", "N/A") + " in Tweet ID: " + tweetId);
                        continue;
                    }

                    processedTweetIDs.add(tweetId);
                    writer.write(tweetObject.toString());
                    writer.newLine();

                } catch (Exception e) {
                    System.out.println("Error parsing JSON: " + e.getMessage() + " for line: " + line);
                }
            }
        }
    }
}
