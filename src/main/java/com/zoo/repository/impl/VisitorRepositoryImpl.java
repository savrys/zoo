package com.zoo.repository.impl;

import com.zoo.config.DatabaseConfig;
import com.zoo.exception.DatabaseException;
import com.zoo.model.Visitor;
import com.zoo.repository.VisitorRepository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class VisitorRepositoryImpl implements VisitorRepository {

    @Override
    public void save(Visitor visitor) {
        String sql = "INSERT INTO visitors (full_name, email, phone) VALUES (?, ?, ?)";
        try (Connection conn = DriverManager.getConnection(DatabaseConfig.URL, DatabaseConfig.USER, DatabaseConfig.PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, visitor.getFullName());
            stmt.setString(2, visitor.getEmail());
            stmt.setString(3, visitor.getPhone());
            stmt.executeUpdate();

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    visitor.setId(generatedKeys.getInt(1));
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Ошибка при сохранении посетителя", e);
        }
    }

    @Override
    public Optional<Visitor> findById(int id) {
        String sql = "SELECT * FROM visitors WHERE id = ?";
        try (Connection conn = DriverManager.getConnection(DatabaseConfig.URL, DatabaseConfig.USER, DatabaseConfig.PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRowToVisitor(rs));
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Ошибка при поиске посетителя по ID", e);
        }
        return Optional.empty();
    }

    @Override
    public List<Visitor> findAll() {
        List<Visitor> visitors = new ArrayList<>();
        String sql = "SELECT * FROM visitors ORDER BY id";
        try (Connection conn = DriverManager.getConnection(DatabaseConfig.URL, DatabaseConfig.USER, DatabaseConfig.PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                visitors.add(mapRowToVisitor(rs));
            }
        } catch (SQLException e) {
            throw new DatabaseException("Ошибка при получении списка посетителей", e);
        }
        return visitors;
    }

    @Override
    public void update(Visitor visitor) {
        String sql = "UPDATE visitors SET full_name = ?, email = ?, phone = ? WHERE id = ?";
        try (Connection conn = DriverManager.getConnection(DatabaseConfig.URL, DatabaseConfig.USER, DatabaseConfig.PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, visitor.getFullName());
            stmt.setString(2, visitor.getEmail());
            stmt.setString(3, visitor.getPhone());
            stmt.setInt(4, visitor.getId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseException("Ошибка при обновлении посетителя", e);
        }
    }

    @Override
    public void delete(int id) {
        String sql = "DELETE FROM visitors WHERE id = ?";
        try (Connection conn = DriverManager.getConnection(DatabaseConfig.URL, DatabaseConfig.USER, DatabaseConfig.PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseException("Ошибка при удалении посетителя", e);
        }
    }

    private Visitor mapRowToVisitor(ResultSet rs) throws SQLException {
        return new Visitor(
                rs.getInt("id"),
                rs.getString("full_name"),
                rs.getString("email"),
                rs.getString("phone")
        );
    }
}