package org.crud_db.repository;

import org.crud_db.model.Genre;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class GenreRepository {
    private final Connection connection;

    public GenreRepository(Connection connection) {
        this.connection = connection;
    }



    // CREATE
    public void addGenre(Genre genre) throws SQLException {
        String sql = "INSERT INTO genres (name, description) VALUES (?, ?)";

        try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, genre.getName());
            stmt.setString(2, genre.getDescription());
            stmt.executeUpdate();

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    genre.setGenreId(rs.getInt(1));
                }
            }
        }
    }

    // Добавить жанр к песне
    public void addGenreToSong(int songId, int genreId) throws SQLException {
        String sql = "INSERT INTO song_genres (song_id, genre_id) VALUES (?, ?)";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, songId);
            stmt.setInt(2, genreId);
            stmt.executeUpdate();
        }
    }

    // READ
    public Genre getGenreById(int genreId) throws SQLException {
        String sql = "SELECT * FROM genres WHERE genre_id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, genreId);

            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() ? mapResultSetToGenre(rs) : null;
            }
        }
    }

    public List<Genre> getAllGenres() throws SQLException {
        String sql = "SELECT * FROM genres";
        List<Genre> genres = new ArrayList<>();

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                genres.add(mapResultSetToGenre(rs));
            }
        }
        return genres;
    }

    // Получить все жанры песни
    public List<Genre> getGenresForSong(int songId) throws SQLException {
        String sql = """
        SELECT g.* FROM genres g
        JOIN song_genres sg ON g.genre_id = sg.genre_id
        WHERE sg.song_id = ?
        """;

        List<Genre> genres = new ArrayList<>();

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, songId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Genre genre = new Genre();
                    genre.setGenreId(rs.getInt("genre_id"));
                    genre.setName(rs.getString("name"));
                    genre.setDescription(rs.getString("description"));
                    genres.add(genre);
                }
            }
        }
        return genres;
    }

    // UPDATE
    public void updateGenre(Genre genre) throws SQLException {
        String sql = "UPDATE genres SET name = ?, description = ? WHERE genre_id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, genre.getName());
            stmt.setString(2, genre.getDescription());
            stmt.setInt(3, genre.getGenreId());
            stmt.executeUpdate();
        }
    }



    // DELETE
    public boolean deleteGenre(int genreId) throws SQLException {
        String sql = "DELETE FROM genres WHERE genre_id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, genreId);
            return stmt.executeUpdate() > 0;
        }
    }

    // Удалить жанр у песни
    public void removeGenreFromSong(int songId, int genreId) throws SQLException {
        String sql = "DELETE FROM song_genres WHERE song_id = ? AND genre_id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, songId);
            stmt.setInt(2, genreId);
            stmt.executeUpdate();
        }
    }



    // Вспомогательный метод для маппинга ResultSet -> Genre
    private Genre mapResultSetToGenre(ResultSet rs) throws SQLException {
        Genre genre = new Genre();
        genre.setGenreId(rs.getInt("genre_id"));
        genre.setName(rs.getString("name"));
        genre.setDescription(rs.getString("description"));
        return genre;
    }
}
