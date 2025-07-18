package com.advance.real.controller;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class UWCrawler {

    private static final int MAX_VISIT_LIMIT = 10; // Limit of pages to visit
    private Set<String> discoveredUrls;
    private int pagesVisited;
    private Set<String> failedUrls;  // To store URLs that failed to connect

    public UWCrawler() {
        this.discoveredUrls = new HashSet<>();
        this.pagesVisited = 0;
        this.failedUrls = new HashSet<>();
    }

    public void crawl(String url) {
        try {
            deleteFileIfExists(); // Remove discovered_urls.txt if it exists
            deleteDirectoryIfExists("text_pages"); // Remove text_pages directory if it exists
            deleteDirectoryIfExists("html_pages"); // Remove html_pages directory if it exists
            visitPage(url); // Start crawling
        } catch (Exception e) {
            System.err.println("Error during crawling: " + e.getMessage());
        }
    }

    private void deleteFileIfExists() {
        File file = new File("discovered_urls.txt");
        if (file.exists() && file.isFile()) {
            if (file.delete()) {
                System.out.println("Deleted file: " + "discovered_urls.txt");
            } else {
                System.err.println("Failed to delete file: " + "discovered_urls.txt");
            }
        }
    }

    private void visitPage(String url) {
        if (pagesVisited >= MAX_VISIT_LIMIT) {
            return; // Stop crawling when the limit is reached
        }

        try {
            // Remove `text_pages` and `html_pages` directories if they exist

            if (discoveredUrls.contains(url)) {
                return; // Avoid visiting the same URL twice
            }

            System.out.println("Visiting: " + url);
            discoveredUrls.add(url);
            pagesVisited++;

            // Fetch and save the webpage
            Document doc = Jsoup.connect(url).get();
            saveWebPageAsHTML(doc, pagesVisited,url);
            saveWebPageAsText(doc, pagesVisited, url);

            // Extract and visit links
            Elements links = doc.select("a[href]");
            for (Element link : links) {
                String nextUrl = link.absUrl("href");
                if (isValidUrl(nextUrl)) {
                    visitPage(nextUrl);
                    if (pagesVisited >= MAX_VISIT_LIMIT) {
                        break; // Stop further crawling if the limit is reached
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Failed to connect to URL: " + url);
            failedUrls.add(url);  // Add to failed URLs list
        }
    }

    /**
     * Deletes a directory and its contents if it exists.
     *
     * @param folderName The name of the directory to delete.
     */
    private void deleteDirectoryIfExists(String folderName) {
        File folder = new File(folderName);
        if (folder.exists()) {
            for (File file : folder.listFiles()) {
                file.delete(); // Delete individual files
            }
            folder.delete(); // Delete the directory itself
            System.out.println("Deleted existing directory: " + folderName);
        }
    }

    private boolean isValidUrl(String url) {
        return url.startsWith("http") && !url.contains("javascript:void(0)");
    }

    private void saveWebPageAsHTML(Document doc, int pageNumber, String url) {
        try {
            String sanitizedUrl = url.replaceAll("[^a-zA-Z0-9.-]", "_");
            String folderName = "html_pages";
            File folder = new File(folderName);
            if (!folder.exists()) {
                folder.mkdir(); // Create folder if it doesn't exist
            }

            String fileName = folderName + "/" + sanitizedUrl + "_page_" + pageNumber + ".txt";
            try (FileWriter writer = new FileWriter(fileName)) {
                writer.write(doc.outerHtml());
            }
            System.out.println("Saved HTML: " + fileName);
        } catch (IOException e) {
            System.err.println("Error saving HTML: " + e.getMessage());
        }
    }

    private void saveWebPageAsText(Document doc, int pageNumber, String url) {
        try {
            // Sanitize the URL to make it a valid file name
            String sanitizedUrl = url.replaceAll("[^a-zA-Z0-9.-]", "_");
            String folderName = "text_pages";
            File folder = new File(folderName);
            if (!folder.exists()) {
                folder.mkdir(); // Create folder if it doesn't exist
            }

            // Use the sanitized URL and page number for the file name
            String fileName = folderName + "/" + sanitizedUrl + "_page_" + pageNumber + ".txt";
            try (FileWriter writer = new FileWriter(fileName)) {
                // Extract text content from the document
                String textContent = doc.text();

                // Convert the text into CSV format (each word/line in separate cells/rows)
                String[] lines = textContent.split("\n");
                for (String line : lines) {
                    writer.write(line.replace(",", " ") + "\n"); // Escape commas
                }
            }
            this.saveUrlsToFile(url);
            System.out.println("Saved Text: " + fileName);
        } catch (IOException e) {
            System.err.println("Error saving CSV: " + e.getMessage());
        }
    }

    public void saveUrlsToFile(String url) {
        try (FileWriter writer = new FileWriter("discovered_urls.txt", true)) { // 'true' enables append mode
            writer.write(url + "\n");
            System.out.println("URL saved to file: " + url);
        } catch (IOException e) {
            System.err.println("Error writing URL to file: " + e.getMessage());
        }
    }

    public Set<String> getDiscoveredUrls() {
        return new TreeSet<>(discoveredUrls); // TreeSet sorts the elements automatically
    }

    public Set<String> getFailedUrls() {
        return failedUrls;  // Return the list of failed URLs
    }
}