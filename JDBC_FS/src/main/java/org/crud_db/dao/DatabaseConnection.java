package org.crud_db.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

// Обеспечение подсоединения к базе данных music_library_v1
public class DatabaseConnection {
    private static final String URL = "jdbc:postgresql://localhost:5432/music_db";
    private static final String USER = System.getenv("DB_USER");
    private static final String PASSWORD = System.getenv("DB_PASSWORD");

    private DatabaseConnection() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    public static Connection getConnection() throws SQLException {
        if (USER == null || PASSWORD == null) {
            throw new IllegalStateException("DB credentials not set in environment variables");
        }
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}
