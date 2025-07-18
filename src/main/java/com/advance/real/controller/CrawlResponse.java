package com.advance.real.controller;

import java.util.Set;

public class CrawlResponse {

    private Set<String> links;
    private String statusMessage;

    public CrawlResponse(Set<String> links, String statusMessage) {
        this.links = links;
        this.statusMessage = statusMessage;
    }

    // Getters and setters
    public Set<String> getLinks() {
        return links;
    }

    public void setLinks(Set<String> links) {
        this.links = links;
    }

    public String getStatusMessage() {
        return statusMessage;
    }

    public void setStatusMessage(String statusMessage) {
        this.statusMessage = statusMessage;
    }
}
