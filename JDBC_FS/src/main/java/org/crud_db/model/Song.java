package org.crud_db.model;

import java.time.Duration;

public class Song {
    private int songId;
    private String title;
    private Integer albumId;
    private Duration duration;
    private String lyrics;
    private String filePath;

    // Конструкторы
    public Song() {}

    // В случае, если песня - сингл
    public Song(String title, Duration duration, String lyrics, String filePath) {
        this.title = title;
        this.albumId = null;
        this.duration = duration;
        this.lyrics = lyrics;
        this.filePath = filePath;
    }

    // Если песня относится к альбому
    public Song(String title, Integer albumId, Duration duration, String lyrics, String filePath) {
        this.title = title;
        this.albumId = albumId;
        this.duration = duration;
        this.lyrics = lyrics;
        this.filePath = filePath;
    }

    // Геттеры и сеттеры
    public int getSongId() { return songId; }
    public void setSongId(int songId) { this.songId = songId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public Integer getAlbumId() { return albumId; }
    public void setAlbumId(Integer albumId) { this.albumId = albumId; }

    public Duration getDuration() { return duration; }
    public void setDuration(Duration duration) { this.duration = duration; }

    public String getLyrics() { return lyrics; }
    public void setLyrics(String lyrics) { this.lyrics = lyrics; }

    public String getFilePath() { return filePath; }
    public void setFilePath(String filePath) { this.filePath = filePath; }
}
