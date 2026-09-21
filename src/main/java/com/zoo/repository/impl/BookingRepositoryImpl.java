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

    private static final String BASE_SELECT =
            "SELECT b.id, b.visitor_id, v.full_name as visitor_name, b.visit_date, b.ticket_count, b.status, b.created_at " +
                    "FROM bookings b JOIN visitors v ON b.visitor_id = v.id ";

    @Override
    public void save(Booking booking) {
        String sql = "INSERT INTO bookings (visitor_id, visit_date, ticket_count, status) VALUES (?, ?, ?, ?)";
        try (Connection conn = DriverManager.getConnection(DatabaseConfig.URL, DatabaseConfig.USER, DatabaseConfig.PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, booking.getVisitorId());
            stmt.setTimestamp(2, Timestamp.valueOf(booking.getVisitDate()));
            stmt.setInt(3, booking.getTicketCount());
            stmt.setString(4, booking.getStatus().name());
            stmt.executeUpdate();

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    booking.setId(generatedKeys.getInt(1));
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Ошибка при сохранении бронирования", e);
        }
    }

    @Override
    public Optional<Booking> findById(int id) {
        String sql = BASE_SELECT + "WHERE b.id = ?";
        try (Connection conn = DriverManager.getConnection(DatabaseConfig.URL, DatabaseConfig.USER, DatabaseConfig.PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRowToBooking(rs));
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Ошибка при поиске бронирования по ID", e);
        }
        return Optional.empty();
    }

    @Override
    public List<Booking> findAll() {
        List<Booking> bookings = new ArrayList<>();
        String sql = BASE_SELECT + "ORDER BY b.id";
        try (Connection conn = DriverManager.getConnection(DatabaseConfig.URL, DatabaseConfig.USER, DatabaseConfig.PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                bookings.add(mapRowToBooking(rs));
            }
        } catch (SQLException e) {
            throw new DatabaseException("Ошибка при получении списка бронирований", e);
        }
        return bookings;
    }

    @Override
    public void update(Booking booking) {
        String sql = "UPDATE bookings SET visitor_id = ?, visit_date = ?, ticket_count = ?, status = ? WHERE id = ?";
        try (Connection conn = DriverManager.getConnection(DatabaseConfig.URL, DatabaseConfig.USER, DatabaseConfig.PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, booking.getVisitorId());
            stmt.setTimestamp(2, Timestamp.valueOf(booking.getVisitDate()));
            stmt.setInt(3, booking.getTicketCount());
            stmt.setString(4, booking.getStatus().name());
            stmt.setInt(5, booking.getId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseException("Ошибка при обновлении бронирования", e);
        }
    }

    @Override
    public void delete(int id) {
        String sql = "DELETE FROM bookings WHERE id = ?";
        try (Connection conn = DriverManager.getConnection(DatabaseConfig.URL, DatabaseConfig.USER, DatabaseConfig.PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseException("Ошибка при удалении бронирования", e);
        }
    }

    @Override
    public List<Booking> findByStatus(BookingStatus status) {
        List<Booking> bookings = new ArrayList<>();
        String sql = BASE_SELECT + "WHERE b.status = ? ORDER BY b.id";
        try (Connection conn = DriverManager.getConnection(DatabaseConfig.URL, DatabaseConfig.USER, DatabaseConfig.PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, status.name());
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    bookings.add(mapRowToBooking(rs));
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Ошибка при фильтрации по статусу", e);
        }
        return bookings;
    }

    @Override
    public List<Booking> findByVisitorId(int visitorId) {
        List<Booking> bookings = new ArrayList<>();
        String sql = BASE_SELECT + "WHERE b.visitor_id = ? ORDER BY b.id";
        try (Connection conn = DriverManager.getConnection(DatabaseConfig.URL, DatabaseConfig.USER, DatabaseConfig.PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, visitorId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    bookings.add(mapRowToBooking(rs));
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Ошибка при поиске по ID посетителя", e);
        }
        return bookings;
    }

    @Override
    public List<Booking> findByDateRange(LocalDateTime start, LocalDateTime end) {
        List<Booking> bookings = new ArrayList<>();
        String sql = BASE_SELECT + "WHERE b.visit_date BETWEEN ? AND ? ORDER BY b.visit_date";
        try (Connection conn = DriverManager.getConnection(DatabaseConfig.URL, DatabaseConfig.USER, DatabaseConfig.PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setTimestamp(1, Timestamp.valueOf(start));
            stmt.setTimestamp(2, Timestamp.valueOf(end));
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    bookings.add(mapRowToBooking(rs));
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Ошибка при фильтрации по дате", e);
        }
        return bookings;
    }

    @Override
    public List<Booking> searchByVisitorName(String namePart) {
        List<Booking> bookings = new ArrayList<>();
        String sql = BASE_SELECT + "WHERE LOWER(v.full_name) LIKE LOWER(?) ORDER BY b.id";
        try (Connection conn = DriverManager.getConnection(DatabaseConfig.URL, DatabaseConfig.USER, DatabaseConfig.PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, "%" + namePart + "%");
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    bookings.add(mapRowToBooking(rs));
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Ошибка при поиске по имени посетителя", e);
        }
        return bookings;
    }

    @Override
    public int countByStatus(BookingStatus status) {
        String sql = "SELECT COUNT(*) FROM bookings WHERE status = ?";
        try (Connection conn = DriverManager.getConnection(DatabaseConfig.URL, DatabaseConfig.USER, DatabaseConfig.PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, status.name());
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
        } catch (SQLException e) {
            throw new DatabaseException("Ошибка при подсчете по статусу", e);
        }
        return 0;
    }

    @Override
    public int countAll() {
        String sql = "SELECT COUNT(*) FROM bookings";
        try (Connection conn = DriverManager.getConnection(DatabaseConfig.URL, DatabaseConfig.USER, DatabaseConfig.PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            throw new DatabaseException("Ошибка при подсчете всех записей", e);
        }
        return 0;
    }

    @Override
    public int countHighTicketCount(int minTickets) {
        String sql = "SELECT COUNT(*) FROM bookings WHERE ticket_count >= ?";
        try (Connection conn = DriverManager.getConnection(DatabaseConfig.URL, DatabaseConfig.USER, DatabaseConfig.PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, minTickets);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
        } catch (SQLException e) {
            throw new DatabaseException("Ошибка при подсчете записей с большим кол-вом билетов", e);
        }
        return 0;
    }

    private Booking mapRowToBooking(ResultSet rs) throws SQLException {
        return new Booking(
                rs.getInt("id"),
                rs.getInt("visitor_id"),
                rs.getString("visitor_name"),
                rs.getTimestamp("visit_date").toLocalDateTime(),
                rs.getInt("ticket_count"),
                BookingStatus.valueOf(rs.getString("status")),
                rs.getTimestamp("created_at").toLocalDateTime()
        );
    }
}