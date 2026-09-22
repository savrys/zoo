package com.zoo;

import com.zoo.repository.BookingRepository;
import com.zoo.repository.VisitorRepository;
import com.zoo.repository.impl.BookingRepositoryImpl;
import com.zoo.repository.impl.VisitorRepositoryImpl;
import com.zoo.service.ZooService;
import com.zoo.service.impl.ZooServiceImpl;
import com.zoo.ui.ConsoleMenu;

public class Main {
    public static void main(String[] args) {
        // Инициализация слоев (Многослойная архитектура)
        VisitorRepository visitorRepository = new VisitorRepositoryImpl();
        BookingRepository bookingRepository = new BookingRepositoryImpl();
        
        ZooService zooService = new ZooServiceImpl(bookingRepository, visitorRepository);
        
        // Запуск интерфейса
        ConsoleMenu menu = new ConsoleMenu(zooService);
        menu.start();
    }
}