package com.zoo.service;

import com.zoo.exception.BusinessException;
import com.zoo.model.Booking;
import com.zoo.model.BookingStatus;
import com.zoo.model.Visitor;

import java.time.LocalDateTime;
import java.util.List;

public interface ZooService {
    void addVisitor(String fullName, String email, String phone);
    List<Visitor> getAllVisitors();
    
    void createBooking(int visitorId, LocalDateTime visitDate, int price) throws BusinessException;
    List<Booking> getAllBookings();
    void updateBookingStatus(int bookingId, BookingStatus newStatus) throws BusinessException;
    void deleteBooking(int id) throws BusinessException;
    
    List<Booking> searchBookingsByVisitorName(String namePart);
    List<Booking> filterBookingsByStatus(BookingStatus status);
    List<Booking> filterBookingsByDateRange(LocalDateTime start, LocalDateTime end);
    
    String getStatistics();
}
