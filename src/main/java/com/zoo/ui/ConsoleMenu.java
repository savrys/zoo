package com.zoo.ui;

import com.zoo.exception.BusinessException;
import com.zoo.model.Booking;
import com.zoo.model.BookingStatus;
import com.zoo.model.Visitor;
import com.zoo.service.ZooService;
import com.zoo.untils.ExcelExporter;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Scanner;

public class ConsoleMenu {

    private final ZooService zooService;
    private final Scanner scanner;
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public ConsoleMenu(ZooService zooService) {
        this.zooService = zooService;
        this.scanner = new Scanner(System.in);
    }

    public void start() {
        while (true) {
            System.out.println("\n========================================");
            System.out.println("     СИСТЕМА БРОНИРОВАНИЯ ЗООПАРКА     ");
            System.out.println("========================================");
            System.out.println("1. Добавить нового посетителя");
            System.out.println("2. Просмотр списка посетителей");
            System.out.println("3. Просмотр всех бронирований");
            System.out.println("4. Создать новое бронирование");
            System.out.println("5. Изменить статус бронирования");
            System.out.println("6. Удалить бронирование");
            System.out.println("7. Поиск и фильтрация бронирований");
            System.out.println("8. Вывести аналитическую статистику");
            System.out.println("9. Экспорт данных в Excel (.xlsx)");
            System.out.println("0. Выход");
            System.out.print("Выберите действие: ");

            int choice = readIntegerInput();
            try {
                switch (choice) {
                    case 1 -> addVisitorForm();
                    case 2 -> showVisitors();
                    case 3 -> showAllBookings();
                    case 4 -> createBookingForm();
                    case 5 -> updateStatusForm();
                    case 6 -> deleteBookingForm();
                    case 7 -> searchAndFilterSubmenu();
                    case 8 -> showStatistics();
                    case 9 -> exportToExcelForm();
                    case 0 -> {
                        System.out.println("Работа завершена. До свидания!");
                        return;
                    }
                    default -> System.out.println("Ошибка: Выбран несуществующий пункт меню!");
                }
            } catch (BusinessException e) {
                System.out.println("\n[НАРУШЕНИЕ БИЗНЕС-ПРАВИЛА]: " + e.getMessage());
            } catch (Exception e) {
                System.out.println("\n[ОШИБКА ПРИЛОЖЕНИЯ]: " + e.getMessage());
            }
        }
    }

    private void addVisitorForm() {
        System.out.println("\n--- Регистрация нового посетителя ---");
        System.out.print("Введите ФИО: ");
        String fullName = scanner.nextLine().trim();
        System.out.print("Введите Email: ");
        String email = scanner.nextLine().trim();
        System.out.print("Введите номер телефона: ");
        String phone = scanner.nextLine().trim();

        zooService.addVisitor(fullName, email, phone);
        System.out.println("Успешно: Посетитель добавлен в систему.");
    }

    private void showVisitors() {
        System.out.println("\n--- Список посетителей зоопарка ---");
        List<Visitor> visitors = zooService.getAllVisitors();
        if (visitors.isEmpty()) {
            System.out.println("[База посетителей пуста]");
            return;
        }
        visitors.forEach(v -> System.out.printf("ID: %d | ФИО: %s | Email: %s | Тел: %s\n", 
                v.getId(), v.getFullName(), v.getEmail(), v.getPhone()));
    }

    private void showAllBookings() {
        System.out.println("\n--- Все зарегистрированные бронирования ---");
        printBookingList(zooService.getAllBookings());
    }

    private void createBookingForm() throws BusinessException {
        System.out.println("\n--- Регистрация нового бронирования билета ---");
        System.out.print("Введите ID посетителя: ");
        int visitorId = readIntegerInput();
        System.out.print("Введите дату и время визита (ГГГГ-ММ-ДД ХХ:ММ): ");
        LocalDateTime visitDate = readDateTimeInput();
        System.out.print("Введите стоимость билета (целое число): ");
        int price = readIntegerInput();

        zooService.createBooking(visitorId, visitDate, price);
        System.out.println("Успешно: Новое бронирование зафиксировано в СУБД.");
    }

    private void updateStatusForm() throws BusinessException {
        System.out.print("Введите ID изменяемого бронирования: ");
        int id = readIntegerInput();
        System.out.println("Доступные статусы: 1. CREATED, 2. CONFIRMED, 3. COMPLETED, 4. CANCELLED");
        System.out.print("Выберите номер нового статуса: ");
        int statusNum = readIntegerInput();
        
        BookingStatus status = switch (statusNum) {
            case 1 -> BookingStatus.CREATED;
            case 2 -> BookingStatus.CONFIRMED;
            case 3 -> BookingStatus.COMPLETED;
            case 4 -> BookingStatus.CANCELLED;
            default -> null;
        };

        if (status == null) {
            System.out.println("Ошибка: Введен некорректный вариант статуса.");
            return;
        }

        zooService.updateBookingStatus(id, status);
        System.out.println("Успешно: Статус записи обновлен.");
    }

    private void deleteBookingForm() throws BusinessException {
        System.out.print("Введите ID удаляемого бронирования: ");
        int id = readIntegerInput();
        zooService.deleteBooking(id);
        System.out.println("Успешно: Запись удалена.");
    }

    private void searchAndFilterSubmenu() {
        System.out.println("\n--- Меню поиска и фильтрации ---");
        System.out.println("1. Найти бронирования по ФИО посетителя (или части имени)");
        System.out.println("2. Фильтровать по статусу");
        System.out.println("3. Фильтровать по диапазону дат и времени");
        System.out.print("Выберите операцию: ");
        
        int subChoice = readIntegerInput();
        switch (subChoice) {
            case 1 -> {
                System.out.print("Введите имя или его часть: ");
                String namePart = scanner.nextLine().trim();
                printBookingList(zooService.searchBookingsByVisitorName(namePart));
            }
            case 2 -> {
                System.out.println("1. CREATED, 2. CONFIRMED, 3. COMPLETED, 4. CANCELLED");
                System.out.print("Выберите номер статуса: ");
                int st = readIntegerInput();
                if (st >= 1 && st <= 4) {
                    printBookingList(zooService.filterBookingsByStatus(BookingStatus.values()[st - 1]));
                } else {
                    System.out.println("Некорректный выбор.");
                }
            }
            case 3 -> {
                System.out.print("Введите начальную дату (ГГГГ-ММ-ДД ХХ:ММ): ");
                LocalDateTime start = readDateTimeInput();
                System.out.print("Введите конечную дату (ГГГГ-ММ-ДД ХХ:ММ): ");
                LocalDateTime end = readDateTimeInput();
                printBookingList(zooService.filterBookingsByDateRange(start, end));
            }
            default -> System.out.println("Пункт меню не распознан. Возврат.");
        }
    }

    private void showStatistics() {
        System.out.println("\n" + zooService.getStatistics());
    }

    private void exportToExcelForm() {
        String path = "bookings_report.xlsx";
        try {
            ExcelExporter.exportBookings(zooService.getAllBookings(), path);
            System.out.println("Успешно: Отчет сгенерирован и сохранен в корень проекта под именем: " + path);
        } catch (IOException e) {
            System.out.println("Ошибка записи файла Excel: " + e.getMessage());
        }
    }

    private void printBookingList(List<Booking> list) {
        if (list.isEmpty()) {
            System.out.println("[Данные не найдены]");
            return;
        }
        list.forEach(b -> System.out.printf("ID: %d | Посетитель ID: %d | Дата визита: %s | Цена: %d руб. | Статус: %s\n",
                b.getId(), b.getVisitorId(), b.getVisitDate().format(FORMATTER), b.getPrice(), b.getStatus()));
    }

    // --- Обработка ввода (Защита от аварийного завершения) ---

    private int readIntegerInput() {
        while (true) {
            try {
                return Integer.parseInt(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.print("Ошибка: Требуется ввести целое число! Повторите: ");
            }
        }
    }

    private LocalDateTime readDateTimeInput() {
        while (true) {
            try {
                String input = scanner.nextLine().trim();
                return LocalDateTime.parse(input, FORMATTER);
            } catch (DateTimeParseException e) {
                System.out.print("Ошибка: Формат даты нарушен! Шаблон: ГГГГ-ММ-ДД ХХ:ММ (Пример: 2026-06-15 14:00). Повторите: ");
            }
        }
    }
}
