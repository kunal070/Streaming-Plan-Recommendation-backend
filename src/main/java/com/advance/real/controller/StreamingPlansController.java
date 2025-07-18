package com.advance.real.controller;

import java.io.FileReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/")
public class StreamingPlansController {

    private static final String CSV_FILE_PATH = "Combined.csv";

    private List<Map<String, String>> getPlansByService(String serviceName) {
        List<Map<String, String>> plans = new ArrayList<>();

        try (Reader reader = new FileReader(CSV_FILE_PATH); 
             CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT.withFirstRecordAsHeader())) {

            for (CSVRecord record : csvParser) {
                // Filter rows based on the streaming service name
                if (serviceName.equalsIgnoreCase(record.get("streamingService"))) {
                    Map<String, String> planDetails = new HashMap<>();
                    planDetails.put("plan", record.get("plan"));
                    planDetails.put("price", record.get("price"));
                    planDetails.put("adSupported", record.get("ad-supported"));
                    planDetails.put("videoQuality", record.get("videoQuality"));
                    planDetails.put("spatialAudio", record.get("spatialAudio"));
                    planDetails.put("watchDevice", record.get("watchDevice"));
                    planDetails.put("supportedDownloadDevices", record.get("supportedDownloadDevices"));
                    planDetails.put("streamingService", record.get("streamingService"));

                    plans.add(planDetails);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return plans;
    }

    @GetMapping("/disney")
    public List<Map<String, String>> getDisneyPlans() {
        return getPlansByService("Disney");
    }

    @GetMapping("/netflix")
    public List<Map<String, String>> getNetflixPlans() {
        return getPlansByService("Netflix");
    }

    @GetMapping("/paramount")
    public List<Map<String, String>> getParamountPlans() {
        return getPlansByService("Paramount+");
    }

    @GetMapping("/youtube")
    public List<Map<String, String>> getYouTubePlans() {
        return getPlansByService("YouTube");
    }

    @GetMapping("/discovery")
    public List<Map<String, String>> getDiscoveryPlans() {
        return getPlansByService("Discovery+");
    }
}
