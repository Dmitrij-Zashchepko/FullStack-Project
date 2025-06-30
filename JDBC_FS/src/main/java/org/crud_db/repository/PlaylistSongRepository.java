package org.crud_db.repository;

import org.crud_db.model.PlaylistSong;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PlaylistSongRepository {
    private final Connection connection;

    public PlaylistSongRepository(Connection connection) {
        this.connection = connection;
    }

    // Добавление песни в плейлист
    public void addSongToPlaylist(PlaylistSong playlistSong) throws SQLException {
        String sql = "INSERT INTO playlist_songs (playlist_id, song_id) VALUES (?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, playlistSong.getPlaylistId());
            stmt.setInt(2, playlistSong.getSongId());
            stmt.executeUpdate();
        }
    }

    // Получение всех связей плейлист-песня
    public List<PlaylistSong> getAllPlaylistSongs() throws SQLException {
        List<PlaylistSong> playlistSongs = new ArrayList<>();
        String sql = "SELECT playlist_id, song_id, created_at FROM playlist_songs";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                PlaylistSong ps = new PlaylistSong();
                ps.setPlaylistId(rs.getInt("playlist_id"));
                ps.setSongId(rs.getInt("song_id"));
                ps.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
                playlistSongs.add(ps);
            }
        }
        return playlistSongs;
    }

    // Получение песен в плейлисте
    public List<Integer> getSongsInPlaylist(int playlistId) throws SQLException {
        List<Integer> songIds = new ArrayList<>();
        String sql = "SELECT song_id FROM playlist_songs WHERE playlist_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, playlistId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    songIds.add(rs.getInt("song_id"));
                }
            }
        }
        return songIds;
    }

    // Получение плейлистов, содержащих песню
    public List<Integer> getPlaylistsContainingSong(int songId) throws SQLException {
        List<Integer> playlistIds = new ArrayList<>();
        String sql = "SELECT playlist_id FROM playlist_songs WHERE song_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, songId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    playlistIds.add(rs.getInt("playlist_id"));
                }
            }
        }
        return playlistIds;
    }

    // Удаление песни из плейлиста
    public boolean removeSongFromPlaylist(int playlistId, int songId) throws SQLException {
        String sql = "DELETE FROM playlist_songs WHERE playlist_id = ? AND song_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, playlistId);
            stmt.setInt(2, songId);
            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;
        }
    }

    // Проверка наличия песни в плейлисте
    public boolean isSongInPlaylist(int playlistId, int songId) throws SQLException {
        String sql = "SELECT 1 FROM playlist_songs WHERE playlist_id = ? AND song_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, playlistId);
            stmt.setInt(2, songId);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        }
    }
}
