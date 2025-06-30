package org.crud_db.model;

public class Genre {
    private int genreId;
    private String name;
    private String description;

    // Конструкторы
    public Genre() {}

    public Genre(String name, String description) {
        this.name = name;
        this.description = description;
    }

    // Геттеры и сеттеры
    public int getGenreId() { return genreId; }
    public void setGenreId(int genreId) { this.genreId = genreId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}
