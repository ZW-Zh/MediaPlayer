package com.android.mediaplayer.utils;

/**
 * Created by zzw on 2019/4/17.
 */

public class FileUtil {
    public static String getFileSize(long size){
        String fileSize;
        //判断文件大小
            if (size / 1024 / 1024 / 1024 > 0) {
                fileSize = size / 1024 / 1024 / 1024 + " G";
            } else if (size / 1024 / 1024 > 0) {
                fileSize = size / 1024 / 1024 + " M";
            } else if (size / 1024 > 0) {
                fileSize = size / 1024 + " K";
            } else {
                fileSize = size + " B";
            }
        return fileSize;

    }
    public static String getTotalTime(long totalTime){
        long second = totalTime / 1000;
        long hh = second / 3600;
        long mm = second % 3600 / 60;
        long ss = second % 60;
        String time = null;
        if (hh != 0) {
            time = String.format("%02d:%02d:%02d", hh, mm, ss);
        } else {
            time = String.format("%02d:%02d", mm, ss);
        }
        return time;
    }
}
