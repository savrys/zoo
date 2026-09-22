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
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, visitor.getFullName());
            pstmt.setString(2, visitor.getEmail());
            pstmt.setString(3, visitor.getPhone());
            pstmt.executeUpdate();
        } catch (SQLException | DatabaseException e) {
            System.err.println("Ошибка сохранения посетителя: " + e.getMessage());
        }
    }

    @Override
    public Optional<Visitor> findById(int id) {
        String sql = "SELECT * FROM visitors WHERE id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    Visitor v = new Visitor();
                    v.setId(rs.getInt("id"));
                    v.setFullName(rs.getString("full_name"));
                    v.setEmail(rs.getString("email"));
                    v.setPhone(rs.getString("phone"));
                    return Optional.of(v);
                }
            }
        } catch (SQLException | DatabaseException e) {
            System.err.println("Ошибка поиска посетителя по ID: " + e.getMessage());
        }
        return Optional.empty();
    }

    @Override
    public List<Visitor> findAll() {
        List<Visitor> visitors = new ArrayList<>();
        String sql = "SELECT * FROM visitors";
        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Visitor v = new Visitor();
                v.setId(rs.getInt("id"));
                v.setFullName(rs.getString("full_name"));
                v.setEmail(rs.getString("email"));
                v.setPhone(rs.getString("phone"));
                visitors.add(v);
            }
        } catch (SQLException | DatabaseException e) {
            System.err.println("Ошибка выгрузки посетителей: " + e.getMessage());
        }
        return visitors;
    }

    @Override
    public void update(Visitor visitor) {
        String sql = "UPDATE visitors SET full_name = ?, email = ?, phone = ? WHERE id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, visitor.getFullName());
            pstmt.setString(2, visitor.getEmail());
            pstmt.setString(3, visitor.getPhone());
            pstmt.setInt(4, visitor.getId());
            pstmt.executeUpdate();
        } catch (SQLException | DatabaseException e) {
            System.err.println("Ошибка обновления посетителя: " + e.getMessage());
        }
    }

    @Override
    public void delete(int id) {
        String sql = "DELETE FROM visitors WHERE id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        } catch (SQLException | DatabaseException e) {
            System.err.println("Ошибка удаления посетителя: " + e.getMessage());
        }
    }
}
