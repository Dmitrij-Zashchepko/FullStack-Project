package org.crud_db;

import org.crud_db.dao.DatabaseConnection;
import org.crud_db.db_manager_app.DBManager;

import java.sql.Connection;
import java.sql.SQLException;

public class Main {
    public static void main(String[] args) {
        try (Connection connection = DatabaseConnection.getConnection()) {
            DBManager dbManager = new DBManager(connection);
            dbManager.start();
        } catch (SQLException e) {
            System.err.println("Ошибка БД: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Критическая ошибка: " + e.getMessage());
        }
    }
}