package com.zoo.model;

import java.time.LocalDateTime;

public class Booking {
    private int id;
    private int visitorId;
    private String visitorName;
    private LocalDateTime visitDate;
    private int ticketCount;
    private BookingStatus status;
    private LocalDateTime createdAt;

    public Booking(int id, int visitorId, String visitorName, LocalDateTime visitDate, int ticketCount, BookingStatus status, LocalDateTime createdAt) {
        this.id = id;
        this.visitorId = visitorId;
        this.visitorName = visitorName;
        this.visitDate = visitDate;
        this.ticketCount = ticketCount;
        this.status = status;
        this.createdAt = createdAt;
    }

    public Booking(int visitorId, LocalDateTime visitDate, int ticketCount, BookingStatus status) {
        this.visitorId = visitorId;
        this.visitDate = visitDate;
        this.ticketCount = ticketCount;
        this.status = status;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getVisitorId() { return visitorId; }
    public void setVisitorId(int visitorId) { this.visitorId = visitorId; }
    public String getVisitorName() { return visitorName; }
    public void setVisitorName(String visitorName) { this.visitorName = visitorName; }
    public LocalDateTime getVisitDate() { return visitDate; }
    public void setVisitDate(LocalDateTime visitDate) { this.visitDate = visitDate; }
    public int getTicketCount() { return ticketCount; }
    public void setTicketCount(int ticketCount) { this.ticketCount = ticketCount; }
    public BookingStatus getStatus() { return status; }
    public void setStatus(BookingStatus status) { this.status = status; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    @Override
    public String toString() {
        return String.format("ID: %d | Посетитель: %s | Дата: %s | Билетов: %d | Статус: %s",
                id, visitorName, visitDate, ticketCount, status);
    }
}