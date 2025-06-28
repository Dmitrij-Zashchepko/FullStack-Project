package org.crud_db.db_manager_app.managers;

import org.crud_db.model.Song;
import org.crud_db.repository.SongRepository;

import java.sql.SQLException;
import java.time.Duration;
import java.util.List;
import java.util.Scanner;

public class SongManager {
    private final Scanner scanner;
    private final SongRepository songRepo;
    private final AlbumManager albumManager;

    private static final String songNotFoundMsg = "Песня не найдена.";

    public SongManager(Scanner scanner, SongRepository songRepo, AlbumManager albumManager) {
        this.scanner = scanner;
        this.songRepo = songRepo;
        this.albumManager = albumManager;
    }



    public void showMenu() {
        while (true) {
            printMenu();
            int choice = readIntInput();

            switch (choice) {
                case 1 -> addSong();
                case 2 -> showAllSongs();
                case 3 -> getSongById();
                case 4 -> updateSong();
                case 5 -> deleteSong();
                case 0 -> { return; }
                default -> System.out.println("Неверный ввод!");
            }
        }
    }

    private void printMenu() {
        System.out.println("\n=== Управление песнями ===");
        System.out.println("1. Добавить песню");
        System.out.println("2. Показать все песни");
        System.out.println("3. Найти песню по ID");
        System.out.println("4. Обновить данные песни");
        System.out.println("5. Удалить песню");
        System.out.println("0. Назад");
        System.out.print("Выберите действие: ");
    }



    private void addSong() {
        System.out.print("Введите название песни: ");
        String title = scanner.nextLine();

        System.out.print("Введите ID альбома (0 если нет альбома): ");
        int albumId = readIntInput();

        Duration duration = readDurationInput();

        System.out.print("Текст песни (опционально): ");
        String lyrics = scanner.nextLine();

        System.out.print("Путь к файлу: ");
        String filePath = scanner.nextLine();

        if (albumId <= 0) {
            albumId = albumManager.addSingle(title);
        }
        Song song = new Song(title, albumId, duration, lyrics, filePath);

        try {
            songRepo.addSong(song);
            System.out.println("Песня добавлена с ID: " + song.getSongId());
        } catch (SQLException e) {
            System.err.println("Ошибка при добавлении: " + e.getMessage());
        }
    }

    private void showAllSongs() {
        try {
            List<Song> songs = songRepo.getAllSongs();
            if (songs.isEmpty()) {
                System.out.println("Список песен пуст.");
                return;
            }

            System.out.println("\nСписок песен:");
            for (Song song : songs) {
                System.out.printf("%d. %s (Альбом: %s, Длительность: %s)%n",
                        song.getSongId(),
                        song.getTitle(),
                        song.getAlbumId() != null ? song.getAlbumId() : "нет",
                        formatDuration(song.getDuration()));
            }
        } catch (SQLException e) {
            System.err.println("Ошибка при загрузке: " + e.getMessage());
        }
    }

    private void getSongById() {
        System.out.print("Введите ID песни: ");
        int id = readIntInput();

        try {
            Song song = songRepo.getSongById(id);
            if (song != null) {
                System.out.printf("Найдена песня: %s (Альбом: %s, Длительность: %s)%n",
                        song.getTitle(),
                        song.getAlbumId() != null ? song.getAlbumId() : "нет",
                        formatDuration(song.getDuration()));
            } else {
                System.out.println(songNotFoundMsg);
            }
        } catch (SQLException e) {
            System.err.println("Ошибка поиска: " + e.getMessage());
        }
    }

    private void updateSong() {
        System.out.print("Введите ID песни для обновления: ");
        int id = readIntInput();

        try {
            Song song = songRepo.getSongById(id);
            if (song == null) {
                System.out.println(songNotFoundMsg);
                return;
            }

            System.out.print("Новое название (текущее: " + song.getTitle() + "): ");
            String title = scanner.nextLine();
            if (!title.isEmpty()) song.setTitle(title);

            System.out.print("Новая длительность (текущая: " +
                    formatDuration(song.getDuration()) + "): ");
            String durationInput = scanner.nextLine();
            if (!durationInput.isEmpty()) {
                song.setDuration(parseDuration(durationInput));
            }

            System.out.print("Новый ID альбома (текущий: " +
                    song.getAlbumId() + "): ");
            int albumIdInput = readIntInput();

            if (albumIdInput > 0) {
                song.setAlbumId(albumIdInput);
                songRepo.updateSong(song);
            } else if (albumIdInput == -1) {
                albumManager.deleteSingle(song.getAlbumId());
                albumIdInput = albumManager.addSingle(song.getTitle());
                song.setAlbumId(albumIdInput);
                songRepo.addSong(song);
            } else {
                songRepo.updateSong(song);
            }

            System.out.println("Данные песни обновлены.");
        } catch (SQLException e) {
            System.err.println("Ошибка обновления: " + e.getMessage());
        }
    }

    private void deleteSong() {
        System.out.print("Введите ID песни для удаления: ");
        int id = readIntInput();

        try {
            if (songRepo.deleteSong(id)) {
                System.out.println("Песня удалена.");
            } else {
                System.out.println(songNotFoundMsg);
            }
        } catch (SQLException e) {
            System.err.println("Ошибка удаления: " + e.getMessage());
        }
    }



    private Duration readDurationInput() {
        while (true) {
            System.out.print("Введите длительность (чч:мм:сс): ");
            try {
                String input = scanner.nextLine();
                return parseDuration(input);
            } catch (IllegalArgumentException _) {
                System.out.println("Неверный формат! Используйте чч:мм:сс");
            }
        }
    }

    private Duration parseDuration(String input) {
        String[] parts = input.split(":");
        return Duration.ofHours(Long.parseLong(parts[0]))
                .plusMinutes(Long.parseLong(parts[1]))
                .plusSeconds(Long.parseLong(parts[2]));
    }

    private String formatDuration(Duration duration) {
        return String.format("%02d:%02d:%02d",
                duration.toHoursPart(),
                duration.toMinutesPart(),
                duration.toSecondsPart());
    }

    private int readIntInput() {
        while (true) {
            try {
                return Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException _) {
                System.out.print("Ошибка! Введите целое число: ");
            }
        }
    }
}
