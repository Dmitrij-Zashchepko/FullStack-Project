package org.crud_db.db_manager_app.managers;

import org.crud_db.model.Artist;
import org.crud_db.repository.ArtistRepository;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Logger;
import java.util.logging.Level;

@SuppressWarnings("java:S106")
public class ArtistManager {
    private final Scanner scanner;
    private final ArtistRepository repo;

    private static final Logger logger = Logger.getLogger(ArtistManager.class.getName());

    public ArtistManager(Scanner scanner, ArtistRepository repo) {
        this.scanner = scanner;
        this.repo = repo;
    }



    public void showMenu() {
        while (true) {
            printMenu();
            int choice = readIntInput();
            switch (choice) {
                case 1 -> addArtist();
                case 2 -> showAllArtists();
                case 3 -> getArtistById();
                case 4 -> updateArtist();
                case 5 -> deleteArtist();
                case 0 -> { return; }
                default -> System.out.println("Неверный ввод!");
            }
        }
    }

    private void printMenu() {
        System.out.println("\n=== Управление исполнителями ===");
        System.out.println("1. Добавить исполнителя");
        System.out.println("2. Показать список всех исполнителей");
        System.out.println("3. Найти исполнителя по ID");
        System.out.println("4. Обновить данные исполнителя");
        System.out.println("5. Удалить исполнителя");
        System.out.println("0. Назад");
        System.out.print("Выберите действие: ");
    }



    // Добавить исполнителя в БД
    private void addArtist() {
        System.out.print("\nВведите имя исполнителя: ");
        String name = scanner.nextLine();

        System.out.print("\nВведите страну: ");
        String country = scanner.nextLine();

        System.out.print("\nВведите дату основания (ГГГГ-ММ-ДД): ");
        LocalDate formed = readDateInput(scanner);

        System.out.print("\nВведите биографию: ");
        String biography = scanner.nextLine();

        // Создание и сохранение объекта
        Artist artist = new Artist(name, country, formed, biography);
        try {
            repo.addArtist(artist);
            System.out.println("\nИсполнитель добавлен с ID: " + artist.getArtistId());
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "\nОшибка БД: {0}", e.getMessage());
        }
    }

    // Удалить исполнителя по ID из БД
    private void deleteArtist() {
        System.out.print("\nВведите ID исполнителя для удаления: ");
        int id = readIntInput();

        try {
            if(repo.deleteArtist(id)) {
                System.out.println("\nИсполнитель удален.");
            } else {
                logger.log(Level.INFO,"\nИсполнителя с таким ID не найдено.");
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE,"Ошибка удаления: {0}", e.getMessage());
        }
    }

    // Получить весь список исполнителей
    private void showAllArtists() {
        try {
            List<Artist> artists = repo.getAllArtists();
            if (artists.isEmpty()) {
                System.out.println("\nСписок исполнителей пуст.");
                return;
            }

            System.out.println("\nСписок исполнителей:");
            for (Artist artist : artists) {
                System.out.printf("%d. [%s] %s %tF (%s)%n",
                        artist.getArtistId(),
                        artist.getName(),
                        artist.getCountry(),
                        artist.getFormedDate(),
                        artist.getBiography());
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE,"Ошибка при загрузке: {0}", e.getMessage());
        }
    }

    // Получить информацию об исполнителе (по ID)
    private void getArtistById() {
        System.out.print("\nВведите ID исполнителя: ");
        int id = readIntInput();

        try {
            Artist artist = repo.getArtistById(id);
            if (artist != null) {
                System.out.printf("%nНайден исполнитель: %s (%s) %tF%n",
                        artist.getName(),
                        artist.getCountry(),
                        artist.getFormedDate());
            } else {
                logger.log(Level.INFO, "\nИсполнитель не найден.");
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE,"Ошибка поиска: {0}", e.getMessage());
        }
    }

    // Обновить информацию об исполнителе
    private void updateArtist() {
        System.out.print("\nВведите ID исполнителя для обновления: ");
        int id = readIntInput();

        try {
            Artist artist = repo.getArtistById(id);
            if (artist == null) {
                logger.log(Level.INFO, "\nИсполнитель не найден.");
                return;
            }

            System.out.print("\nНовое имя (текущее: " + artist.getName() + "): ");
            String name = scanner.nextLine();
            if (!name.isEmpty()) artist.setName(name);

            System.out.print("\nНовая страна (текущая: " + artist.getCountry() + "): ");
            String country = scanner.nextLine();
            if (!country.isEmpty()) artist.setCountry(country);

            System.out.print("\nНовая дата создания (текущая: " + artist.getFormedDate() + "): ");
            LocalDate formedDate = readDateInput(scanner);
            if (formedDate != null) artist.setFormedDate(formedDate);

            System.out.print("\nНовая биография: ");
            String biography = scanner.nextLine();
            if (!biography.isEmpty()) artist.setBiography(biography);

            repo.updateArtist(artist);
            System.out.println("\nДанные обновлены.");
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Ошибка обновления: {0}", e.getMessage());
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
                String date = scanner.nextLine();
                if (date.isEmpty()) return null;
                return LocalDate.parse(scanner.nextLine(), formatter);
            } catch (Exception _) {
                System.out.println("\nОшибка! Используйте формат ГГГГ-ММ-ДД.");
            }
        }
    }
}
