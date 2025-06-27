package org.crud_db.repository;

import org.crud_db.model.Artist;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;


// Создание класса для реализации CRUD-операций и других для artists
public class ArtistRepository {
    private final Connection connection;

    // Конструктор
    public ArtistRepository(Connection connection) {
        this.connection = connection;
    }



    // -- CREATE --

    // Добавление нового исполнителя
    public void addArtist(Artist artist) throws SQLException {
        String sql = "INSERT INTO artists (name, country, formed_date, biography) VALUES (?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, artist.getName());
            stmt.setString(2, artist.getCountry());
            stmt.setDate(3, Date.valueOf(artist.getFormedDate()));
            stmt.setString(4, artist.getBiography());
            stmt.executeUpdate();

            // Получаем сгенерированный ID
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    artist.setArtistId(rs.getInt(1));
                }
            }
        }
    }



    // -- READ --

    // Получение списка всех исполнителей
    public List<Artist> getAllArtists() throws SQLException {
        List<Artist> artists = new ArrayList<>();
        String sql = "SELECT * FROM artists";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Artist artist = new Artist();
                artist.setArtistId(rs.getInt("artist_id"));
                artist.setName(rs.getString("name"));
                artist.setCountry(rs.getString("country"));
                artist.setFormedDate(rs.getDate("formed_date").toLocalDate());
                artist.setBiography(rs.getString("biography"));
                artists.add(artist);
            }
        }
        return artists;
    }

    // Чтение определённого исполнителя по ID
    public Artist getArtistById(int artist_id) throws SQLException {
        Artist artist = null; // Если исполнитель не найден, то вернётся NULL
        String sql = "SELECT * FROM artists WHERE artist_id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, artist_id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    artist = new Artist();
                    artist.setArtistId(rs.getInt("artist_id"));
                    artist.setName(rs.getString("name"));
                    artist.setCountry(rs.getString("country"));
                    artist.setFormedDate(rs.getDate("formed_date").toLocalDate());
                    artist.setBiography(rs.getString("biography"));
                }
            }
        }
        return artist;
    }



    // -- UPDATE --

    // Обновление данных исполнителя
    public void updateArtist(Artist artist) throws SQLException {
        String sql = "UPDATE artists SET name = ?, country = ?, formed_date = ?, biography = ? WHERE artist_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, artist.getName());
            stmt.setString(2, artist.getCountry());
            stmt.setDate(3, Date.valueOf(artist.getFormedDate()));
            stmt.setString(4, artist.getBiography());
            stmt.setInt(5, artist.getArtistId());
            stmt.executeUpdate();
        }
    }



    // -- DELETE --

    // Удаление исполнителя по ID
    public boolean deleteArtist(int artistId) throws SQLException {
        String sql = "DELETE FROM artists WHERE artist_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, artistId);
            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;
        }
    }
}
