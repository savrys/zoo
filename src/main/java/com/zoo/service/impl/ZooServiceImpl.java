package com.zoo.service.impl;

import com.zoo.exception.BusinessException;
import com.zoo.model.Booking;
import com.zoo.model.BookingStatus;
import com.zoo.model.Visitor;
import com.zoo.repository.BookingRepository;
import com.zoo.repository.VisitorRepository;
import com.zoo.repository.impl.BookingRepositoryImpl;
import com.zoo.repository.impl.VisitorRepositoryImpl;
import com.zoo.service.ZooService;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ZooServiceImpl implements ZooService {

    private final VisitorRepository visitorRepository;
    private final BookingRepository bookingRepository;

    public ZooServiceImpl() {
        this.visitorRepository = new VisitorRepositoryImpl();
        this.bookingRepository = new BookingRepositoryImpl();
    }

    @Override
    public void addVisitor(String name, String email, String phone) {
        if (name == null || name.trim().isEmpty()) {
            throw new BusinessException("Имя посетителя не может быть пустым.");
        }
        if (email == null || !email.contains("@")) {
            throw new BusinessException("Некорректный email.");
        }
        Visitor visitor = new Visitor(name, email, phone);
        visitorRepository.save(visitor);
    }

    @Override
    public List<Visitor> getAllVisitors() {
        return visitorRepository.findAll();
    }

    @Override
    public void createBooking(int visitorId, LocalDateTime visitDate, int ticketCount) {
        Visitor visitor = visitorRepository.findById(visitorId)
                .orElseThrow(() -> new BusinessException("Посетитель с ID " + visitorId + " не найден."));

        if (visitDate.isBefore(LocalDateTime.now())) {
            throw new BusinessException("Нельзя создать бронирование на прошедшую дату.");
        }

        if (ticketCount <= 0 || ticketCount > 10) {
            throw new BusinessException("Количество билетов должно быть от 1 до 10.");
        }

        Booking booking = new Booking(visitor.getId(), visitDate, ticketCount, BookingStatus.CREATED);
        bookingRepository.save(booking);
    }

    @Override
    public List<Booking> getAllBookings() {
        return bookingRepository.findAll();
    }

    @Override
    public void updateBookingStatus(int bookingId, BookingStatus newStatus) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new BusinessException("Бронирование с ID " + bookingId + " не найдено."));

        BookingStatus currentStatus = booking.getStatus();

        if (currentStatus == BookingStatus.COMPLETED || currentStatus == BookingStatus.CANCELLED) {
            throw new BusinessException("Нельзя изменить статус завершенного или отмененного бронирования.");
        }
        if (currentStatus == BookingStatus.CREATED && newStatus == BookingStatus.COMPLETED) {
            throw new BusinessException("Нельзя завершить бронирование, минуя статус CONFIRMED.");
        }

        booking.setStatus(newStatus);
        bookingRepository.update(booking);
    }

    @Override
    public void deleteBooking(int bookingId) {
        bookingRepository.findById(bookingId)
                .orElseThrow(() -> new BusinessException("Бронирование не найдено."));
        bookingRepository.delete(bookingId);
    }

    @Override
    public List<Booking> searchBookingsByVisitorName(String namePart) {
        if (namePart == null || namePart.trim().isEmpty()) {
            throw new BusinessException("Строка поиска не может быть пустой.");
        }
        return bookingRepository.searchByVisitorName(namePart);
    }

    @Override
    public List<Booking> filterBookingsByStatus(BookingStatus status) {
        return bookingRepository.findByStatus(status);
    }

    @Override
    public List<Booking> filterBookingsByDateRange(LocalDateTime start, LocalDateTime end) {
        if (start.isAfter(end)) {
            throw new BusinessException("Начальная дата не может быть позже конечной.");
        }
        return bookingRepository.findByDateRange(start, end);
    }

    @Override
    public Map<String, Integer> getStatistics() {
        Map<String, Integer> stats = new LinkedHashMap<>();
        stats.put("Всего посетителей", visitorRepository.findAll().size());
        stats.put("Всего бронирований", bookingRepository.countAll());
        stats.put("Активных (CREATED)", bookingRepository.countByStatus(BookingStatus.CREATED));
        stats.put("Подтвержденных (CONFIRMED)", bookingRepository.countByStatus(BookingStatus.CONFIRMED));
        stats.put("Завершенных (COMPLETED)", bookingRepository.countByStatus(BookingStatus.COMPLETED));
        stats.put("Отмененных (CANCELLED)", bookingRepository.countByStatus(BookingStatus.CANCELLED));
        stats.put("Записей с 3+ билетами", bookingRepository.countHighTicketCount(3));
        return stats;
    }
}