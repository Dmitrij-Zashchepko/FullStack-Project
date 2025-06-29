package org.crud_db.db_manager_app;

import org.crud_db.db_manager_app.managers.*;
import org.crud_db.repository.*;
import java.sql.Connection;
import java.util.Scanner;

@SuppressWarnings("java:S106")
public class DBManager {
    private final Scanner scanner;

    private final ArtistManager artistManager;
    private final AlbumManager albumManager;
    private final SongManager songManager;
    private final GenreManager genreManager;
    private final UserManager userManager;
    private final PlaylistManager playlistManager;

    public DBManager(Connection connection) {
        this.scanner = new Scanner(System.in);

        ArtistRepository artistRepo = new ArtistRepository(connection);
        this.artistManager = new ArtistManager(scanner, artistRepo);

        AlbumRepository albumRepo = new AlbumRepository(connection);
        this.albumManager = new AlbumManager(scanner, albumRepo);

        SongRepository songRepo = new SongRepository(connection);
        this.songManager = new SongManager(scanner, songRepo, albumManager);

        GenreRepository genreRepo = new GenreRepository(connection);
        this.genreManager = new GenreManager(scanner, genreRepo, songManager);

        PlaylistRepository playlistRepo = new PlaylistRepository(connection);
        PlaylistSongRepository playlistSongRepo = new PlaylistSongRepository(connection);
        this.playlistManager = new PlaylistManager(scanner, playlistRepo, playlistSongRepo);

        UserRepository userRepo = new UserRepository(connection);
        this.userManager = new UserManager(scanner, userRepo, playlistManager);
    }

    public void start() {
        while (true) {
            printMainMenu();
            int choice = readIntInput();

            switch (choice) {
                case 1 -> artistManager.showMenu();
                case 2 -> albumManager.showMenu();
                case 3 -> songManager.showMenu();
                case 4 -> genreManager.showMenu();
                case 5 -> userManager.showMenu();
                case 6 -> playlistManager.showMenu();
                case 0 -> {
                    System.out.println("Выход...");
                    return;
                }
                default -> System.out.println("Неверный ввод!");
            }
        }
    }

    private void printMainMenu() {
        System.out.println("\n=== Главное меню ===");
        System.out.println("1. Управление исполнителями");
        System.out.println("2. Управление альбомами");
        System.out.println("3. Управление песнями");
        System.out.println("4. Управление жанрами");
        System.out.println("5. Управление пользователями");
        System.out.println("6. Управление плейлистами");
        System.out.println("0. Выход");
        System.out.print("Выберите действие: ");
    }

    private int readIntInput() {
        while (true) {
            try {
                return Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException _) {
                System.out.print("Введите число: ");
            }
        }
    }
}
