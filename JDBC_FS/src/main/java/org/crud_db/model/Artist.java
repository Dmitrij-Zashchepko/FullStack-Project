package org.crud_db.model;

import java.time.LocalDate;

public class Artist {
    private int artistId;
    private String name;
    private String country;
    private LocalDate formedDate;
    private String biography;

    // Конструкторы
    public Artist() {}

    public Artist(String name, String country, LocalDate formedDate, String biography) {
        this.name = name;
        this.country = country;
        this.formedDate = formedDate;
        this.biography = biography;
    }

    // Геттеры и сеттеры
    public int getArtistId() { return artistId; }
    public void setArtistId(int artistId) { this.artistId = artistId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getCountry() { return country; }
    public void setCountry(String country) { this.country = country; }

    public LocalDate getFormedDate() { return formedDate; }
    public void setFormedDate(LocalDate formedDate) { this.formedDate = formedDate; }

    public String getBiography() { return biography; }
    public void setBiography(String biography) { this.biography = biography; }
}
