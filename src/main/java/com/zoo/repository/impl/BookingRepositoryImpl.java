package com.zoo.repository.impl;

import com.zoo.config.DatabaseConfig;
import com.zoo.exception.DatabaseException;
import com.zoo.model.Booking;
import com.zoo.model.BookingStatus;
import com.zoo.repository.BookingRepository;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class BookingRepositoryImpl implements BookingRepository {

    private Booking mapRow(ResultSet rs) throws SQLException {
        int ticketCount = rs.getInt("ticket_count");
        if (rs.wasNull()) {
            ticketCount = 1;
        }
        
        return new Booking(
            rs.getInt("id"),
            rs.getInt("visitor_id"),
            rs.getTimestamp("visit_date").toLocalDateTime(),
            ticketCount,
            BookingStatus.valueOf(rs.getString("status")),
            rs.getBigDecimal("price")
        );
    }

    @Override
    public void save(Booking booking) {
        String sql = "INSERT INTO bookings (visitor_id, visit_date, ticket_count, status, price) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            ps.setInt(1, booking.getVisitorId());
            ps.setTimestamp(2, Timestamp.valueOf(booking.getVisitDate()));
            ps.setInt(3, booking.getTicketCount() != null ? booking.getTicketCount() : 1);
            ps.setString(4, booking.getStatus().name());
            ps.setBigDecimal(5, booking.getPrice());
            
            ps.executeUpdate();
            
            try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    booking.setId(generatedKeys.getInt(1));
                }
            }
        } catch (SQLException | DatabaseException e) {
            throw new RuntimeException("Ошибка сохранения бронирования", e);
        }
    }

    @Override
    public List<Booking> findAll() {
        List<Booking> bookings = new ArrayList<>();
        String sql = "SELECT * FROM bookings";
        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                bookings.add(mapRow(rs));
            }
        } catch (SQLException | DatabaseException e) {
            throw new RuntimeException("Ошибка поиска всех бронирований", e);
        }
        return bookings;
    }

    @Override
    public Optional<Booking> findById(int id) {
        String sql = "SELECT * FROM bookings WHERE id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRow(rs));
                }
            }
        } catch (SQLException | DatabaseException e) {
            throw new RuntimeException("Ошибка поиска бронирования по ID", e);
        }
        return Optional.empty();
    }

    @Override
    public List<Booking> findByVisitorId(int visitorId) {
        List<Booking> bookings = new ArrayList<>();
        String sql = "SELECT * FROM bookings WHERE visitor_id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, visitorId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) bookings.add(mapRow(rs));
            }
        } catch (SQLException | DatabaseException e) {
            throw new RuntimeException("Ошибка поиска по ID посетителя", e);
        }
        return bookings;
    }

    @Override
    public List<Booking> findByStatus(BookingStatus status) {
        List<Booking> bookings = new ArrayList<>();
        String sql = "SELECT * FROM bookings WHERE status = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status.name());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) bookings.add(mapRow(rs));
            }
        } catch (SQLException | DatabaseException e) {
            throw new RuntimeException("Ошибка поиска по статусу", e);
        }
        return bookings;
    }

    @Override
    public List<Booking> findByDateRange(LocalDateTime start, LocalDateTime end) {
        List<Booking> bookings = new ArrayList<>();
        String sql = "SELECT * FROM bookings WHERE visit_date BETWEEN ? AND ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setTimestamp(1, Timestamp.valueOf(start));
            ps.setTimestamp(2, Timestamp.valueOf(end));
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) bookings.add(mapRow(rs));
            }
        } catch (SQLException | DatabaseException e) {
            throw new RuntimeException("Ошибка поиска по диапазону дат", e);
        }
        return bookings;
    }

    @Override
    public void update(Booking booking) {
        String sql = "UPDATE bookings SET visitor_id = ?, visit_date = ?, ticket_count = ?, status = ?, price = ? WHERE id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, booking.getVisitorId());
            ps.setTimestamp(2, Timestamp.valueOf(booking.getVisitDate()));
            ps.setInt(3, booking.getTicketCount() != null ? booking.getTicketCount() : 1);
            ps.setString(4, booking.getStatus().name());
            ps.setBigDecimal(5, booking.getPrice());
            ps.setInt(6, booking.getId());
            ps.executeUpdate();
        } catch (SQLException | DatabaseException e) {
            throw new RuntimeException("Ошибка обновления бронирования", e);
        }
    }

    @Override
    public void delete(int id) {
        String sql = "DELETE FROM bookings WHERE id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException | DatabaseException e) {
            throw new RuntimeException("Ошибка удаления бронирования", e);
        }
    }

    @Override
    public List<Booking> searchByVisitorName(String name) {
        List<Booking> bookings = new ArrayList<>();
        String sql = "SELECT b.* FROM bookings b JOIN visitors v ON b.visitor_id = v.id " +
                     "WHERE v.first_name ILIKE ? OR v.last_name ILIKE ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, "%" + name + "%");
            ps.setString(2, "%" + name + "%");
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) bookings.add(mapRow(rs));
            }
        } catch (SQLException | DatabaseException e) {
            throw new RuntimeException("Ошибка поиска по имени посетителя", e);
        }
        return bookings;
    }

    @Override
    public int countAll() {
        String sql = "SELECT COUNT(*) FROM bookings";
        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException | DatabaseException e) {
            throw new RuntimeException("Ошибка подсчета всех записей", e);
        }
        return 0;
    }

    @Override
    public int countByStatus(BookingStatus status) {
        String sql = "SELECT COUNT(*) FROM bookings WHERE status = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status.name());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
        } catch (SQLException | DatabaseException e) {
            throw new RuntimeException("Ошибка подсчета по статусу", e);
        }
        return 0;
    }

    @Override
    public int countHighTicketCount(int minTickets) {
        String sql = "SELECT COUNT(*) FROM bookings WHERE ticket_count >= ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, minTickets);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
        } catch (SQLException | DatabaseException e) {
            throw new RuntimeException("Ошибка подсчета крупных броней", e);
        }
        return 0;
    }
}
