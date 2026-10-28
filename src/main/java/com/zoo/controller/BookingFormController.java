package com.zoo.controller;

import com.zoo.model.Booking;
import com.zoo.model.BookingStatus;
import com.zoo.model.Visitor;
import com.zoo.repository.BookingRepository;
import com.zoo.repository.VisitorRepository;
import com.zoo.repository.impl.BookingRepositoryImpl;
import com.zoo.repository.impl.VisitorRepositoryImpl;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class BookingFormController {

    @FXML private ComboBox<Visitor> visitorCombo;
    @FXML private DatePicker datePicker;
    @FXML private ComboBox<String> timeCombo;
    @FXML private TextField ticketsField;
    @FXML private TextField priceField;
    @FXML private ComboBox<BookingStatus> statusCombo;

    private final BookingRepository bookingRepo = new BookingRepositoryImpl();
    private final VisitorRepository visitorRepo = new VisitorRepositoryImpl();
    private Booking booking;

    @FXML
    public void initialize() {
        statusCombo.setItems(FXCollections.observableArrayList(BookingStatus.values()));

        timeCombo.setItems(FXCollections.observableArrayList(
                "09:00", "10:00", "11:00", "12:00", "13:00", "14:00",
                "15:00", "16:00", "17:00", "18:00", "19:00", "20:00"
        ));
        timeCombo.setValue("12:00");

        try {
            visitorCombo.setItems(FXCollections.observableArrayList(visitorRepo.findAll()));
        } catch (Exception e) {
            new Alert(Alert.AlertType.ERROR, "Не удалось загрузить посетителей: " + e.getMessage()).showAndWait();
        }
    }

    public void setBooking(Booking b) {
        this.booking = b;
        if (b != null) {
            for (Visitor v : visitorCombo.getItems()) {
                if (v.getId() == b.getVisitorId()) {
                    visitorCombo.setValue(v);
                    break;
                }
            }
            datePicker.setValue(b.getVisitDate().toLocalDate());
            timeCombo.setValue(String.format("%02d:%02d", b.getVisitDate().getHour(), b.getVisitDate().getMinute()));
            ticketsField.setText(String.valueOf(b.getTicketCount()));
            priceField.setText(String.valueOf(b.getPrice()));
            statusCombo.setValue(b.getStatus());
        } else {
            datePicker.setValue(LocalDate.now().plusDays(1));
            timeCombo.setValue("12:00");
            statusCombo.setValue(BookingStatus.CREATED);
            ticketsField.setText("1");
            priceField.setText("0");
        }
    }

    @FXML
    private void onSave() {
        try {
            Visitor v = visitorCombo.getValue();
            if (v == null) throw new Exception("Выберите посетителя");

            LocalDate d = datePicker.getValue();
            if (d == null) throw new Exception("Выберите дату");

            String timeStr = timeCombo.getValue();
            if (timeStr == null) throw new Exception("Выберите время");

            String[] parts = timeStr.split(":");
            LocalTime time = LocalTime.of(Integer.parseInt(parts[0]), Integer.parseInt(parts[1]));
            LocalDateTime visit = LocalDateTime.of(d, time);

            int tickets = Integer.parseInt(ticketsField.getText().trim());
            if (tickets < 1 || tickets > 20) throw new Exception("Билетов должно быть 1..20");

            String priceText = priceField.getText().trim().replace(",", ".");
            if (priceText.isEmpty()) throw new Exception("Введите цену");
            BigDecimal price = new BigDecimal(priceText);
            if (price.compareTo(BigDecimal.ZERO) < 0) throw new Exception("Цена не может быть отрицательной");

            BookingStatus status = statusCombo.getValue();
            if (status == null) throw new Exception("Выберите статус");

            if (booking == null) {
                Booking b = new Booking(null, v.getId(), visit, tickets, status, price);
                bookingRepo.save(b);
                com.zoo.service.EmailService.sendBookingConfirmation(v.getEmail(), v.getFullName(), visit.toString(), tickets, price.toString());
            } else {
                booking.setVisitorId(v.getId());
                booking.setVisitDate(visit);
                booking.setTicketCount(tickets);
                booking.setStatus(status);
                booking.setPrice(price);
                bookingRepo.update(booking);
            }

            ((Stage) visitorCombo.getScene().getWindow()).close();
        } catch (Exception e) {
            new Alert(Alert.AlertType.ERROR, e.getMessage()).showAndWait();
        }
    }

    @FXML
    private void onCancel() {
        ((Stage) visitorCombo.getScene().getWindow()).close();
    }
}