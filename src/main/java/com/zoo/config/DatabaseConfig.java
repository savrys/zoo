package com.zoo.config;

import com.zoo.exception.DatabaseException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConfig {
    // IP-адрес компьютера, на котором запущен PostgreSQL
    private static final String URL = "jdbc:postgresql://localhost:5432/zoo_db";
    private static final String USER = "postgres";
    private static final String PASSWORD = "p328555SDAp";

    public static Connection getConnection() throws DatabaseException {
        try {
            Class.forName("org.postgresql.Driver");
            return DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (ClassNotFoundException e) {
            throw new DatabaseException("Драйвер PostgreSQL JDBC не найден в classpath: " + e.getMessage());
        } catch (SQLException e) {
            throw new DatabaseException("Ошибка подключения к базе данных: " + e.getMessage());
        }
    }
}