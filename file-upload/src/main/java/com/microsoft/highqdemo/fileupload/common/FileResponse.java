package com.microsoft.highqdemo.fileupload.common;

import java.util.ArrayList;

public class FileResponse {
    private ArrayList<UploadedFile> files = new ArrayList<UploadedFile>();

    public FileResponse(String name, String url, String type, long size) {
        files.add(new UploadedFile(name, url, type, size));
    }

    public FileResponse(ArrayList<UploadedFile> uploadedFiles) {
        files.addAll(uploadedFiles);
    }

    public FileResponse(UploadedFile uploadedFile) {
        files.add(uploadedFile);
    }

    public ArrayList<UploadedFile> getFiles() {
        return files;
    }
}
