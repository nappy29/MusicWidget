package com.example.musicwidget;

import java.io.Serializable;

public class Song implements Serializable {
    private final long id;
    private final String title;
    private final String artist;
    private final String duration;
    private final byte[] bitmap;


    public Song(long id, String title, String artist, String duration, byte[] audio_btm) {
        this.id = id;
        this.title = title;
        this.artist = artist;
        this.duration = duration;
        this.bitmap = audio_btm;
    }

    public String getTitle() {
        return title;
    }

    public String getArtist() {
        return artist;
    }

    public String getDurationStr() {
        return duration;
    }
    public byte[] getBitmap() {
        return bitmap;
    }
}