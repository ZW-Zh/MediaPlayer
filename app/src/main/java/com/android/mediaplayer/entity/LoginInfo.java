package com.android.mediaplayer.entity;

import java.io.Serializable;
import java.util.List;

/**
 * Created by zzw on 2019/4/17.
 */

public class LoginInfo implements Serializable{
    private int userId;
    private List<Integer> musicId;

    public LoginInfo(int userId, List<Integer> musicId) {
        this.userId = userId;
        this.musicId = musicId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public List<Integer> getMusicId() {
        return musicId;
    }

    public void setMusicId(List<Integer> musicId) {
        this.musicId = musicId;
    }
}
