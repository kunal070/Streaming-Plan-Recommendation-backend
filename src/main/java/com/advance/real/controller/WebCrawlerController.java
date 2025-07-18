package com.advance.real.controller;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.advance.real.services.OttScraperService;

@RestController
@RequestMapping("/crawl")
public class WebCrawlerController {

    private final OttScraperService ottScraperService;

    // Constructor-based dependency injection
    public WebCrawlerController(OttScraperService ottScraperService) {
        this.ottScraperService = ottScraperService;
    }

    @PostMapping
    public ResponseEntity<?> handleCrawlRequest(@RequestBody CrawlRequest request) {
        String url = request.getUrl();
        System.out.println("Received URL: " + url);

        // Initialize and execute the crawler

        UWCrawler crawler = new UWCrawler();
        crawler.crawl(url);
//        crawler.saveUrlsToFile();

        // Retrieve discovered and failed URLs
        Set<String> foundUrls = crawler.getDiscoveredUrls();
        Set<String> failedUrls = crawler.getFailedUrls();

        // Return the discovered URLs along with any failed URLs
        if (!failedUrls.isEmpty()) {
            return ResponseEntity.ok(new CrawlResponse(foundUrls, "Some URLs failed to crawl: " + failedUrls));
        } else {
            return ResponseEntity.ok(new CrawlResponse(foundUrls, "Crawling completed successfully."));
        }
    }

    @GetMapping("/words")
    public ResponseEntity<Map<String, String>> getCorrectWord(@RequestParam(value = "word", required = true) String word) throws IOException {
        String correctWord = OttScraperService.getCorrectWord(word);
    
        // Create a map to return the word as a JSON object
        Map<String, String> response = new HashMap<>();
        response.put("recommendedWord", correctWord);
    
        // Return the response as JSON with a 200 OK status
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/spell-checking")
    public ResponseEntity<Map<String, Object>> getSpellChecking(@RequestParam(value = "word", required = true) String word) throws IOException {
        // Get the list of suggested words from the getSpellCheck method
        List<String> correctWords = OttScraperService.getSpellCheck(word);

        // Create a map to return the words as a JSON object
        Map<String, Object> response = new HashMap<>();
        response.put("recommendedWords", correctWords); // Return list of suggested words

        // Return the response as JSON with a 200 OK status
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/frequency-count")
    public ResponseEntity<Map<String, Object>> getKeywordFrequencyCount(@RequestParam String keyword) {
        Map<String, Object> result = new HashMap<>();

        // Check if the 'text_pages' directory exists
        Path textPagesDir = Paths.get("text_pages");
        if (!Files.exists(textPagesDir)) {
            result.put("message", "text_pages directory not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(result);
        }

        // Get all text files in the directory
        File directory = textPagesDir.toFile();
        File[] files = directory.listFiles((dir, name) -> name.endsWith(".txt"));
        if (files == null || files.length == 0) {
            result.put("message", "No text files found in text_pages directory");
            return ResponseEntity.ok(result);
        }

        List<Map<String, Object>> fileResults = new ArrayList<>();

        // Process each file
        for (File file : files) {
            AVLNode root = null; // AVL tree root node for the current file

            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    // Split line into words and insert each word into the AVL tree
                    String[] words = line.split("\\W+"); // Split by non-word characters
                    for (String word : words) {
                        if (!word.isEmpty()) {
                            root = insert(root, word.toLowerCase()); // Insert each word
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            // Get the frequency of the keyword from the AVL tree
            int count = search(root, keyword.toLowerCase());

            Map<String, Object> fileResult = new HashMap<>();
            fileResult.put("file", file.getName());
            fileResult.put("count", count);
            fileResults.add(fileResult);
        }

        // Sort the results by count in descending order
        fileResults.sort((a, b) -> Integer.compare((int) b.get("count"), (int) a.get("count")));

        result.put("data", fileResults);
        return ResponseEntity.ok(result);
    }

    // AVL Node definition
    class AVLNode {
        String word;
        int count;
        int height;
        AVLNode left, right;

        AVLNode(String word) {
            this.word = word;
            this.count = 1;
            this.height = 1;
        }
    }

    // Helper methods for AVL tree
    private AVLNode insert(AVLNode node, String word) {
        if (node == null) {
            return new AVLNode(word);
        }

        if (word.compareTo(node.word) < 0) {
            node.left = insert(node.left, word);
        } else if (word.compareTo(node.word) > 0) {
            node.right = insert(node.right, word);
        } else {
            node.count++; // Increment count if the word already exists
            return node;
        }

        node.height = 1 + Math.max(height(node.left), height(node.right));

        // Get the balance factor to check if this node became unbalanced
        int balance = getBalance(node);

        // Perform rotations to balance the tree
        if (balance > 1 && word.compareTo(node.left.word) < 0) {
            return rotateRight(node);
        }
        if (balance < -1 && word.compareTo(node.right.word) > 0) {
            return rotateLeft(node);
        }
        if (balance > 1 && word.compareTo(node.left.word) > 0) {
            node.left = rotateLeft(node.left);
            return rotateRight(node);
        }
        if (balance < -1 && word.compareTo(node.right.word) < 0) {
            node.right = rotateRight(node.right);
            return rotateLeft(node);
        }

        return node;
    }

    private int search(AVLNode node, String word) {
        while (node != null) {
            if (word.equals(node.word)) {
                return node.count;
            } else if (word.compareTo(node.word) < 0) {
                node = node.left;
            } else {
                node = node.right;
            }
        }
        return 0; // Word not found
    }

    private int height(AVLNode node) {
        return node == null ? 0 : node.height;
    }

    private int getBalance(AVLNode node) {
        return node == null ? 0 : height(node.left) - height(node.right);
    }

    private AVLNode rotateRight(AVLNode y) {
        AVLNode x = y.left;
        AVLNode T = x.right;

        x.right = y;
        y.left = T;

        y.height = Math.max(height(y.left), height(y.right)) + 1;
        x.height = Math.max(height(x.left), height(x.right)) + 1;

        return x;
    }

    private AVLNode rotateLeft(AVLNode x) {
        AVLNode y = x.right;
        AVLNode T = y.left;

        y.left = x;
        x.right = T;

        x.height = Math.max(height(x.left), height(x.right)) + 1;
        y.height = Math.max(height(y.left), height(y.right)) + 1;

        return y;
    }

//    Trie Algorithm
    @GetMapping("/inverted-index")
    public ResponseEntity<List<Map<String, Object>>> getInvertedIndex(@RequestParam String keyword) throws IOException {
        // Create a new Trie for storing words and their locations
        Trie trie = new Trie();

        // Path to the directory containing text files
        Path textPagesDir = Paths.get("text_pages");

        // Check if the directory exists
        if (!Files.exists(textPagesDir)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Collections.singletonList(Map.of("error", "Directory 'text_pages' not found")));
        }

        // Get all text files in the directory
        File[] files = textPagesDir.toFile().listFiles((dir, name) -> name.endsWith(".txt"));
        if (files == null || files.length == 0) {
            return ResponseEntity.ok(Collections.singletonList(Map.of("message", "No text files found in 'text_pages' directory")));
        }

        List<String> urls = Files.readAllLines(Paths.get("discovered_urls.txt"));
        Collections.sort(urls);
        String[] urlArray = urls.toArray(new String[0]);

        // Process each file
        for (int fileIndex = 0; fileIndex < files.length; fileIndex++) {
            File file = files[fileIndex];
            try {
                List<String> lines = Files.readAllLines(file.toPath()); // Read file line by line
                for (int lineIndex = 0; lineIndex < lines.size(); lineIndex++) {
                    String line = lines.get(lineIndex);
                    String[] words = line.split("\\s+"); // Split line into words

                    for (int position = 0; position < words.length; position++) {
                        String word = words[position].toLowerCase(); // Case-insensitive match

                        // Insert each word into the Trie with its position
                        trie.insert(word, fileIndex + 1, lineIndex + 1, position + 1, file.getName());
                    }
                }
            } catch (IOException e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(Collections.singletonList(Map.of("error", "Error reading file: " + file.getName())));
            }
        }

        // Perform search for the keyword in the Trie
        List<Map<String, Object>> results = trie.search(keyword.toLowerCase());

        return ResponseEntity.ok(results);
    }

    // TrieNode class representing a single character node
    class TrieNode {
        Map<Character, TrieNode> children = new HashMap<>();
        List<Map<String, Object>> positions = new ArrayList<>();

        public void addPosition(Map<String, Object> position) {
            positions.add(position);
        }
    }

    // Trie class to handle word insertion and searching
    class Trie {
        private final TrieNode root = new TrieNode();

        // Insert a word into the Trie along with its position data
        public void insert(String word, int fileIndex, int lineIndex, int position, String fileName) {
            TrieNode currentNode = root;
            for (char c : word.toCharArray()) {
                currentNode = currentNode.children.computeIfAbsent(c, k -> new TrieNode());
            }

            // Record the position where the word occurs
            Map<String, Object> positionMap = new HashMap<>();
            positionMap.put("fileIndex", fileIndex);
            positionMap.put("lineIndex", lineIndex);
            positionMap.put("position", position);
            positionMap.put("fileName", fileName);

            currentNode.addPosition(positionMap);
        }

        // Search for a word in the Trie and return its positions
        public List<Map<String, Object>> search(String word) {
            TrieNode currentNode = root;
            for (char c : word.toCharArray()) {
                currentNode = currentNode.children.get(c);
                if (currentNode == null) {
                    return Collections.emptyList(); // Word not found
                }
            }
            return currentNode.positions;
        }
    }
}
