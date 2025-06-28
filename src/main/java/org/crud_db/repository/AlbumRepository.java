package org.crud_db.repository;

import org.crud_db.model.Album;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

// Создание класса для реализации CRUD-операций и других для albums
public class AlbumRepository {
    private final Connection connection;

    // Конструктор
    public AlbumRepository(Connection connection) {
        this.connection = connection;
    }



    // -- CREATE --

    // Добавить новый альбом (альбом обязательно привязывается к исполнителю)
    public void addAlbum(Album album) throws SQLException {
        String sql = "INSERT INTO albums (title, artist_id, release_date, cover_image_url) " +
                     "VALUES (?, ?, ?, ?)";

        try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, album.getTitle());
            stmt.setInt(2, album.getArtistId());
            stmt.setDate(3, Date.valueOf(album.getReleaseDate()));
            stmt.setString(4, album.getCoverImageUrl());

            stmt.executeUpdate();
            // Получаем сгенерированный ID
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    album.setAlbumId(rs.getInt(1));
                }
            }
        }
    }



    // -- READ --

    // Получение списка всех альбомов, одного определённого (по ID)
    public Album getAlbumById(int albumId) throws SQLException {
        String sql = "SELECT * FROM albums WHERE album_id = ?";
        Album album = null;

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, albumId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    album = new Album();
                    album.setAlbumId(rs.getInt("album_id"));
                    album.setTitle(rs.getString("title"));
                    album.setArtistId(rs.getInt("artist_id"));
                    album.setReleaseDate(rs.getDate("release_date").toLocalDate());
                    album.setCoverImageUrl(rs.getString("cover_image_url"));
                }
            }
        }
        return album;
    }

    // Получение одного альбома по ID
    public List<Album> getAllAlbums() throws SQLException {
        String sql = "SELECT * FROM albums";
        List<Album> albums = new ArrayList<>();

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Album album = new Album();
                album.setAlbumId(rs.getInt("album_id"));
                album.setTitle(rs.getString("title"));
                album.setArtistId(rs.getInt("artist_id"));
                album.setReleaseDate(rs.getDate("release_date").toLocalDate());
                album.setCoverImageUrl(rs.getString("cover_image_url"));
                albums.add(album);
            }
        }
        return albums;
    }

    // Получение общего кол-ва треков (для отдельного поля нет необходимости)
    public int getTotalTracks(int albumId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM songs WHERE album_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, albumId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);  // Возвращает количество треков
                }
            }
        }
        return 0;
    }



    // -- UPDATE --

    // Обновление информации об альбоме
    public void updateAlbum(Album album) throws SQLException {
        String sql = "UPDATE albums SET title = ?, artist_id = ?, release_date = ?, " +
                     "cover_image_url = ? WHERE album_id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, album.getTitle());
            stmt.setInt(2, album.getArtistId());
            stmt.setDate(3, Date.valueOf(album.getReleaseDate()));
            stmt.setString(4, album.getCoverImageUrl());
            stmt.setInt(5, album.getAlbumId());
            stmt.executeUpdate();
        }
    }



    // -- DELETE --

    // Удаление альбома по ID
    public boolean deleteAlbum(int albumId) throws SQLException {
        String sql = "DELETE FROM albums WHERE album_id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, albumId);
            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;
        }
    }
}
