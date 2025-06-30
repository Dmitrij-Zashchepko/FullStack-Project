package org.crud_db.model;

import java.time.LocalDateTime;

public class PlaylistSong {
    private int playlistId;
    private int songId;
    private LocalDateTime createdAt;

    // Конструкторы
    public PlaylistSong() {}

    public PlaylistSong(int playlistId, int songId) {
        this.playlistId = playlistId;
        this.songId = songId;
    }

    // Геттеры и сеттеры
    public int getPlaylistId() { return playlistId; }
    public void setPlaylistId(int playlistId) { this.playlistId = playlistId; }

    public int getSongId() { return songId; }
    public void setSongId(int songId) { this.songId = songId; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime addedAt) { this.createdAt = addedAt; }
}
