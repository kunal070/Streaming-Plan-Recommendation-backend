// package com.advance.real.controller;

// import org.apache.commons.csv.CSVFormat;
// import org.apache.commons.csv.CSVParser;
// import org.apache.commons.csv.CSVRecord;
// import org.springframework.web.bind.annotation.GetMapping;
// import org.springframework.web.bind.annotation.RequestMapping;
// import org.springframework.web.bind.annotation.RestController;

// import java.io.FileReader;
// import java.io.Reader;
// import java.util.*;

// @RestController
// @RequestMapping("/api")
// public class BestVideoController {

//     // Helper method to compare video quality (Highest quality first)
//     private int compareVideoQuality(String videoQuality1, String videoQuality2) {
//         Map<String, Integer> qualityRank = new HashMap<>();
//         qualityRank.put("1080p (Full HD)", 1);
//         qualityRank.put("4K (Ultra HD)", 2);
//         qualityRank.put("4K (Ultra HD) + HDR", 3);

//         return Integer.compare(qualityRank.getOrDefault(videoQuality2, 0), qualityRank.getOrDefault(videoQuality1, 0)); // Reverse order for highest quality first
//     }

//     // Helper method to compare based on preference (Netflix > Prime > Disney)
//     private int compareStreamingService(String service1, String service2) {
//         List<String> preference = Arrays.asList("Netflix", "Prime", "Disney");
//         return Integer.compare(preference.indexOf(service1), preference.indexOf(service2));
//     }

//     @GetMapping("/best/videoquality")
//     public Map<String, String> getBestVideoQualityPlan() {
//         List<Map<String, String>> plans = new ArrayList<>();

//         try {
//             // Path to your CSV file
//             String csvFilePath = "C:/Users/DELL/Downloads/Combined.csv"; // Modify this path to your file location

//             // Read the CSV file
//             Reader reader = new FileReader(csvFilePath);
//             CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT.withFirstRecordAsHeader());

//             // Load all records into a list
//             for (CSVRecord record : csvParser) {
//                 Map<String, String> planDetails = new HashMap<>();
//                 planDetails.put("plan", record.get("plan"));
//                 planDetails.put("price", record.get("price"));
//                 planDetails.put("adSupported", record.get("ad-supported"));
//                 planDetails.put("videoQuality", record.get("videoQuality"));
//                 planDetails.put("spatialAudio", record.get("spatialAudio"));
//                 planDetails.put("watchDevice", record.get("watchDevice"));
//                 planDetails.put("supportedDownloadDevices", record.get("supportedDownloadDevices"));
//                 planDetails.put("streamingService", record.get("streamingService"));

//                 plans.add(planDetails);
//             }

//             // Sort the plans by video quality (highest first), then by price, and finally by streaming service preference
//             plans.sort((plan1, plan2) -> {
//                 // Compare by video quality
//                 int videoQualityComparison = compareVideoQuality(plan1.get("videoQuality"), plan2.get("videoQuality"));
//                 if (videoQualityComparison != 0) {
//                     return videoQualityComparison; // If video quality is different, return the comparison result
//                 }

//                 // If video quality is the same, compare by price
//                 double price1 = parsePrice(plan1.get("price"));
//                 double price2 = parsePrice(plan2.get("price"));
//                 int priceComparison = Double.compare(price1, price2);

//                 if (priceComparison != 0) {
//                     return priceComparison; // If prices are different, return the comparison result
//                 }

//                 // If both video quality and price are equal, compare by streaming service preference
//                 return compareStreamingService(plan1.get("streamingService"), plan2.get("streamingService"));
//             });

//         } catch (Exception e) {
//             e.printStackTrace();
//         }

//         // Return the best plan (the first in the sorted list)
//         return plans.isEmpty() ? Collections.emptyMap() : plans.get(0);
//     }

//     // Helper method to parse the price from the string (e.g., "5.99 CAD/month")
//     private double parsePrice(String price) {
//         try {
//             String numericPrice = price.split(" ")[0]; // Remove "CAD/month" part
//             return Double.parseDouble(numericPrice);
//         } catch (NumberFormatException e) {
//             return Double.MAX_VALUE; // Return a very high value if the price is not valid
//         }
//     }
// }
