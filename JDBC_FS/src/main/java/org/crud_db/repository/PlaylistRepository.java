package org.crud_db.repository;

import org.crud_db.model.Playlist;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PlaylistRepository {
    private final Connection connection;

    public PlaylistRepository(Connection connection) {
        this.connection = connection;
    }



    // CREATE
    public void addPlaylist(Playlist playlist) throws SQLException {
        String sql = "INSERT INTO playlists (user_id, title, description, is_public) VALUES (?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, playlist.getUserId());
            stmt.setString(2, playlist.getTitle());
            stmt.setString(3, playlist.getDescription());
            stmt.setBoolean(4, playlist.isPublic());
            stmt.executeUpdate();

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    playlist.setPlaylistId(rs.getInt(1));
                }
            }
        }
    }



    // READ
    public List<Playlist> getAllPlaylists() throws SQLException {
        List<Playlist> playlists = new ArrayList<>();
        String sql = "SELECT * FROM playlists";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Playlist playlist = new Playlist();
                playlist.setPlaylistId(rs.getInt("playlist_id"));
                playlist.setUserId(rs.getInt("user_id"));
                playlist.setTitle(rs.getString("title"));
                playlist.setDescription(rs.getString("description"));
                playlist.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
                playlist.setPublic(rs.getBoolean("is_public"));
                playlists.add(playlist);
            }
        }
        return playlists;
    }

    public Playlist getPlaylistById(int playlistId) throws SQLException {
        Playlist playlist = null;
        String sql = "SELECT * FROM playlists WHERE playlist_id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, playlistId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    playlist = new Playlist();
                    playlist.setPlaylistId(rs.getInt("playlist_id"));
                    playlist.setUserId(rs.getInt("user_id"));
                    playlist.setTitle(rs.getString("title"));
                    playlist.setDescription(rs.getString("description"));
                    playlist.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
                    playlist.setPublic(rs.getBoolean("is_public"));
                }
            }
        }
        return playlist;
    }



    // UPDATE
    public void updatePlaylist(Playlist playlist) throws SQLException {
        String sql = "UPDATE playlists SET title = ?, description = ?, is_public = ? WHERE playlist_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, playlist.getTitle());
            stmt.setString(2, playlist.getDescription());
            stmt.setBoolean(3, playlist.isPublic());
            stmt.setInt(4, playlist.getPlaylistId());
            stmt.executeUpdate();
        }
    }



    // DELETE
    public boolean deletePlaylist(int playlistId) throws SQLException {
        String sql = "DELETE FROM playlists WHERE playlist_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, playlistId);
            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;
        }
    }



    // Дополнительный метод для получения плейлистов пользователя
    public List<Playlist> getPlaylistsByUser(int userId) throws SQLException {
        List<Playlist> playlists = new ArrayList<>();
        String sql = "SELECT * FROM playlists WHERE user_id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, userId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Playlist playlist = new Playlist();
                    playlist.setPlaylistId(rs.getInt("playlist_id"));
                    playlist.setUserId(rs.getInt("user_id"));
                    playlist.setTitle(rs.getString("title"));
                    playlist.setDescription(rs.getString("description"));
                    playlist.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
                    playlist.setPublic(rs.getBoolean("is_public"));
                    playlists.add(playlist);
                }
            }
        }
        return playlists;
    }
}