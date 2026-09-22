package com.zoo.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Booking {
    private Integer id;
    private Integer visitorId;
    private LocalDateTime visitDate; 
    private Integer ticketCount = 1; // Защита: по умолчанию 1 билет, чтобы избежать null
    private BookingStatus status;
    private BigDecimal price; 

    public Booking() {}

    // Конструктор без цены (если стоимость рассчитывается позже)
    public Booking(Integer visitorId, LocalDateTime visitDate, Integer ticketCount, BookingStatus status) {
        this.visitorId = visitorId;
        this.visitDate = visitDate;
        setTicketCount(ticketCount);
        this.status = status;
        this.price = BigDecimal.ZERO;
    }

    // Конструктор со всеми полями без ID
    public Booking(Integer visitorId, LocalDateTime visitDate, Integer ticketCount, BookingStatus status, BigDecimal price) {
        this.visitorId = visitorId;
        this.visitDate = visitDate;
        setTicketCount(ticketCount);
        this.status = status;
        this.price = price;
    }

    // Полный конструктор с ID
    public Booking(Integer id, Integer visitorId, LocalDateTime visitDate, Integer ticketCount, BookingStatus status, BigDecimal price) {
        this.id = id;
        this.visitorId = visitorId;
        this.visitDate = visitDate;
        setTicketCount(ticketCount);
        this.status = status;
        this.price = price;
    }

    // Геттеры и Сеттеры
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public Integer getVisitorId() { return visitorId; }
    public void setVisitorId(Integer visitorId) { this.visitorId = visitorId; }

    public LocalDateTime getVisitDate() { return visitDate; }
    public void setVisitDate(LocalDateTime visitDate) { this.visitDate = visitDate; }

    public Integer getTicketCount() { 
        return ticketCount != null ? ticketCount : 1; 
    }
    public void setTicketCount(Integer ticketCount) { 
        this.ticketCount = (ticketCount != null) ? ticketCount : 1; 
    }

    public BookingStatus getStatus() { return status; }
    public void setStatus(BookingStatus status) { this.status = status; }

    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }
    
    public void setPrice(int price) {
        this.price = BigDecimal.valueOf(price);
    }
}
