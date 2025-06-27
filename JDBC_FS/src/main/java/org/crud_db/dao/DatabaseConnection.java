package org.crud_db.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

// Обеспечение подсоединения к базе данных music_library_v1
public class DatabaseConnection {
    private static final String URL = "jdbc:postgresql://localhost:5432/music_db";
    private static final String USER = "postgres";
    private static final String PASSWORD = "Password";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}
