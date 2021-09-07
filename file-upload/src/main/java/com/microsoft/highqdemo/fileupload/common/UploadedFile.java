package com.microsoft.highqdemo.fileupload.common;

public class UploadedFile {
    private String name;
    private String url;
    private String type;
    private long size;
    private final String deleteType = "DELETE";

    public UploadedFile(String name, String url, String type, long size) {
        this.name = name;
        this.url = url;
        this.type = type;
        this.size = size;
    }

    public String getName() {
        return this.name;
    }

    public String getUrl() {
        return this.url;
    }

    public String getType() {
        return this.type;
    }

    public long getSize() {
        return this.size;
    }

    public String getDeleteType() {
        return this.deleteType;
    }
}
