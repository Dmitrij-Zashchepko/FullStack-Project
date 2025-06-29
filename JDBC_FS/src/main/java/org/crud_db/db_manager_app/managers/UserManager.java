// UserManager.java
package org.crud_db.db_manager_app.managers;

import org.crud_db.model.User;
import org.crud_db.repository.UserRepository;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Logger;
import java.util.logging.Level;

@SuppressWarnings("java:S106")
public class UserManager {
    private final Scanner scanner;
    private final UserRepository repo;
    private final PlaylistManager playlistManager;

    private static final String DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm";

    private static final Logger logger = Logger.getLogger(UserManager.class.getName());

    public UserManager(Scanner scanner, UserRepository repo, PlaylistManager playlistManager) {
        this.scanner = scanner;
        this.repo = repo;
        this.playlistManager = playlistManager;
    }

    public void showMenu() {
        while (true) {
            printMenu();
            int choice = readIntInput();
            switch (choice) {
                case 1 -> addUser();
                case 2 -> showAllUsers();
                case 3 -> getUserById();
                case 4 -> updateUser();
                case 5 -> deleteUser();
                case 6 -> showUserPlaylists();
                case 0 -> { return; }
                default -> System.out.println("Неверный ввод!");
            }
        }
    }

    private void printMenu() {
        System.out.println("\n=== Управление пользователями ===");
        System.out.println("1. Добавить пользователя");
        System.out.println("2. Показать список всех пользователей");
        System.out.println("3. Найти пользователя по ID");
        System.out.println("4. Обновить данные пользователя");
        System.out.println("5. Удалить пользователя");
        System.out.println("6. Показать плейлисты пользователя");
        System.out.println("0. Назад");
        System.out.print("Выберите действие: ");
    }



    private void addUser() {
        System.out.print("\nВведите имя пользователя: ");
        String username = scanner.nextLine();

        System.out.print("\nВведите email: ");
        String email = scanner.nextLine();

        System.out.print("\nВведите хэш пароля: ");
        String passwordHash = scanner.nextLine();

        User user = new User(username, email, passwordHash);
        try {
            repo.addUser(user);
            System.out.println("\nПользователь добавлен с ID: " + user.getUserId());
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Ошибка БД: {0}", e.getMessage());
        }
    }

    private void deleteUser() {
        System.out.print("\nВведите ID пользователя для удаления: ");
        int id = readIntInput();

        try {
            if(repo.deleteUser(id)) {
                System.out.println("\nПользователь удален.");
            } else {
                logger.log(Level.INFO, "Пользователя с таким ID не найдено.");
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Ошибка удаления: {0}", e.getMessage());
        }
    }

    private void showAllUsers() {
        try {
            List<User> users = repo.getAllUsers();
            if (users.isEmpty()) {
                System.out.println("\nСписок пользователей пуст.");
                return;
            }

            System.out.println("\nСписок пользователей:");
            for (User user : users) {
                System.out.printf("%d. %s (%s) - зарегистрирован %s, последний вход: %s%n",
                        user.getUserId(),
                        user.getUsername(),
                        user.getEmail(),
                        user.getCreatedAt().format(DateTimeFormatter.ofPattern(DATE_TIME_FORMAT)),
                        user.getLastLogin() != null ?
                                user.getLastLogin().format(DateTimeFormatter.ofPattern(DATE_TIME_FORMAT)) :
                                "никогда");
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Ошибка при загрузке: {0}", e.getMessage());
        }
    }

    private User getUserById() {
        System.out.print("\nВведите ID пользователя: ");
        int id = readIntInput();

        try {
            User user = repo.getUserById(id);
            if (user != null) {
                System.out.printf("%nНайден пользователь: %s (%s)%nЗарегистрирован: %s%nПоследний вход: %s%n",
                        user.getUsername(),
                        user.getEmail(),
                        user.getCreatedAt().format(DateTimeFormatter.ofPattern(DATE_TIME_FORMAT)),
                        user.getLastLogin() != null ?
                                user.getLastLogin().format(DateTimeFormatter.ofPattern(DATE_TIME_FORMAT)) :
                                "никогда");
                return user;
            } else {
                logger.log(Level.INFO, "Пользователь не найден.");
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Ошибка поиска: {0}", e.getMessage());
        }
        return null;
    }

    private void updateUser() {
        System.out.print("\nВведите ID пользователя для обновления: ");
        int id = readIntInput();

        try {
            User user = repo.getUserById(id);
            if (user == null) {
                logger.log(Level.INFO, "Пользователь не найден.");
                return;
            }

            System.out.print("\nНовое имя пользователя (текущее: " + user.getUsername() + "): ");
            String username = scanner.nextLine();
            if (!username.isEmpty()) user.setUsername(username);

            System.out.print("\nНовый email (текущий: " + user.getEmail() + "): ");
            String email = scanner.nextLine();
            if (!email.isEmpty()) user.setEmail(email);

            System.out.print("\nНовый хэш пароля (оставьте пустым, чтобы не менять): ");
            String passwordHash = scanner.nextLine();
            if (!passwordHash.isEmpty()) user.setPasswordHash(passwordHash);

            System.out.print("\nОбновить дату последнего входа на текущую? (y/n): ");
            String updateLogin = scanner.nextLine();
            if (updateLogin.equalsIgnoreCase("y")) {
                user.setLastLogin(LocalDateTime.now());
            }

            repo.updateUser(user);
            System.out.println("\nДанные обновлены.");
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Ошибка обновления: {0}", e.getMessage());
        }
    }

    private void showUserPlaylists() {
        User user = getUserById();
        if (user == null)
            return;
        playlistManager.showUserPlaylists(user.getUserId());
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