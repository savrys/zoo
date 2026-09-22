package com.zoo.service.impl;

import com.zoo.exception.BusinessException;
import com.zoo.model.Booking;
import com.zoo.model.BookingStatus;
import com.zoo.model.Visitor;
import com.zoo.repository.BookingRepository;
import com.zoo.repository.VisitorRepository;
import com.zoo.service.ZooService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public class ZooServiceImpl implements ZooService {

    private final BookingRepository bookingRepository;
    private final VisitorRepository visitorRepository;

    public ZooServiceImpl(BookingRepository bookingRepository, VisitorRepository visitorRepository) {
        this.bookingRepository = bookingRepository;
        this.visitorRepository = visitorRepository;
    }

    @Override
    public void addVisitor(String fullName, String email, String phone) {
        Visitor visitor = new Visitor();
        visitor.setFullName(fullName);
        visitor.setEmail(email);
        visitor.setPhone(phone);
        visitorRepository.save(visitor);
    }

    @Override
    public List<Visitor> getAllVisitors() {
        return visitorRepository.findAll();
    }

    @Override
    public void createBooking(int visitorId, LocalDateTime visitDate, int price) throws BusinessException {
        // Правило 1: Контроль обязательного наличия связанного пользователя
        if (visitorRepository.findById(visitorId).isEmpty()) {
            throw new BusinessException("Бизнес-ошибка: Посетитель с ID " + visitorId + " не существует!");
        }

        // Правило 2: Бронирование не может быть оформлено на прошедшее время
        if (visitDate.isBefore(LocalDateTime.now())) {
            throw new BusinessException("Бизнес-ошибка: Нельзя забронировать билет на прошедшее время!");
        }

        // Правило 3: Стоимость не должна быть отрицательной или равной нулю
        if (price <= 0) {
            throw new BusinessException("Бизнес-ошибка: Стоимость билета обязана быть больше нуля!");
        }

        Booking booking = new Booking();
        booking.setVisitorId(visitorId);
        booking.setVisitDate(visitDate);
        booking.setPrice(price);
        booking.setStatus(BookingStatus.CREATED); // Правило 4: Инициализация только в стартовом статусе

        bookingRepository.save(booking);
    }

    @Override
    public List<Booking> getAllBookings() {
        return bookingRepository.findAll();
    }

    @Override
    public void updateBookingStatus(int bookingId, BookingStatus newStatus) throws BusinessException {
        Optional<Booking> optionalBooking = bookingRepository.findById(bookingId);
        if (optionalBooking.isEmpty()) {
            throw new BusinessException("Ошибка: Запись бронирования не найдена.");
        }

        Booking current = optionalBooking.get();

        // Правило 5: Запрещенный перевод завершенных визитов в статус отмены
        if (current.getStatus() == BookingStatus.COMPLETED && newStatus == BookingStatus.CANCELLED) {
            throw new BusinessException("Бизнес-ошибка: Нельзя аннулировать бронирование, которое уже завершено (COMPLETED)!");
        }
        if (current.getStatus() == BookingStatus.CANCELLED) {
            throw new BusinessException("Бизнес-ошибка: Изменение отмененного заказа заблокировано.");
        }

        current.setStatus(newStatus);
        bookingRepository.update(current);
    }

    @Override
    public void deleteBooking(int id) throws BusinessException {
        if (bookingRepository.findById(id).isEmpty()) {
            throw new BusinessException("Ошибка: Бронирование с указанным ID не существует.");
        }
        bookingRepository.delete(id);
    }

    @Override
    public List<Booking> searchBookingsByVisitorName(String namePart) {
        return bookingRepository.searchByVisitorName(namePart);
    }

    @Override
    public List<Booking> filterBookingsByStatus(BookingStatus status) {
        return bookingRepository.findByStatus(status);
    }

    @Override
    public List<Booking> filterBookingsByDateRange(LocalDateTime start, LocalDateTime end) {
        return bookingRepository.findByDateRange(start, end);
    }

    @Override
    public String getStatistics() {
        int totalBookings = bookingRepository.countAll();
        int createdCount = bookingRepository.countByStatus(BookingStatus.CREATED);
        int confirmedCount = bookingRepository.countByStatus(BookingStatus.CONFIRMED);
        int completedCount = bookingRepository.countByStatus(BookingStatus.COMPLETED);
        int vipTickets = bookingRepository.countHighTicketCount(800); // Билеты дороже 800 рублей

        return String.format(
                "========================================\n" +
                "          СТАТИСТИКА СИСТЕМЫ           \n" +
                "========================================\n" +
                "1. Всего бронирований в системе: %d\n" +
                "2. В статусе ожидания (CREATED): %d\n" +
                "3. Подтвержденных (CONFIRMED):   %d\n" +
                "4. Успешно завершенных:          %d\n" +
                "5. Из них премиум-билетов (>800р): %d\n" +
                "========================================",
                totalBookings, createdCount, confirmedCount, completedCount, vipTickets
        );
    }
}
