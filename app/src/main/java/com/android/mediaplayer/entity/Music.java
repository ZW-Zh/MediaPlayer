package com.android.mediaplayer.entity;

import android.graphics.Bitmap;

/**
 * Created by shaoyangyang on 2017/12/18.
 */

public class Music {
    /**
     * 在这里所有的属性都是用public修饰的，所以在以后调用时直接调用就可以了
     * 如果用private修饰是需要构建set和get方法
     */
    //歌名
    private String title;
    //歌唱者
    private String artist;
    //专辑名
    private  String album;
    private  int length;
    //专辑图片
    private Bitmap albumBip;
    private String path;
    private String lrcPath;

    public boolean isPlaying;

    private int id;

    private boolean like;

    public void setLike(boolean like) {
        this.like = like;
    }

    public boolean isLike() {
        return like;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getArtist() {
        return artist;
    }

    public String getAlbum() {
        return album;
    }

    public int getLength() {
        return length;
    }

    public Bitmap getAlbumBip() {
        return albumBip;
    }

    public String getPath() {
        return path;
    }

    public boolean isPlaying() {
        return isPlaying;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public void setAlbumBip(Bitmap albumBip) {
        this.albumBip = albumBip;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public void setPlaying(boolean playing) {
        isPlaying = playing;
    }

    public String getLrcPath() {
        return lrcPath;
    }

    public void setLrcPath(String lrcPath) {
        this.lrcPath = lrcPath;
    }

    @Override
    public String toString() {
        return "Music{" +
                "title='" + title + '\'' +
                ", artist='" + artist + '\'' +
                ", album='" + album + '\'' +
                ", length=" + length +
                ", albumBip=" + albumBip +
                ", path='" + path + '\'' +
                ", lrcPath='" + lrcPath + '\'' +
                ", isPlaying=" + isPlaying +
                ", id=" + id +
                ", like=" + like +
                '}';
    }
}
