package org.crud_db.model;

import java.time.LocalDateTime;

public class Playlist {
    private int playlistId;
    private int userId;  // Ссылка на User
    private String title;
    private String description;
    private LocalDateTime createdAt;
    private boolean isPublic;

    // Конструкторы
    public Playlist() {}

    public Playlist(int userId, String title) {
        this.userId = userId;
        this.title = title;
        // description может иметь значение NULL (по умолчанию)
        // isPublic по умолчанию FALSE
        // description и isPublic могут быть изменены через сеттеры
    }

    // Геттеры и сеттеры
    public int getPlaylistId() { return playlistId; }
    public void setPlaylistId(int playlistId) { this.playlistId = playlistId; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public boolean isPublic() { return isPublic; }
    public void setPublic(boolean isPublic) { this.isPublic = isPublic; }
}