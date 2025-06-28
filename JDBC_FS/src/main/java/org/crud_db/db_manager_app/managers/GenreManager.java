package org.crud_db.db_manager_app.managers;

import org.crud_db.model.Genre;
import org.crud_db.repository.GenreRepository;
import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;

public class GenreManager {
    private final Scanner scanner;
    private final GenreRepository genreRepo;
    private static final String insertGenreIDMsg = "Введите ID жанра: ";
    private static final String genreNotFoundMsg = "Жанр не найден.";

    public GenreManager(Scanner scanner, GenreRepository genreRepo) {
        this.scanner = scanner;
        this.genreRepo = genreRepo;
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
            System.err.println("Ошибка при добавлении: " + e.getMessage());
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
            System.err.println("Ошибка при загрузке: " + e.getMessage());
        }
    }

    private void getGenreById() {
        System.out.print(insertGenreIDMsg);
        int id = readIntInput();

        try {
            Genre genre = genreRepo.getGenreById(id);
            if (genre != null) {
                System.out.printf("Найден жанр: %s (%s)%n",
                        genre.getName(),
                        genre.getDescription());
            } else {
                System.out.println(genreNotFoundMsg);
            }
        } catch (SQLException e) {
            System.err.println("Ошибка поиска: " + e.getMessage());
        }
    }

    private void updateGenre() {
        System.out.print("Введите ID жанра для обновления: ");
        int id = readIntInput();

        try {
            Genre genre = genreRepo.getGenreById(id);
            if (genre == null) {
                System.out.println(genreNotFoundMsg);
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
            System.err.println("Ошибка обновления: " + e.getMessage());
        }
    }

    private void deleteGenre() {
        System.out.print("Введите ID жанра для удаления: ");
        int id = readIntInput();

        try {
            if (genreRepo.deleteGenre(id)) {
                System.out.println("Жанр удален.");
            } else {
                System.out.println(genreNotFoundMsg);
            }
        } catch (SQLException e) {
            System.err.println("Ошибка удаления: " + e.getMessage());
        }
    }



    private void manageSongGenres() {
        System.out.print("Введите ID песни: ");
        int songId = readIntInput();

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
                    case 1 -> addGenreToSong(songId);
                    case 2 -> removeGenreFromSong(songId);
                    case 3 -> showSongGenres(songId);
                    case 0 -> { return; }
                    default -> System.out.println("Неверный ввод!");
                }
            }
        } catch (SQLException e) {
            System.err.println("Ошибка БД: " + e.getMessage());
        }
    }

    private void addGenreToSong(int songId) throws SQLException {
        System.out.print(insertGenreIDMsg);
        int genreId = readIntInput();

        genreRepo.addGenreToSong(songId, genreId);
        System.out.println("Жанр добавлен к песне.");
    }

    private void removeGenreFromSong(int songId) throws SQLException {
        System.out.print(insertGenreIDMsg);
        int genreId = readIntInput();

        genreRepo.removeGenreFromSong(songId, genreId);
        System.out.println("Жанр удален из песни.");
    }

    private void showSongGenres(int songId) throws SQLException {
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
