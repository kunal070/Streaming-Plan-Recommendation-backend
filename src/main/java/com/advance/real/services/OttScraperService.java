package com.advance.real.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

@Service
public class OttScraperService {

    private static final Logger logger = LoggerFactory.getLogger(OttScraperService.class);

    @Autowired

    public static int calculateEditDistance(String word1, String word2) {
        int len1 = word1.length();
        int len2 = word2.length();

        // Create a DP table to store the results of subproblems
        int[][] dp = new int[len1 + 1][len2 + 1];

        // Fill dp[][] in a bottom-up manner
        for (int i = 0; i <= len1; i++) {
            for (int j = 0; j <= len2; j++) {
                // If the first word is empty, insert all characters of the second word
                if (i == 0) {
                    dp[i][j] = j; // j insertions
                }
                // If the second word is empty, remove all characters of the first word
                else if (j == 0) {
                    dp[i][j] = i; // i deletions
                }
                // If the last characters of both words are the same, ignore the last character
                else if (word1.charAt(i - 1) == word2.charAt(j - 1)) {
                    dp[i][j] = dp[i - 1][j - 1];
                }
                // If the last characters are different, consider all possibilities
                else {
                    dp[i][j] = 1 + Math.min(dp[i - 1][j], // Remove
                            Math.min(dp[i][j - 1],    // Insert
                                    dp[i - 1][j - 1])); // Replace
                }
            }
        }

        // The final value in dp[len1][len2] will be the answer
        return dp[len1][len2];
    }

    public static String getCorrectWord(String misspelledWord) {
        String filePath = "words.txt"; // Path to your words.txt file
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            StringBuilder dictionaryBuilder = new StringBuilder();
            while ((line = br.readLine()) != null) {
                dictionaryBuilder.append(line).append("\n");
            }

            String[] dictionary = dictionaryBuilder.toString().split("\n");

            // Check if the word already exists in the dictionary
            for (String word : dictionary) {
                if (word.equalsIgnoreCase(misspelledWord)) {
                    return null; // Return null if the word exists
                }
            }

            String closestWord = null;
            int minDistance = Integer.MAX_VALUE;

            // Iterate over each word in the dictionary
            for (String word : dictionary) {
                int distance = calculateEditDistance(misspelledWord, word);
                if (distance < minDistance) {
                    minDistance = distance;
                    closestWord = word;
                }
            }

            // Return the closest word
            return closestWord;

        } catch (IOException e) {
            logger.error("Error reading the file: {}", e.getMessage());
        }
        return null;
    }

    public static List<String> getSpellCheck(String misspelledWord) {
        String filePath = "words.txt"; // Path to your words.txt file
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            StringBuilder dictionaryBuilder = new StringBuilder();
            while ((line = br.readLine()) != null) {
                dictionaryBuilder.append(line).append("\n");
            }

            String[] dictionary = dictionaryBuilder.toString().split("\n");

            // Check if the word is already in the dictionary
            for (String word : dictionary) {
                if (word.equalsIgnoreCase(misspelledWord)) {
                    // Return empty list if the word is found
                    return Collections.emptyList();
                }
            }

            // List to store the closest words and their distances
            List<Map.Entry<String, Integer>> wordDistances = new ArrayList<>();

            // Iterate over each word in the dictionary
            for (String word : dictionary) {
                int distance = calculateEditDistance(misspelledWord, word);
                wordDistances.add(new AbstractMap.SimpleEntry<>(word, distance));
            }

            // Sort the words by their edit distance (ascending order)
            wordDistances.sort(Map.Entry.comparingByValue());

            // Collect the top 3 closest words
            List<String> closestWords = new ArrayList<>();
            for (int i = 0; i < Math.min(3, wordDistances.size()); i++) {
                closestWords.add(wordDistances.get(i).getKey());
            }

            return closestWords;

        } catch (IOException e) {
            logger.error("Error reading the file: {}", e.getMessage());
        }
        return Collections.emptyList(); // Return an empty list if error occurs
    }
}