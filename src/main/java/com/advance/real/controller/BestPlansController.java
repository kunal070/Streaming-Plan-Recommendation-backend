package com.advance.real.controller;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.FileReader;
import java.io.Reader;
import java.util.*;

@RestController
@RequestMapping("/api")
public class BestPlansController {

    // Helper method to compare video quality (Highest quality first)
    private int compareVideoQuality(String videoQuality1, String videoQuality2) {
        Map<String, Integer> qualityRank = new HashMap<>();
        qualityRank.put("1080p (Full HD)", 1);
        qualityRank.put("4K (Ultra HD)", 2);
        qualityRank.put("4K (Ultra HD) + HDR", 3);

        return Integer.compare(qualityRank.getOrDefault(videoQuality2, 0), qualityRank.getOrDefault(videoQuality1, 0)); // Reverse order for highest quality first
    }

    // Helper method to compare based on preference (Netflix > Prime > Disney)
    private int compareStreamingService(String service1, String service2) {
        List<String> preference = Arrays.asList("Netflix", "Prime", "Disney");
        return Integer.compare(preference.indexOf(service1), preference.indexOf(service2));
    }

    @GetMapping("/best/price")
    public Object[] getBestPricePlan() {
        List<PlanDTO> plans = new ArrayList<>();

        try {
            // Path to your CSV file
            String csvFilePath = "Combined.csv"; // Modify this path to your file location

            // Read the CSV file
            Reader reader = new FileReader(csvFilePath);
            CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT.withFirstRecordAsHeader());

            // Load all records into a list
            for (CSVRecord record : csvParser) {
                PlanDTO planDetails = new PlanDTO();
                planDetails.setPlan(record.get("plan"));
                planDetails.setPrice(record.get("price"));
                planDetails.setAdSupported(record.get("ad-supported"));
                planDetails.setVideoQuality(record.get("videoQuality"));
                planDetails.setSpatialAudio(record.get("spatialAudio"));
                planDetails.setWatchDevice(record.get("watchDevice"));
                planDetails.setSupportedDownloadDevices(record.get("supportedDownloadDevices"));
                planDetails.setStreamingService(record.get("streamingService"));

                plans.add(planDetails);
            }

            // Sort the plans by price, then by video quality, and finally by streaming service preference
            plans.sort((plan1, plan2) -> {
                // Compare by price (lowest first)
                double price1 = parsePrice(plan1.getPrice());
                double price2 = parsePrice(plan2.getPrice());
                int priceComparison = Double.compare(price1, price2);

                if (priceComparison != 0) {
                    return priceComparison; // If prices are different, return the comparison result
                }

                // If prices are the same, compare by video quality
                int videoQualityComparison = compareVideoQuality(plan1.getVideoQuality(), plan2.getVideoQuality());
                if (videoQualityComparison != 0) {
                    return videoQualityComparison; // If video quality is different, return the comparison result
                }

                // If both price and video quality are equal, compare by streaming service preference
                return compareStreamingService(plan1.getStreamingService(), plan2.getStreamingService());
            });

        } catch (Exception e) {
            e.printStackTrace();
        }

        // Return the best plan (the first in the sorted list)
        return plans.stream()
                .limit(3) // Take only the first 3 objects
                .toArray(Object[]::new); // Convert the stream to an array
         }

    @GetMapping("/best/videoquality")
    public Object[] getBestVideoQualityPlan() {
        List<PlanDTO> plans = new ArrayList<>();

        try {
            // Path to your CSV file
            String csvFilePath = "Combined.csv";

            // Read the CSV file
            Reader reader = new FileReader(csvFilePath);
            CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT.withFirstRecordAsHeader());

            // Load all records into a list
            for (CSVRecord record : csvParser) {
                PlanDTO planDetails = new PlanDTO();
                planDetails.setPlan(record.get("plan"));
                planDetails.setPrice(record.get("price"));
                planDetails.setAdSupported(record.get("ad-supported"));
                planDetails.setVideoQuality(record.get("videoQuality"));
                planDetails.setSpatialAudio(record.get("spatialAudio"));
                planDetails.setWatchDevice(record.get("watchDevice"));
                planDetails.setSupportedDownloadDevices(record.get("supportedDownloadDevices"));
                planDetails.setStreamingService(record.get("streamingService"));

                plans.add(planDetails);
            }

            // Sort the plans by video quality (highest first), then by price, and finally by streaming service preference
            plans.sort((plan1, plan2) -> {
                // Compare by video quality
                int videoQualityComparison = compareVideoQuality(plan1.getVideoQuality(), plan2.getVideoQuality());
                if (videoQualityComparison != 0) {
                    return videoQualityComparison; // If video quality is different, return the comparison result
                }

                // If video quality is the same, compare by price
                double price1 = parsePrice(plan1.getPrice());
                double price2 = parsePrice(plan2.getPrice());
                int priceComparison = Double.compare(price1, price2);

                if (priceComparison != 0) {
                    return priceComparison; // If prices are different, return the comparison result
                }

                // If both video quality and price are equal, compare by streaming service preference
                return compareStreamingService(plan1.getStreamingService(), plan2.getStreamingService());
            });

        } catch (Exception e) {
            e.printStackTrace();
        }

        // Return the best plan (the first in the sorted list)
        return plans.stream()
                .limit(3) // Take only the first 3 objects
                .toArray(Object[]::new);
    }

    // Helper method to parse the price from the string (e.g., "5.99 CAD/month")
    private double parsePrice(String price) {
        try {
            String numericPrice = price.split(" ")[0]; // Remove "CAD/month" part
            return Double.parseDouble(numericPrice);
        } catch (NumberFormatException e) {
            return Double.MAX_VALUE; // Return a very high value if the price is not valid
        }
    }

    // Convert PlanDTO to a Map for response
    private Map<String, String> convertToMap(PlanDTO planDTO) {
        Map<String, String> planMap = new HashMap<>();
        planMap.put("plan", planDTO.getPlan());
        planMap.put("price", planDTO.getPrice());
        planMap.put("adSupported", planDTO.getAdSupported());
        planMap.put("videoQuality", planDTO.getVideoQuality());
        planMap.put("spatialAudio", planDTO.getSpatialAudio());
        planMap.put("watchDevice", planDTO.getWatchDevice());
        planMap.put("supportedDownloadDevices", planDTO.getSupportedDownloadDevices());
        planMap.put("streamingService", planDTO.getStreamingService());
        return planMap;
    }
}
