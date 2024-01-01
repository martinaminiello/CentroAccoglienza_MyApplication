package com.example.centroaccoglienza_myapplication.fragments;

public class VideoModel {
    private String videoUrl;

    public VideoModel() {
        // Default constructor required for Firebase
    }

    public VideoModel(String videoUrl) {
        this.videoUrl = videoUrl;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }
}
