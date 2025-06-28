package org.crud_db.model;

import java.time.LocalDate;

public class Album {
    private int albumId;
    private String title;
    private int artistId;
    private LocalDate releaseDate;
    private String coverImageUrl;

    // Конструкторы
    public Album() {}

    public Album(String title, int artistId, LocalDate releaseDate, String coverImageUrl) {
        this.title = title;
        this.artistId = artistId;
        this.releaseDate = releaseDate;
        this.coverImageUrl = coverImageUrl;
    }

    // Геттеры и сеттеры
    public int getAlbumId() { return albumId; }
    public void setAlbumId(int albumId) { this.albumId = albumId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public int getArtistId() { return artistId; }
    public void setArtistId(int artistId) { this.artistId = artistId; }

    public LocalDate getReleaseDate() { return releaseDate; }
    public void setReleaseDate(LocalDate releaseDate) { this.releaseDate = releaseDate; }

    public String getCoverImageUrl() { return coverImageUrl; }
    public void setCoverImageUrl(String coverImageUrl) { this.coverImageUrl = coverImageUrl; }
}
