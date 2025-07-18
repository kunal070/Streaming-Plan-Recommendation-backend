package com.advance.real.controller;

public class PlanDTO {
    private String plan;
    private String price;
    private String adSupported;
    private String videoQuality;
    private String spatialAudio;
    private String watchDevice;
    private String supportedDownloadDevices;
    private String streamingService;

    // Getters and Setters
    public String getPlan() {
        return plan;
    }

    public void setPlan(String plan) {
        this.plan = plan;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getAdSupported() {
        return adSupported;
    }

    public void setAdSupported(String adSupported) {
        this.adSupported = adSupported;
    }

    public String getVideoQuality() {
        return videoQuality;
    }

    public void setVideoQuality(String videoQuality) {
        this.videoQuality = videoQuality;
    }

    public String getSpatialAudio() {
        return spatialAudio;
    }

    public void setSpatialAudio(String spatialAudio) {
        this.spatialAudio = spatialAudio;
    }

    public String getWatchDevice() {
        return watchDevice;
    }

    public void setWatchDevice(String watchDevice) {
        this.watchDevice = watchDevice;
    }

    public String getSupportedDownloadDevices() {
        return supportedDownloadDevices;
    }

    public void setSupportedDownloadDevices(String supportedDownloadDevices) {
        this.supportedDownloadDevices = supportedDownloadDevices;
    }

    public String getStreamingService() {
        return streamingService;
    }

    public void setStreamingService(String streamingService) {
        this.streamingService = streamingService;
    }
}
