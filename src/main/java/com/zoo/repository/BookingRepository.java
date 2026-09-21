package com.zoo.repository;

import com.zoo.model.Booking;
import com.zoo.model.BookingStatus;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface BookingRepository {
    void save(Booking booking);
    Optional<Booking> findById(int id);
    List<Booking> findAll();
    void update(Booking booking);
    void delete(int id);
    List<Booking> findByStatus(BookingStatus status);
    List<Booking> findByVisitorId(int visitorId);
    List<Booking> findByDateRange(LocalDateTime start, LocalDateTime end);
    List<Booking> searchByVisitorName(String namePart);
    int countByStatus(BookingStatus status);
    int countAll();
    int countHighTicketCount(int minTickets);
}