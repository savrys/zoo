package com.zoo.config;

import com.zoo.exception.DatabaseException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConfig {
    private static final String URL = "jdbc:postgresql://localhost:5432/zoo_db";
    private static final String USER = "postgres";
    private static final String PASSWORD = "p328555SDAp";

    public static Connection getConnection() throws DatabaseException {
        try {
            return DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (SQLException e) {
            throw new DatabaseException("Критическая ошибка подключения к базе данных: " + e.getMessage());
        }
    }
}
