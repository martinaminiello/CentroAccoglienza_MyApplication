package com.example.centroaccoglienza_myapplication.fragments;

public class UploadedFile {
    private String fileName;
    private String fileUrl;

    public UploadedFile() {
        // Default constructor required for calls to DataSnapshot.getValue(UploadedFile.class)
    }

    public UploadedFile(String fileName, String fileUrl) {
        this.fileName = fileName;
        this.fileUrl = fileUrl;
    }

    public String getFileName() {
        return fileName;
    }

    public String getFileUrl() {
        return fileUrl;
    }
}
