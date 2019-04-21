package com.android.mediaplayer.entity;

/**
 * Created by zzw on 2019/4/17.
 */

public class WebMusic {
    private int id;

        private String title;
        //歌唱者
        private String artist;
        //专辑名
        private  String album;
        private  int length;
        //专辑图片
        private String albumBip;
        private String path;
        private String lrcPath;

        public WebMusic(){

        }



        public WebMusic(int id, String title, String artist, String album, int length, String albumBip, String path, String lrcPath) {
            this.id = id;
            this.title = title;
            this.artist = artist;
            this.album = album;
            this.length = length;
            this.albumBip = albumBip;
            this.path = path;
            this.lrcPath = lrcPath;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getArtist() {
            return artist;
        }

        public void setArtist(String artist) {
            this.artist = artist;
        }

        public String getAlbum() {
            return album;
        }

        public void setAlbum(String album) {
            this.album = album;
        }

        public int getLength() {
            return length;
        }

        public void setLength(int length) {
            this.length = length;
        }

        public String getAlbumBip() {
            return albumBip;
        }

        public void setAlbumBip(String albumBip) {
            this.albumBip = albumBip;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getPath() {
            return path;
        }

        public void setPath(String path) {

            this.path = path;
        }

        public String getLrcPath() {
            return lrcPath;
        }

        public void setLrcPath(String lrcPath) {
            this.lrcPath = lrcPath;
        }
}
