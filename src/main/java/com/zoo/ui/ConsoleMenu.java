package com.zoo.ui;

import com.zoo.exception.BusinessException;
import com.zoo.exception.DatabaseException;
import com.zoo.model.Booking;
import com.zoo.model.BookingStatus;
import com.zoo.model.Visitor;
import com.zoo.service.ZooService;
import com.zoo.service.impl.ZooServiceImpl;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class ConsoleMenu {
    private final ZooService zooService;
    private final Scanner scanner;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public ConsoleMenu() {
        this.zooService = new ZooServiceImpl();
        this.scanner = new Scanner(System.in);
    }

    public void start() {
        while (true) {
            printMainMenu();
            int choice = readInt("Выберите действие: ");
            try {
                switch (choice) {
                    case 1 -> manageVisitors();
                    case 2 -> manageBookings();
                    case 3 -> searchBookings();
                    case 4 -> filterBookings();
                    case 5 -> showStatistics();
                    case 0 -> {
                        System.out.println("Выход из программы...");
                        return;
                    }
                    default -> System.out.println("Неверный пункт меню.");
                }
            } catch (BusinessException | DatabaseException e) {
                System.out.println("Ошибка: " + e.getMessage());
            } catch (Exception e) {
                System.out.println("Непредвиденная ошибка: " + e.getMessage());
            }
        }
    }

    private void printMainMenu() {
        System.out.println("\n================ ЗООПАРК: БРОНИРОВАНИЕ ПОСЕЩЕНИЙ ================");
        System.out.println("1. Управление посетителями");
        System.out.println("2. Управление бронированиями");
        System.out.println("3. Поиск бронирований");
        System.out.println("4. Фильтрация бронирований");
        System.out.println("5. Статистика");
        System.out.println("0. Выход");
        System.out.println("==================================================================");
    }

    private void manageVisitors() {
        while (true) {
            System.out.println("\n--- Посетители ---");
            System.out.println("1. Добавить посетителя");
            System.out.println("2. Показать всех");
            System.out.println("0. Назад");
            int choice = readInt("Выбор: ");
            if (choice == 0) break;

            switch (choice) {
                case 1 -> {
                    System.out.print("Введите ФИО: ");
                    String name = scanner.nextLine();
                    System.out.print("Введите Email: ");
                    String email = scanner.nextLine();
                    System.out.print("Введите телефон: ");
                    String phone = scanner.nextLine();
                    zooService.addVisitor(name, email, phone);
                    System.out.println("✅ Посетитель успешно добавлен!");
                }
                case 2 -> {
                    List<Visitor> visitors = zooService.getAllVisitors();
                    if (visitors.isEmpty()) {
                        System.out.println("Список пуст.");
                    } else {
                        visitors.forEach(System.out::println);
                    }
                }
                default -> System.out.println("Неверный выбор.");
            }
        }
    }

    private void manageBookings() {
        while (true) {
            System.out.println("\n--- Бронирования ---");
            System.out.println("1. Создать бронирование");
            System.out.println("2. Показать все бронирования");
            System.out.println("3. Изменить статус бронирования");
            System.out.println("4. Удалить бронирование");
            System.out.println("0. Назад");
            int choice = readInt("Выбор: ");
            if (choice == 0) break;

            switch (choice) {
                case 1 -> {
                    List<Visitor> visitors = zooService.getAllVisitors();
                    if (visitors.isEmpty()) {
                        System.out.println("Сначала добавьте посетителей.");
                        break;
                    }
                    visitors.forEach(System.out::println);
                    int visitorId = readInt("Введите ID посетителя: ");
                    LocalDateTime date = readDateTime("Введите дату и время (yyyy-MM-dd HH:mm): ");
                    int tickets = readInt("Введите количество билетов: ");
                    zooService.createBooking(visitorId, date, tickets);
                    System.out.println("✅ Бронирование создано!");
                }
                case 2 -> {
                    List<Booking> bookings = zooService.getAllBookings();
                    if (bookings.isEmpty()) {
                        System.out.println("Список пуст.");
                    } else {
                        bookings.forEach(System.out::println);
                    }
                }
                case 3 -> {
                    int id = readInt("Введите ID бронирования: ");
                    System.out.println("Доступные статусы: CREATED, CONFIRMED, COMPLETED, CANCELLED");
                    BookingStatus status = readEnum("Введите новый статус: ");
                    zooService.updateBookingStatus(id, status);
                    System.out.println("✅ Статус обновлен!");
                }
                case 4 -> {
                    int id = readInt("Введите ID бронирования для удаления: ");
                    zooService.deleteBooking(id);
                    System.out.println("✅ Бронирование удалено!");
                }
                default -> System.out.println("Неверный выбор.");
            }
        }
    }

    private void searchBookings() {
        System.out.println("\n--- Поиск бронирований по имени посетителя ---");
        System.out.print("Введите часть имени: ");
        String namePart = scanner.nextLine();
        List<Booking> results = zooService.searchBookingsByVisitorName(namePart);
        if (results.isEmpty()) {
            System.out.println("Ничего не найдено.");
        } else {
            results.forEach(System.out::println);
        }
    }

    private void filterBookings() {
        while (true) {
            System.out.println("\n--- Фильтрация ---");
            System.out.println("1. По статусу");
            System.out.println("2. По диапазону дат");
            System.out.println("0. Назад");
            int choice = readInt("Выбор: ");
            if (choice == 0) break;

            switch (choice) {
                case 1 -> {
                    System.out.println("Доступные статусы: CREATED, CONFIRMED, COMPLETED, CANCELLED");
                    BookingStatus status = readEnum("Введите статус: ");
                    List<Booking> results = zooService.filterBookingsByStatus(status);
                    if (results.isEmpty()) System.out.println("Ничего не найдено.");
                    else results.forEach(System.out::println);
                }
                case 2 -> {
                    LocalDateTime start = readDateTime("Введите начальную дату (yyyy-MM-dd HH:mm): ");
                    LocalDateTime end = readDateTime("Введите конечную дату (yyyy-MM-dd HH:mm): ");
                    List<Booking> results = zooService.filterBookingsByDateRange(start, end);
                    if (results.isEmpty()) System.out.println("Ничего не найдено.");
                    else results.forEach(System.out::println);
                }
                default -> System.out.println("Неверный выбор.");
            }
        }
    }

    private void showStatistics() {
        System.out.println("\n--- СТАТИСТИКА ---");
        Map<String, Integer> stats = zooService.getStatistics();
        stats.forEach((key, value) -> System.out.println(key + ": " + value));
    }

    private int readInt(String prompt) {
        while (true) {
            try {
                System.out.print(prompt);
                String input = scanner.nextLine().trim();
                return Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("Ошибка: введите целое число.");
            }
        }
    }

    private LocalDateTime readDateTime(String prompt) {
        while (true) {
            try {
                System.out.print(prompt);
                String input = scanner.nextLine().trim();
                return LocalDateTime.parse(input, DATE_FORMATTER);
            } catch (DateTimeParseException e) {
                System.out.println("Ошибка: неверный формат даты. Используйте yyyy-MM-dd HH:mm");
            }
        }
    }

    private BookingStatus readEnum(String prompt) {
        while (true) {
            try {
                System.out.print(prompt);
                String input = scanner.nextLine().trim().toUpperCase();
                return BookingStatus.valueOf(input);
            } catch (IllegalArgumentException e) {
                System.out.println("Ошибка: неверный статус. Попробуйте снова.");
            }
        }
    }
}