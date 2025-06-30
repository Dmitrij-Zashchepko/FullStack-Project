package org.crud_db.db_manager_app.managers;

import org.crud_db.model.Album;
import org.crud_db.repository.AlbumRepository;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.List;
import java.util.Scanner;

@SuppressWarnings("java:S106")
public class AlbumManager {
    private final Scanner scanner;
    private final AlbumRepository repo;

    private static final String ALBUM_NOT_FOUND_MSG = "Альбом не найден.";

    private static final Logger logger = Logger.getLogger(AlbumManager.class.getName());

    public AlbumManager(Scanner scanner, AlbumRepository albumRepo) {
        this.scanner = scanner;
        this.repo = albumRepo;
    }

    public void showMenu() {
        while (true) {
            printMenu();
            int choice = readIntInput();

            switch (choice) {
                case 1 -> addAlbum();
                case 2 -> showAllAlbums();
                case 3 -> getAlbumById();
                case 4 -> updateAlbum();
                case 5 -> deleteAlbum();
                case 0 -> { return; }
                default -> System.out.println("Неверный ввод!");
            }
        }
    }

    private void printMenu() {
        System.out.println("\n=== Управление альбомами ===");
        System.out.println("1. Добавить альбом");
        System.out.println("2. Показать все альбомы");
        System.out.println("3. Найти альбом по ID");
        System.out.println("4. Обновить данные альбома");
        System.out.println("5. Удалить альбом");
        System.out.println("0. Назад");
        System.out.print("Выберите действие: ");
    }


    private void addAlbum() {
        System.out.print("Введите название альбома: ");
        String title = scanner.nextLine();

        System.out.print("Введите ID исполнителя: ");
        int artistId = readIntInput();

        System.out.print("\nВведите дату релиза(ГГГГ-ММ-ДД): ");
        LocalDate releaseDate = readDateInput(scanner);

        System.out.print("URL обложки (опционально): ");
        String coverUrl = scanner.nextLine();

        Album album = new Album(title, artistId, releaseDate, coverUrl);

        try {
            repo.addAlbum(album);
            System.out.println("Альбом добавлен с ID: " + album.getAlbumId());
        } catch (SQLException e) {
            logger.log(Level.SEVERE,"Ошибка при добавлении: {0}", e.getMessage());
        }
    }

    private void showAllAlbums() {
        try {
            List<Album> albums = repo.getAllAlbums();
            if (albums.isEmpty()) {
                System.out.println("Список альбомов пуст.");
                return;
            }

            System.out.println("\nСписок альбомов:");
            for (Album album : albums) {
                System.out.printf("%d. %s (Исполнитель: %d, Дата: %s)%n",
                        album.getAlbumId(),
                        album.getTitle(),
                        album.getArtistId(),
                        album.getReleaseDate());
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Ошибка при загрузке: {0}", e.getMessage());
        }
    }

    private void getAlbumById() {
        System.out.print("\nВведите ID альбома: ");
        int id = readIntInput();

        try {
            Album album = repo.getAlbumById(id);
            if (album != null) {
                System.out.printf("%nНайден альбом: %s (Исполнитель: %d, Дата: %s)%n",
                        album.getTitle(),
                        album.getArtistId(),
                        album.getReleaseDate());
            } else {
                logger.log(Level.INFO, "{0}", ALBUM_NOT_FOUND_MSG);
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE,"Ошибка поиска: {0}", e.getMessage());
        }
    }

    private void updateAlbum() {
        System.out.print("Введите ID альбома для обновления: ");
        int id = readIntInput();

        try {
            Album album = repo.getAlbumById(id);
            if (album == null) {
                logger.log(Level.INFO, "{0}", ALBUM_NOT_FOUND_MSG);
                return;
            }

            System.out.print("Новое название (текущее: " + album.getTitle() + "): ");
            String title = scanner.nextLine();
            if (!title.isEmpty()) album.setTitle(title);

            System.out.print("Новый ID исполнителя (текущий: " + album.getArtistId() + "): ");
            int artistIdInput = readIntInput();
            if (artistIdInput != 0) {
                album.setArtistId(artistIdInput);
            }

            System.out.print("Новая дата релиза (текущая: " + album.getReleaseDate() + "): ");
            String dateInput = scanner.nextLine();
            if (!dateInput.isEmpty()) {
                album.setReleaseDate(LocalDate.parse(dateInput));
            }

            System.out.print("Новый URL обложки (текущее: " + album.getCoverImageUrl() + "): ");
            String coverUrl = scanner.nextLine();
            if (!coverUrl.isEmpty()) album.setCoverImageUrl(coverUrl);

            repo.updateAlbum(album);
            System.out.println("Данные альбома обновлены.");
        } catch (SQLException e) {
            logger.log(Level.SEVERE,"Ошибка обновления: {0}", e.getMessage());
        } catch (DateTimeParseException _) {
            System.err.println("Неверный формат даты!");
        }
    }

    private void deleteAlbum() {
        System.out.print("Введите ID альбома для удаления: ");
        int id = readIntInput();

        try {
            if (repo.deleteAlbum(id)) {
                System.out.println("Альбом удален.");
            } else {
                logger.log(Level.INFO, ALBUM_NOT_FOUND_MSG);
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE,"Ошибка удаления: {0}", e.getMessage());
        }
    }

    int addSingle(String title) {
        System.out.print("Введите название альбома для сингла (опционально; скопирует название песни, если нет ввода): ");
        String albumTitle = scanner.nextLine();
        if (albumTitle.isEmpty()) albumTitle = title;

        System.out.print("Введите ID исполнителя: ");
        int artistId = readIntInput();

        LocalDate releaseDate = readDateInput(scanner);

        System.out.print("URL обложки (опционально): ");
        String coverUrl = scanner.nextLine();

        Album album = new Album(albumTitle, artistId, releaseDate, coverUrl);

        try {
            repo.addAlbum(album);
            System.out.println("Сингл добавлен с ID: " + album.getAlbumId());
            return album.getAlbumId();
        } catch (SQLException e) {
            logger.log(Level.SEVERE,"Ошибка при добавлении: {0}", e.getMessage());
        }
        return album.getAlbumId();
    }

    void deleteSingle(int albumId) {
        try {
            if (repo.deleteAlbum(albumId)) {
                System.out.println("Альбом удален.");
            } else {
                System.out.println(ALBUM_NOT_FOUND_MSG);
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE,"Ошибка удаления: {0}", e.getMessage());
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

    private LocalDate readDateInput(Scanner scanner) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        while (true) {
            try {
                return LocalDate.parse(scanner.nextLine(), formatter);
            } catch (Exception _) {
                System.out.println("\nОшибка! Используйте формат ГГГГ-ММ-ДД.");
            }
        }
    }
}
