package org.crud_db.repository;

import org.crud_db.model.Song;
import org.postgresql.util.PGInterval;

import java.sql.*;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

public class SongRepository {
    private final Connection connection;

    public SongRepository(Connection connection) {
        this.connection = connection;
    }



    // --- CREATE ---

    // Добавление новой песни
    public void addSong(Song song) throws SQLException {
        String sql = "INSERT INTO songs (title, album_id, duration, lyrics, file_path) " +
                     "VALUES (?, ?, ?, ?, ?)";

        try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            setSongParameters(stmt, song);
            stmt.executeUpdate();

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    song.setSongId(rs.getInt(1));
                }
            }
        }
    }



    // --- READ ---

    // Чтение одной песни (по ID)
    public Song getSongById(int songId) throws SQLException {
        String sql = "SELECT * FROM songs WHERE song_id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, songId);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() ? mapResultSetToSong(rs) : null;
            }
        }
    }

    // Получение списка всех песен
    public List<Song> getAllSongs() throws SQLException {
        String sql = "SELECT * FROM songs";
        List<Song> songs = new ArrayList<>();

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                songs.add(mapResultSetToSong(rs));
            }
        }
        return songs;
    }



    // --- UPDATE ---

    // Обновление данных песни
    public void updateSong(Song song) throws SQLException {
        String sql = "UPDATE songs SET title = ?, album_id = ?, duration = ?, " +
                     "lyrics = ?, file_path = ? WHERE song_id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            setSongParameters(stmt, song);
            stmt.setInt(6, song.getSongId());
            stmt.executeUpdate();
        }
    }



    // -- DELETE --

    // Удаление песни (по ID)
    public boolean deleteSong(int songId) throws SQLException {
        String sql = "DELETE FROM songs WHERE song_id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, songId);
            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;
        }
    }



    // -- Вспомогательные методы --

    // Метод для назначения параметров песни
    private void setSongParameters(PreparedStatement stmt, Song song) throws SQLException {
        stmt.setString(1, song.getTitle());
        stmt.setObject(2, song.getAlbumId(), Types.INTEGER);  // Может быть NULL
        stmt.setObject(3, durationToInterval(song.getDuration()));
        stmt.setString(4, song.getLyrics());
        stmt.setString(5, song.getFilePath());
    }

    // Метод для получения параметров песни
    private Song mapResultSetToSong(ResultSet rs) throws SQLException {
        Song song = new Song();
        song.setSongId(rs.getInt("song_id"));
        song.setTitle(rs.getString("title"));
        song.setAlbumId(rs.getObject("album_id", Integer.class));  // NULL-safe
        song.setDuration(intervalToDuration((PGInterval) rs.getObject("duration")));
        song.setLyrics(rs.getString("lyrics"));
        song.setFilePath(rs.getString("file_path"));
        return song;
    }

    // Конвертация Duration <-> INTERVAL
    private PGInterval durationToInterval(Duration duration) {
        return new PGInterval(0, 0, (int) duration.toDays(),
                (int) duration.toHours() % 24, (int) duration.toMinutes() % 60,
                duration.getSeconds() % 60 + duration.getNano() / 1_000_000_000.0);
    }

    // Конвертация INTERVAL <-> Duration
    private Duration intervalToDuration(PGInterval interval) {
        return Duration.ofHours(interval.getHours())
                .plusMinutes(interval.getMinutes())
                .plusSeconds((long) interval.getSeconds());
    }
}
