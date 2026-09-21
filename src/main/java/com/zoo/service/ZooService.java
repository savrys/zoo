package com.zoo.service;

import com.zoo.model.Booking;
import com.zoo.model.BookingStatus;
import com.zoo.model.Visitor;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public interface ZooService {
    void addVisitor(String name, String email, String phone);
    List<Visitor> getAllVisitors();

    void createBooking(int visitorId, LocalDateTime visitDate, int ticketCount);
    List<Booking> getAllBookings();
    void updateBookingStatus(int bookingId, BookingStatus newStatus);
    void deleteBooking(int bookingId);

    List<Booking> searchBookingsByVisitorName(String namePart);
    List<Booking> filterBookingsByStatus(BookingStatus status);
    List<Booking> filterBookingsByDateRange(LocalDateTime start, LocalDateTime end);

    Map<String, Integer> getStatistics();
}