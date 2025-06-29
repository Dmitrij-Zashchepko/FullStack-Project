package org.crud_db.db_manager_app.managers;

import org.crud_db.model.Genre;
import org.crud_db.model.Song;
import org.crud_db.repository.GenreRepository;
import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Logger;
import java.util.logging.Level;

@SuppressWarnings("java:S106")
public class GenreManager {
    private final Scanner scanner;
    private final GenreRepository genreRepo;
    private final SongManager songManager;

    private static final String INSERT_GENRE_ID_MSG = "Введите ID жанра: ";
    private static final String GENRE_NOT_FOUND_MSG = "Жанр не найден.";

    private static final Logger logger = Logger.getLogger(GenreManager.class.getName());

    public GenreManager(Scanner scanner, GenreRepository genreRepo, SongManager songManager) {
        this.scanner = scanner;
        this.genreRepo = genreRepo;
        this.songManager = songManager;
    }



    public void showMenu() {
        while (true) {
            printMenu();
            int choice = readIntInput();

            switch (choice) {
                case 1 -> addGenre();
                case 2 -> showAllGenres();
                case 3 -> getGenreById();
                case 4 -> updateGenre();
                case 5 -> deleteGenre();
                case 6 -> manageSongGenres();
                case 0 -> { return; }
                default -> System.out.println("Неверный ввод!");
            }
        }
    }

    private void printMenu() {
        System.out.println("\n=== Управление жанрами ===");
        System.out.println("1. Добавить жанр");
        System.out.println("2. Показать все жанры");
        System.out.println("3. Найти жанр по ID");
        System.out.println("4. Обновить данные жанра");
        System.out.println("5. Удалить жанр");
        System.out.println("6. Управление жанрами песен");
        System.out.println("0. Назад");
        System.out.print("Выберите действие: ");
    }



    private void addGenre() {
        System.out.print("Введите название жанра: ");
        String name = scanner.nextLine();

        System.out.print("Введите описание: ");
        String description = scanner.nextLine();

        Genre genre = new Genre(name, description);
        try {
            genreRepo.addGenre(genre);
            System.out.println("Жанр добавлен с ID: " + genre.getGenreId());
        } catch (SQLException e) {
            logger.log(Level.SEVERE,"Ошибка при добавлении: {0}", e.getMessage());
        }
    }

    private void showAllGenres() {
        try {
            List<Genre> genres = genreRepo.getAllGenres();
            if (genres.isEmpty()) {
                System.out.println("Список жанров пуст.");
                return;
            }

            System.out.println("\nСписок жанров:");
            for (Genre genre : genres) {
                System.out.printf("%d. %s - %s%n",
                        genre.getGenreId(),
                        genre.getName(),
                        genre.getDescription());
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE,"Ошибка при загрузке: {0}", e.getMessage());
        }
    }

    private void getGenreById() {
        System.out.print(INSERT_GENRE_ID_MSG);
        int id = readIntInput();

        try {
            Genre genre = genreRepo.getGenreById(id);
            if (genre != null) {
                System.out.printf("Найден жанр: %s (%s)%n",
                        genre.getName(),
                        genre.getDescription());
            } else {
                logger.log(Level.INFO, GENRE_NOT_FOUND_MSG);
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Ошибка поиска: {0}", e.getMessage());
        }
    }

    private void updateGenre() {
        System.out.print("Введите ID жанра для обновления: ");
        int id = readIntInput();

        try {
            Genre genre = genreRepo.getGenreById(id);
            if (genre == null) {
                logger.log(Level.INFO, GENRE_NOT_FOUND_MSG);
                return;
            }

            System.out.print("Новое название (текущее: " + genre.getName() + "): ");
            String name = scanner.nextLine();
            if (!name.isEmpty()) genre.setName(name);

            System.out.print("Новое описание (текущее: " + genre.getDescription() + "): ");
            String description = scanner.nextLine();
            if (!description.isEmpty()) genre.setDescription(description);

            genreRepo.updateGenre(genre);
            System.out.println("Данные жанра обновлены.");
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Ошибка обновления: {0}", e.getMessage());
        }
    }

    private void deleteGenre() {
        System.out.print("Введите ID жанра для удаления: ");
        int id = readIntInput();

        try {
            if (genreRepo.deleteGenre(id)) {
                System.out.println("Жанр удален.");
            } else {
                logger.log(Level.INFO, GENRE_NOT_FOUND_MSG);
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Ошибка удаления: {0}", e.getMessage());
        }
    }



    private void manageSongGenres() {
        Song song = songManager.getSongById();

        if (song == null)
            return;

        try {
            while (true) {
                System.out.println("\n=== Управление жанрами песни ===");
                System.out.println("1. Добавить жанр");
                System.out.println("2. Удалить жанр");
                System.out.println("3. Показать все жанры");
                System.out.println("0. Назад");
                System.out.print("Выберите действие: ");

                int choice = readIntInput();
                switch (choice) {
                    case 1 -> addGenreToSong(song.getSongId());
                    case 2 -> removeGenreFromSong(song.getSongId());
                    case 3 -> showSongGenres(song.getSongId());
                    case 0 -> { return; }
                    default -> System.out.println("Неверный ввод!");
                }
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Ошибка БД: {0}", e.getMessage());
        }
    }

    private void addGenreToSong(int songId) throws SQLException {
        System.out.print(INSERT_GENRE_ID_MSG);
        int genreId = readIntInput();

        try {
            Genre genre = genreRepo.getGenreById(genreId);
            if (genre == null) {
                logger.log(Level.INFO, GENRE_NOT_FOUND_MSG);
                return;
            }
            genreRepo.addGenreToSong(songId, genreId);
            System.out.println("Жанр добавлен к песне.");
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Ошибка добавления: {0}", e.getMessage());
        }
    }

    private void removeGenreFromSong(int songId) throws SQLException {
        System.out.print(INSERT_GENRE_ID_MSG);
        int genreId = readIntInput();

        try {
            Genre genre = genreRepo.getGenreById(genreId);
            if (genre == null) {
                logger.log(Level.INFO, GENRE_NOT_FOUND_MSG);
                return;
            }
            genreRepo.removeGenreFromSong(songId, genreId);
            System.out.println("Жанр удален из песни.");
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Ошибка удаления: {0}", e.getMessage());
        }
    }

    private void showSongGenres(int songId) throws SQLException {
        try {
            List<Genre> genres = genreRepo.getGenresForSong(songId);

            if (genres.isEmpty()) {
                System.out.println("У песни нет жанров.");
                return;
            }

            System.out.println("\nЖанры песни:");
            for (Genre genre : genres) {
                System.out.printf("%d. %s - %s%n",
                        genre.getGenreId(),
                        genre.getName(),
                        genre.getDescription());
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE,"Ошибка при загрузке: {0}", e.getMessage());
        }
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
