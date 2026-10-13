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

public class BookingFormController {

    @FXML private ComboBox<Visitor> visitorCombo;
    @FXML private DatePicker datePicker;
    @FXML private TextField ticketsField;
    @FXML private ComboBox<BookingStatus> statusCombo;

    private final BookingRepository bookingRepo = new BookingRepositoryImpl();
    private final VisitorRepository visitorRepo = new VisitorRepositoryImpl();
    private Booking booking;

    @FXML
    public void initialize() {
        statusCombo.setItems(FXCollections.observableArrayList(BookingStatus.values()));

        try {
            visitorCombo.setItems(FXCollections.observableArrayList(visitorRepo.findAll()));
        } catch (Exception e) {
            new Alert(Alert.AlertType.ERROR, "Не удалось загрузить посетителей: " + e.getMessage()).showAndWait();
        }
    }

    public void setBooking(Booking b) {
        this.booking = b;
        if (b != null) {
            // Найти Visitor в ComboBox по visitorId
            for (Visitor v : visitorCombo.getItems()) {
                if (v.getId() == b.getVisitorId()) {
                    visitorCombo.setValue(v);
                    break;
                }
            }
            datePicker.setValue(b.getVisitDate().toLocalDate());
            ticketsField.setText(String.valueOf(b.getTicketCount()));
            statusCombo.setValue(b.getStatus());
        } else {
            datePicker.setValue(LocalDate.now().plusDays(1));
            statusCombo.setValue(BookingStatus.CREATED);
            ticketsField.setText("1");
        }
    }

    @FXML
    private void onSave() {
        try {
            Visitor v = visitorCombo.getValue();
            if (v == null) throw new Exception("Выберите посетителя");

            LocalDate d = datePicker.getValue();
            if (d == null) throw new Exception("Выберите дату");

            int tickets;
            try {
                tickets = Integer.parseInt(ticketsField.getText().trim());
            } catch (NumberFormatException e) {
                throw new Exception("Билеты должны быть целым числом");
            }
            if (tickets < 1 || tickets > 20) throw new Exception("Билетов должно быть 1..20");

            BookingStatus status = statusCombo.getValue();
            if (status == null) throw new Exception("Выберите статус");

            LocalDateTime visit = d.atStartOfDay();

            if (booking == null) {
                // Создать новую бронь
                Booking b = new Booking(null, v.getId(), visit, tickets, status, BigDecimal.ZERO);
                bookingRepo.save(b);
            } else {
                booking.setVisitorId(v.getId());
                booking.setVisitDate(visit);
                booking.setTicketCount(tickets);
                booking.setStatus(status);
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