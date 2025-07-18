package com.advance.real.controller;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import io.github.bonigarcia.wdm.WebDriverManager;

@RestController
@RequestMapping("/api/netflix")
public class NetflixPlansController {

    @GetMapping("/plans")
    public List<Map<String, String>> getNetflixPlans() {
        System.out.println("Netflix plans API called...");

        // Set up Selenium WebDriver
        WebDriverManager.chromedriver().setup();

        // Configure headless mode
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless"); // Run in headless mode
        options.addArguments("--disable-gpu"); // Disable GPU acceleration
        options.addArguments("--window-size=1920,1080"); // Set screen resolution

        // Initialize the WebDriver
        WebDriver driver = new ChromeDriver(options);


        List<Map<String, String>> plans = new ArrayList<>();
        try {
            // Open Netflix help page
            driver.get("https://help.netflix.com/en/node/24926");

            // Step 1: Extract prices from the separate list outside the table
            Map<String, String> planPrices = new HashMap<>();
            List<WebElement> priceListItems = driver.findElements(By.cssSelector("ul li.export-block__parent"));

            for (WebElement item : priceListItems) {
                try {
                    String planName = item.findElement(By.cssSelector("span.doc-editor__marks__bold")).getText();
                    String fullPriceText = item.findElement(By.cssSelector("p")).getText().split(":")[1].trim();
                    String mainPrice = fullPriceText.replaceAll("(\\d+\\.\\d+ CAD / month).*", "$1");
                    planPrices.put(planName, mainPrice);
                } catch (Exception e) {
                    System.out.println("Error extracting price for one of the plans.");
                }
            }

            // Step 2: Locate the table containing the plan features
            WebElement table = driver.findElement(By.cssSelector("div.doc-editor__table-plugin__table-container table"));

            // Get all rows in the table
            List<WebElement> rows = table.findElements(By.cssSelector("tbody tr"));

            // Step 3: Loop through each row to extract plan details and match with prices
            for (int i = 1; i < rows.size(); i++) {
                WebElement row = rows.get(i);
                String planName = row.findElement(By.cssSelector("td:nth-child(1) div p span")).getText();
                String price = planPrices.getOrDefault(planName, "N/A");

                WebElement featuresElement = row.findElement(By.cssSelector("td:nth-child(2) div"));
                List<WebElement> featuresList = featuresElement.findElements(By.cssSelector("ul li"));

                String adSupported = "No";
                String limitedMovies = "No";
                String supportedWatchDevices = "N/A";
                String videoQuality = "N/A";
                String supportedDownloadDevices = "N/A";
                String extraMembers = "None";
                String spatialAudio = "No";
                String extraMemberPrice = "N/A";

                for (WebElement feature : featuresList) {
                    String featureText = feature.getText();
                    if (featureText.contains("Ad-supported")) {
                        adSupported = "Yes";
                    } else if (featureText.contains("Unlimited ad-free movies")) {
                        limitedMovies = "No";
                    } else if (featureText.contains("Watch on")) {
                        supportedWatchDevices = featureText.replaceAll("[^0-9]", "");
                    } else if (featureText.contains("1080p")) {
                        videoQuality = "1080p (Full HD)";
                    } else if (featureText.contains("4K")) {
                        videoQuality = "4K (Ultra HD) + HDR";
                    } else if (featureText.contains("Download on")) {
                        supportedDownloadDevices = featureText.replaceAll("[^0-9]", "");
                    } else if (featureText.contains("extra member")) {
                        extraMembers = featureText.replaceAll("[^0-9]", "");
                        extraMemberPrice = "7.99 CAD / month"; // Static info
                    } else if (featureText.contains("Netflix spatial audio")) {
                        spatialAudio = "Yes";
                    }
                }

                // Store details in a map
                Map<String, String> planDetails = new HashMap<>();
                planDetails.put("id", String.valueOf(i));
                planDetails.put("planName", planName);
                planDetails.put("price", price);
                planDetails.put("adSupported", adSupported);
                planDetails.put("limitedMovies", limitedMovies);
                planDetails.put("supportedWatchDevices", supportedWatchDevices);
                planDetails.put("videoQuality", videoQuality);
                planDetails.put("supportedDownloadDevices", supportedDownloadDevices);
                planDetails.put("extraMembers", extraMembers);
                planDetails.put("extraMemberPrice", extraMemberPrice);
                planDetails.put("spatialAudio", spatialAudio);

                plans.add(planDetails);
                System.out.println(planDetails);
            }
        } finally {
            // Close the browser
            driver.quit();
        }
        return plans;
    }

    @GetMapping("/keyword-frequency")
    public ResponseEntity<Map<String, Object>> getKeywordFrequency(@RequestParam String keyword) {
        Map<String, Object> result = new HashMap<>();

        // Check if the 'text_pages' directory exists
        Path textPagesDir = Paths.get("text_pages");
        if (!Files.exists(textPagesDir)) {
            result.put("message", "text_pages directory not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(result);
        }

        // Load URLs from discovered_urls.txt
        Map<Integer, String> urlMap = loadUrlsFromFile();
        if (urlMap.isEmpty()) {
            result.put("message", "discovered_urls.txt is empty or missing");
            return ResponseEntity.ok(result);
        }

        // Get all text files in the directory
        File directory = textPagesDir.toFile();
        File[] files = directory.listFiles((dir, name) -> name.endsWith(".txt"));
        if (files == null || files.length == 0) {
            result.put("message", "No text files found in text_pages directory");
            return ResponseEntity.ok(result);
        }

        // Sort files based on the numeric part of their names
        Arrays.sort(files, (f1, f2) -> {
            String name1 = f1.getName().replaceAll("[^0-9]", "");
            String name2 = f2.getName().replaceAll("[^0-9]", "");
            return Integer.compare(Integer.parseInt(name1), Integer.parseInt(name2));
        });

        List<Map<String, Object>> fileResults = new ArrayList<>();

        // Process each file
        for (File file : files) {
            int pageNumber = Integer.parseInt(file.getName().replaceAll("[^0-9]", ""));
            String url = urlMap.getOrDefault(pageNumber, "Unknown URL");

            Map<String, Object> fileResult = new HashMap<>();
            fileResult.put("file", file.getName());
            fileResult.put("url", url); // Add the URL from discovered_urls.txt
            fileResult.put("count", getKeywordCount(file, keyword));
            fileResults.add(fileResult);
        }

        result.put("data", fileResults);
        return ResponseEntity.ok(result);
    }

    // Helper method to load URLs from discovered_urls.txt
    private Map<Integer, String> loadUrlsFromFile() {
        Map<Integer, String> urlMap = new LinkedHashMap<>();
        Path filePath = Paths.get("discovered_urls.txt");

        if (Files.exists(filePath)) {
            try (BufferedReader reader = Files.newBufferedReader(filePath)) {
                String line;
                int pageNumber = 1;
                while ((line = reader.readLine()) != null) {
                    urlMap.put(pageNumber++, line.trim());
                }
            } catch (IOException e) {
                System.err.println("Error reading discovered_urls.txt: " + e.getMessage());
            }
        }
        return urlMap;
    }

private int getKeywordCount(File file, String keyword) {
    int count = 0;
    try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
        String line;
        while ((line = reader.readLine()) != null) {
            count += (line.split("\\b" + keyword + "\\b", -1).length - 1);
        }
    } catch (IOException e) {
        System.err.println("Error reading file: " + file.getName() + " - " + e.getMessage());
    }
    return count;
}

}
