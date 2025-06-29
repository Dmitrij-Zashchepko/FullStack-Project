package org.crud_db.db_manager_app.managers;

import org.crud_db.model.Playlist;
import org.crud_db.model.PlaylistSong;
import org.crud_db.model.Song;
import org.crud_db.repository.PlaylistRepository;
import org.crud_db.repository.PlaylistSongRepository;

import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Logger;
import java.util.logging.Level;

@SuppressWarnings("java:S106")
public class PlaylistManager {
    private final Scanner scanner;
    private final PlaylistRepository repo;
    private final PlaylistSongRepository playlistSongRepo;
    private final SongManager songManager;

    private static final String PRIVATE_PL_MSG = "Приватный";
    private static final String PUBLIC_PL_MSG = "Публичный";
    private static final String INSERT_PL_ID_MSG = "\nВведите ID плейлиста: ";
    private static final String EXC_ERROR_MSG = "Ошибка: {0}";

    private static final Logger logger = Logger.getLogger(PlaylistManager.class.getName());

    public PlaylistManager(Scanner scanner, PlaylistRepository repo, PlaylistSongRepository playlistSongRepo,
                           SongManager songManager) {
        this.scanner = scanner;
        this.repo = repo;
        this.playlistSongRepo = playlistSongRepo;
        this.songManager = songManager;
    }



    public void showMenu() {
        while (true) {
            printMenu();
            int choice = readIntInput();
            switch (choice) {
                case 1 -> addPlaylist();
                case 2 -> showAllPlaylists();
                case 3 -> getPlaylistById();
                case 4 -> updatePlaylist();
                case 5 -> deletePlaylist();
                case 6 -> managePlaylistSongs();
                case 0 -> { return; }
                default -> System.out.println("Неверный ввод!");
            }
        }
    }

    private void printMenu() {
        System.out.println("\n=== Управление плейлистами ===");
        System.out.println("1. Добавить плейлист");
        System.out.println("2. Показать все плейлисты");
        System.out.println("3. Найти плейлист по ID");
        System.out.println("4. Обновить плейлист");
        System.out.println("5. Удалить плейлист");
        System.out.println("6. Управление песнями в плейлистах");
        System.out.println("0. Назад");
        System.out.print("Выберите действие: ");
    }



    private void addPlaylist() {
        System.out.print("\nВведите ID пользователя: ");
        int userId = readIntInput();

        System.out.print("Введите название плейлиста: ");
        String title = scanner.nextLine();

        System.out.print("Введите описание: ");
        String description = scanner.nextLine();

        System.out.print("Плейлист публичный? (y/n): ");
        boolean isPublic = scanner.nextLine().equalsIgnoreCase("y");

        Playlist playlist = new Playlist(userId, title);
        playlist.setDescription(description);
        playlist.setPublic(isPublic);
        try {
            repo.addPlaylist(playlist);
            System.out.println("\nПлейлист добавлен с ID: " + playlist.getPlaylistId());
        } catch (SQLException e) {
            logger.log(Level.SEVERE, EXC_ERROR_MSG, e.getMessage());
        }
    }

    private void deletePlaylist() {
        System.out.print(INSERT_PL_ID_MSG);
        int id = readIntInput();

        try {
            if(repo.deletePlaylist(id)) {
                System.out.println("\nПлейлист удален.");
            } else {
                logger.log(Level.INFO, "Плейлиста с таким ID не найдено.");
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, EXC_ERROR_MSG, e.getMessage());
        }
    }

    private void showAllPlaylists() {
        try {
            List<Playlist> playlists = repo.getAllPlaylists();
            if (playlists.isEmpty()) {
                System.out.println("\nСписок плейлистов пуст.");
                return;
            }

            System.out.println("\nСписок плейлистов:");
            for (Playlist playlist : playlists) {
                System.out.printf("%d. [%s] %s (Пользователь: %d, %s)%n",
                        playlist.getPlaylistId(),
                        playlist.isPublic() ? PUBLIC_PL_MSG : PRIVATE_PL_MSG,
                        playlist.getTitle(),
                        playlist.getUserId(),
                        playlist.getDescription());
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, EXC_ERROR_MSG, e.getMessage());
        }
    }

    private Playlist getPlaylistById() {
        System.out.print(INSERT_PL_ID_MSG);
        int id = readIntInput();

        try {
            Playlist playlist = repo.getPlaylistById(id);
            if (playlist != null) {
                System.out.printf("%nНайден плейлист: %s [%s]%nОписание: %s%nСоздан: %s%n",
                        playlist.getTitle(),
                        playlist.isPublic() ? PUBLIC_PL_MSG : PRIVATE_PL_MSG,
                        playlist.getDescription(),
                        playlist.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
                return playlist;
            } else {
                logger.log(Level.INFO, "Плейлист не найден.");
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, EXC_ERROR_MSG, e.getMessage());
        }
        return null;
    }

    private void updatePlaylist() {
        System.out.print(INSERT_PL_ID_MSG);
        int id = readIntInput();

        try {
            Playlist playlist = repo.getPlaylistById(id);
            if (playlist == null) {
                logger.log(Level.INFO, "Плейлист не найден.");
                return;
            }

            System.out.print("\nНовое название (текущее: " + playlist.getTitle() + "): ");
            String title = scanner.nextLine();
            if (!title.isEmpty()) playlist.setTitle(title);

            System.out.print("\nНовое описание (текущее: " + playlist.getDescription() + "): ");
            String description = scanner.nextLine();
            if (!description.isEmpty()) playlist.setDescription(description);

            System.out.print("\nИзменить статус? (текущее: " +
                    (playlist.isPublic() ? PUBLIC_PL_MSG : PRIVATE_PL_MSG) + ") (y/n): ");
            String isPublic = scanner.nextLine();
            if (!isPublic.isEmpty()) {
                playlist.setPublic(isPublic.equalsIgnoreCase("y"));
            }

            repo.updatePlaylist(playlist);
            System.out.println("\nДанные обновлены.");
        } catch (SQLException e) {
            logger.log(Level.SEVERE, EXC_ERROR_MSG, e.getMessage());
        }
    }

    void showUserPlaylists(int userId) {
        try {
            List<Playlist> playlists = repo.getPlaylistsByUser(userId);
            if (playlists.isEmpty()) {
                System.out.println("\nУ пользователя нет плейлистов.");
                return;
            }

            System.out.println("\nПлейлисты пользователя " + userId + ":");
            for (Playlist playlist : playlists) {
                System.out.printf("%d. [%s] %s - %s%n",
                        playlist.getPlaylistId(),
                        playlist.isPublic() ? PUBLIC_PL_MSG : PRIVATE_PL_MSG,
                        playlist.getTitle(),
                        playlist.getDescription());
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, EXC_ERROR_MSG, e.getMessage());
        }
    }



    public void managePlaylistSongs() {
        while (true) {
            printInMenu();
            int choice = readIntInput();
            switch (choice) {
                case 1 -> addSongToPlaylist();
                case 2 -> removeSongFromPlaylist();
                case 3 -> showSongsInPlaylist();
                case 4 -> showPlaylistsForSong();
                case 0 -> {
                    return;
                }
                default -> System.out.println("Неверный ввод!");
            }
        }
    }

    private void printInMenu() {
        System.out.println("\n=== Управление песнями в плейлистах ===");
        System.out.println("1. Добавить песню в плейлист");
        System.out.println("2. Удалить песню из плейлиста");
        System.out.println("3. Показать песни в плейлисте");
        System.out.println("4. Показать плейлисты с песней");
        System.out.println("0. Назад");
        System.out.print("Выберите действие: ");
    }

    private void addSongToPlaylist() {
        Playlist playlist = getPlaylistById();
        Song song = songManager.getSongById();

        if (playlist == null || song == null)
            return;

        try {
            if (playlistSongRepo.isSongInPlaylist(playlist.getPlaylistId(), song.getSongId())) {
                System.out.println("Эта песня уже есть в плейлисте.");
                return;
            }

            playlistSongRepo.addSongToPlaylist(new PlaylistSong(playlist.getPlaylistId(), song.getSongId()));
            System.out.println("Песня добавлена в плейлист.");
        } catch (SQLException e) {
            logger.log(Level.SEVERE, EXC_ERROR_MSG, e.getMessage());
        }
    }

    private void removeSongFromPlaylist() {
        Playlist playlist = getPlaylistById();
        Song song = songManager.getSongById();

        if (playlist == null || song == null)
            return;

        try {
            if (playlistSongRepo.removeSongFromPlaylist(playlist.getPlaylistId(), song.getSongId())) {
                System.out.println("Песня удалена из плейлиста.");
            } else {
                logger.log(Level.INFO, "Песня не найдена в указанном плейлисте.");
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, EXC_ERROR_MSG, e.getMessage());
        }
    }

    private void showSongsInPlaylist() {
        Playlist playlist = getPlaylistById();

        if (playlist == null)
            return;

        try {
            List<Integer> songIds = playlistSongRepo.getSongsInPlaylist(playlist.getPlaylistId());
            if (songIds.isEmpty()) {
                System.out.println("В плейлисте нет песен.");
                return;
            }

            System.out.println("Песни в плейлисте:");
            for (int songId : songIds) {
                System.out.println("- ID песни: " + songId);
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, EXC_ERROR_MSG, e.getMessage());
        }
    }

    private void showPlaylistsForSong() {
        Song song = songManager.getSongById();

        if (song == null)
            return;

        try {
            List<Integer> playlistIds = playlistSongRepo.getPlaylistsContainingSong(song.getSongId());
            if (playlistIds.isEmpty()) {
                System.out.println("Эта песня не содержится ни в одном плейлисте.");
                return;
            }

            System.out.println("Плейлисты, содержащие эту песню:");
            for (int playlistId : playlistIds) {
                System.out.println("- ID плейлиста: " + playlistId);
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, EXC_ERROR_MSG, e.getMessage());
        }
    }


    private int readIntInput() {
        while (true) {
            try {
                return Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException _) {
                System.out.print("\nВведите число: ");
            }
        }
    }
}
