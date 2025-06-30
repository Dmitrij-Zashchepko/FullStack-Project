package org.crud_db;

import org.crud_db.dao.DatabaseConnection;
import org.crud_db.db_manager_app.DBManager;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Main {
    private static final Logger logger = Logger.getLogger(Main.class.getName());

    public static void main(String[] args) {
        try (Connection connection = DatabaseConnection.getConnection()) {
            DBManager dbManager = new DBManager(connection);
            dbManager.start();
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Ошибка БД: {0}", e.getMessage());
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Критическая ошибка: {0}", e.getMessage());
        }
    }
}