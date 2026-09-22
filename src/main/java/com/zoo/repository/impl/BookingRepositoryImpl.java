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

    @Override
    public void save(Booking booking) {
        String sql = "INSERT INTO bookings (visitor_id, visit_date, price, status) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, booking.getVisitorId());
            pstmt.setTimestamp(2, Timestamp.valueOf(booking.getVisitDate()));
            pstmt.setInt(3, booking.getPrice());
            pstmt.setString(4, booking.getStatus().name());
            pstmt.executeUpdate();
        } catch (SQLException | DatabaseException e) {
            System.err.println("Ошибка сохранения нового бронирования: " + e.getMessage());
        }
    }

    @Override
    public Optional<Booking> findById(int id) {
        String sql = "SELECT * FROM bookings WHERE id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRowToBooking(rs));
                }
            }
        } catch (SQLException | DatabaseException e) {
            System.err.println("Ошибка поиска бронирования по ID: " + e.getMessage());
        }
        return Optional.empty();
    }

    @Override
    public List<Booking> findAll() {
        List<Booking> bookings = new ArrayList<>();
        String sql = "SELECT * FROM bookings";
        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                bookings.add(mapRowToBooking(rs));
            }
        } catch (SQLException | DatabaseException e) {
            System.err.println("Ошибка при выгрузке всех бронирований: " + e.getMessage());
        }
        return bookings;
    }

    @Override
    public void update(Booking booking) {
        String sql = "UPDATE bookings SET visitor_id = ?, visit_date = ?, price = ?, status = ? WHERE id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, booking.getVisitorId());
            pstmt.setTimestamp(2, Timestamp.valueOf(booking.getVisitDate()));
            pstmt.setInt(3, booking.getPrice());
            pstmt.setString(4, booking.getStatus().name());
            pstmt.setInt(5, booking.getId());
            pstmt.executeUpdate();
        } catch (SQLException | DatabaseException e) {
            System.err.println("Ошибка обновления бронирования: " + e.getMessage());
        }
    }

    @Override
    public void delete(int id) {
        String sql = "DELETE FROM bookings WHERE id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        } catch (SQLException | DatabaseException e) {
            System.err.println("Ошибка удаления бронирования: " + e.getMessage());
        }
    }

    @Override
    public List<Booking> findByStatus(BookingStatus status) {
        List<Booking> bookings = new ArrayList<>();
        String sql = "SELECT * FROM bookings WHERE status = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, status.name());
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    bookings.add(mapRowToBooking(rs));
                }
            }
        } catch (SQLException | DatabaseException e) {
            System.err.println("Ошибка фильтрации по статусу: " + e.getMessage());
        }
        return bookings;
    }

    @Override
    public List<Booking> findByVisitorId(int visitorId) {
        List<Booking> bookings = new ArrayList<>();
        String sql = "SELECT * FROM bookings WHERE visitor_id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, visitorId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    bookings.add(mapRowToBooking(rs));
                }
            }
        } catch (SQLException | DatabaseException e) {
            System.err.println("Ошибка поиска по ID посетителя: " + e.getMessage());
        }
        return bookings;
    }

    @Override
    public List<Booking> findByDateRange(LocalDateTime start, LocalDateTime end) {
        List<Booking> bookings = new ArrayList<>();
        String sql = "SELECT * FROM bookings WHERE visit_date BETWEEN ? AND ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setTimestamp(1, Timestamp.valueOf(start));
            pstmt.setTimestamp(2, Timestamp.valueOf(end));
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    bookings.add(mapRowToBooking(rs));
                }
            }
        } catch (SQLException | DatabaseException e) {
            System.err.println("Ошибка фильтрации по диапазону дат: " + e.getMessage());
        }
        return bookings;
    }

    @Override
    public List<Booking> searchByVisitorName(String namePart) {
        List<Booking> bookings = new ArrayList<>();
        String sql = "SELECT b.* FROM bookings b JOIN visitors v ON b.visitor_id = v.id WHERE LOWER(v.full_name) LIKE LOWER(?)";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, "%" + namePart + "%");
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    bookings.add(mapRowToBooking(rs));
                }
            }
        } catch (SQLException | DatabaseException e) {
            System.err.println("Ошибка поиска по имени посетителя: " + e.getMessage());
        }
        return bookings;
    }

    @Override
    public int countByStatus(BookingStatus status) {
        String sql = "SELECT COUNT(*) FROM bookings WHERE status = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, status.name());
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException | DatabaseException e) {
            System.err.println("Ошибка подсчета записей по статусу: " + e.getMessage());
        }
        return 0;
    }

    @Override
    public int countAll() {
        String sql = "SELECT COUNT(*) FROM bookings";
        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException | DatabaseException e) {
            System.err.println("Ошибка подсчета всех записей: " + e.getMessage());
        }
        return 0;
    }

    @Override
    public int countHighTicketCount(int minTickets) {
        String sql = "SELECT COUNT(*) FROM bookings WHERE price > ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, minTickets);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException | DatabaseException e) {
            System.err.println("Ошибка подсчета дорогих билетов: " + e.getMessage());
        }
        return 0;
    }

    private Booking mapRowToBooking(ResultSet rs) throws SQLException {
        Booking booking = new Booking();
        booking.setId(rs.getInt("id"));
        booking.setVisitorId(rs.getInt("visitor_id"));
        
        Timestamp timestamp = rs.getTimestamp("visit_date");
        if (timestamp != null) {
            booking.setVisitDate(timestamp.toLocalDateTime());
        }
        
        booking.setPrice(rs.getInt("price"));
        booking.setStatus(BookingStatus.valueOf(rs.getString("status")));
        return booking;
    }
}
