package com.android.mediaplayer.entity;


/**
 * Created by zzw on 2019/3/17.
 */

public class MyFile {

    private int imageId;
    private String fileName;
    private String filePath;
    private int fileType;

    public MyFile(String fileName, int imageId, String filePath,int fileType) {
        super();
        this.fileName = fileName;
        this.imageId = imageId;
        this.filePath=filePath;
        this.fileType=fileType;
    }

    public String getFileName() {
        return fileName;
    }

    public int getImageId() {
        return imageId;
    }

    public String getFilePath() {
        return filePath;
    }

    public int getFileType() {
        return fileType;
    }
}
