package com.zoo.model;

import java.time.LocalDateTime;

public class Booking {
    private int id;
    private int visitorId;
    private LocalDateTime visitDate;
    private int price; // Тип изменен на int для синхронизации со слоем репозиториев
    private BookingStatus status;

    // Обязательный пустой конструктор по умолчанию
    public Booking() {}

    public Booking(int id, int visitorId, LocalDateTime visitDate, int price, BookingStatus status) {
        this.id = id;
        this.visitorId = visitorId;
        this.visitDate = visitDate;
        this.price = price;
        this.status = status;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getVisitorId() { return visitorId; }
    public void setVisitorId(int visitorId) { this.visitorId = visitorId; }

    public LocalDateTime getVisitDate() { return visitDate; }
    public void setVisitDate(LocalDateTime visitDate) { this.visitDate = visitDate; }

    public int getPrice() { return price; }
    public void setPrice(int price) { this.price = price; }

    public BookingStatus getStatus() { return status; }
    public void setStatus(BookingStatus status) { this.status = status; }
}
