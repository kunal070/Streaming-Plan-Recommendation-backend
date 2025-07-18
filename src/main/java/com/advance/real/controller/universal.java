package com.advance.real.controller;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api") // Base URL for the API
public class  universal{

    private final Map<String, Integer> wordMap = new HashMap<>();

    @PostMapping("/analyze-file")
    public ResponseEntity<Map<String, Object>> analyzeFile(@RequestParam("file") MultipartFile file) {
        Map<String, Object> result = new HashMap<>();

        if (file.isEmpty()) {
            result.put("message", "File is empty");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
        }

        // Save the uploaded file to a temporary location
        File tempFile = new File(System.getProperty("java.io.tmpdir"), file.getOriginalFilename());
        try {
            file.transferTo(tempFile);

            // Read the file content into a String
            String content = new String(Files.readAllBytes(tempFile.toPath()));

            // Define regex patterns for URL, email, phone, and date
            Map<String, List<String>> extractedData = new HashMap<>();
            extractedData.put("urls", extractData(content, "(https?://[\\w.-]+(/[^\\s]*)?|www\\.[\\w.-]+)"));
            extractedData.put("emails", extractData(content, "\\b[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}\\b"));
            extractedData.put("phone_numbers", extractData(content, "\\+?\\d{1,4}[\\s-]?\\(?\\d{2,3}\\)?[\\s-]?\\d{3,4}[\\s-]?\\d{4}"));
            extractedData.put("dates", extractData(content, "\\b\\d{4}[-/]\\d{2}[-/]\\d{2}\\b|\\b\\d{2}/\\d{2}/\\d{4}\\b|\\b\\d{2}-\\d{2}-\\d{4}\\b|\\b\\w+ \\d{1,2}, \\d{4}\\b"));

            result.put("data", extractedData);
            return ResponseEntity.ok(result);
        } catch (IOException e) {
            result.put("message", "Failed to process file: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(result);
        }
    }

    /**
     * Endpoint to update and retrieve the frequency of a word.
     * Hashmap is being used here
     */
    @GetMapping("/search-frequency")
    public Map<String, Integer> searchWordFrequency(@RequestParam String word) {
        if (word == null || word.isEmpty()) {
            throw new IllegalArgumentException("Word parameter is missing or empty.");
        }
        System.out.println("Word received: " + word);
        // Update word frequency in the map
        wordMap.put(word, wordMap.getOrDefault(word, 0) + 1);

        // Prepare the response with the word count
        Map<String, Integer> response = new HashMap<>();
        response.put("count", wordMap.get(word));
        return response;
    }

    //Brute Force is being used here
    @PostMapping("/page-ranking")
    public Map<String, Integer> searchWordFrequencyInverted(@RequestParam String word) throws IOException {
        // Directory containing the text files
        String directoryPath = "text_pages";
        File directory = new File(directoryPath);

        if (!directory.isDirectory()) {
            throw new IllegalArgumentException("Invalid directory path: " + directoryPath);
        }

        // Map to store URLs and their respective word frequency
        Map<String, Integer> urlWordFrequency = new LinkedHashMap<>();

        List<String> urls = Files.readAllLines(Paths.get("discovered_urls.txt"));
        Collections.sort(urls);
        String[] urlArray = urls.toArray(new String[0]);

        // Iterate through all text files in the directory
        File[] files = Objects.requireNonNull(directory.listFiles((dir, name) -> name.endsWith(".txt")));

// Sort files for consistent indexing, if needed
        Arrays.sort(files);

        for (int i = 0; i < files.length; i++) {
            File file = files[i];
            if (file.isFile()) {
                String originalFileName = file.getName();
                int wordCount = countWordInFile(file, word);

                // Convert the file name to a URL
                String url = convertFileNameToUrl(originalFileName);

                // Use index `i` as needed
                System.out.println("Index: " + i + ", URL: " + url + ", Word Count: " + wordCount);
                urlWordFrequency.put(urlArray[i], wordCount);
            }
        }

        // Sort by word frequency in descending order
        return urlWordFrequency.entrySet()
                .stream()
                .sorted((e1, e2) -> e2.getValue().compareTo(e1.getValue()))
                .collect(
                        LinkedHashMap::new,
                        (map, entry) -> map.put(entry.getKey(), entry.getValue()),
                        Map::putAll
                );
    }

    // Helper method to convert file name to URL
    private String convertFileNameToUrl(String fileName) {
        // Remove the .txt extension
        String withoutExtension = fileName.replace(".txt", "");

        // Replace underscores with appropriate URL separators
        return withoutExtension.replace("_", "/").replace("https///", "https://");
    }
    
    /**
     * Counts the occurrences of a word in a given file.
     */
    private int countWordInFile(File file, String word) throws IOException {
        int count = 0;
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] words = line.split("\\W+");
                for (String w : words) {
                    if (w.equalsIgnoreCase(word)) {
                        count++;
                    }
                }
            }
        }
        return count;
    }



    /**
     * Helper function to extract data based on regex patterns.
     */
    private List<String> extractData(String content, String regex) {
        List<String> matches = new ArrayList<>();
        Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(content);
        while (matcher.find()) {
            matches.add(matcher.group().trim());
        }
        return matches;
    }
}
