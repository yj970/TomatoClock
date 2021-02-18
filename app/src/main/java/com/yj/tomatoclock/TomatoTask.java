package com.yj.tomatoclock;

public class TomatoTask {
    String id;
    String tomatoTaskName ;
    String countDownTime ;
    boolean vibrateEnable ;
    boolean voiceEnable ;
    Song song;

    public TomatoTask(String id, String tomatoTaskName, String countDownTime, boolean vibrateEnable, boolean voiceEnable, Song song) {
        this.id = id;
        this.tomatoTaskName = tomatoTaskName;
        this.countDownTime = countDownTime;
        this.vibrateEnable = vibrateEnable;
        this.voiceEnable = voiceEnable;
        this.song = song;
    }

    public String getTomatoTaskName() {
        return tomatoTaskName;
    }

    public void setTomatoTaskName(String tomatoTaskName) {
        this.tomatoTaskName = tomatoTaskName;
    }

    public String getCountDownTime() {
        return countDownTime;
    }

    public void setCountDownTime(String countDownTime) {
        this.countDownTime = countDownTime;
    }

    public boolean isVibrateEnable() {
        return vibrateEnable;
    }

    public void setVibrateEnable(boolean vibrateEnable) {
        this.vibrateEnable = vibrateEnable;
    }

    public boolean isVoiceEnable() {
        return voiceEnable;
    }

    public void setVoiceEnable(boolean voiceEnable) {
        this.voiceEnable = voiceEnable;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Song getSong() {
        return song;
    }

    public void setSong(Song song) {
        this.song = song;
    }
}
