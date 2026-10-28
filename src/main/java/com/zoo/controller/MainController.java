package com.zoo.controller;

import com.zoo.model.Booking;
import com.zoo.model.BookingStatus;
import com.zoo.model.Visitor;
import com.zoo.repository.BookingRepository;
import com.zoo.repository.VisitorRepository;
import com.zoo.repository.impl.BookingRepositoryImpl;
import com.zoo.repository.impl.VisitorRepositoryImpl;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainController {

    // ====== ВКЛАДКА 1: БРОНИ ======
    @FXML private TableView<Booking> table;
    @FXML private TableColumn<Booking, Integer> colId;
    @FXML private TableColumn<Booking, String> colVisitor;
    @FXML private TableColumn<Booking, LocalDateTime> colDate;
    @FXML private TableColumn<Booking, Integer> colTickets;
    @FXML private TableColumn<Booking, BigDecimal> colPrice; // 👈 КОЛОНКА СУММЫ
    @FXML private TableColumn<Booking, BookingStatus> colStatus;

    @FXML private TextField searchField;
    @FXML private ComboBox<BookingStatus> statusFilter;
    @FXML private ComboBox<String> sortCombo;
    @FXML private Label statsLabel;

    // ====== ВКЛАДКА 2: ПОСЕТИТЕЛИ ======
    @FXML private TableView<Visitor> visitorTable;
    @FXML private TableColumn<Visitor, Integer> vColId;
    @FXML private TableColumn<Visitor, String> vColName;
    @FXML private TableColumn<Visitor, String> vColEmail;
    @FXML private TableColumn<Visitor, String> vColPhone;

    private final BookingRepository bookingRepo = new BookingRepositoryImpl();
    private final VisitorRepository visitorRepo = new VisitorRepositoryImpl();

    private final ObservableList<Booking> data = FXCollections.observableArrayList();
    private final ObservableList<Visitor> visitorData = FXCollections.observableArrayList();
    private final Map<Integer, String> visitorNames = new HashMap<>();

    @FXML
    public void initialize() {
        // Настройка колонок броней
        colId.setCellValueFactory(cd -> new SimpleIntegerProperty(cd.getValue().getId()).asObject());
        colVisitor.setCellValueFactory(cd -> new SimpleStringProperty(
                visitorNames.getOrDefault(cd.getValue().getVisitorId(), "—")));
        colDate.setCellValueFactory(cd -> new SimpleObjectProperty<>(cd.getValue().getVisitDate()));
        colTickets.setCellValueFactory(cd -> new SimpleIntegerProperty(cd.getValue().getTicketCount()).asObject());

        // 👇 ПРИВЯЗКА СУММЫ
        colPrice.setCellValueFactory(cd -> new SimpleObjectProperty<>(cd.getValue().getPrice()));

        colStatus.setCellValueFactory(cd -> new SimpleObjectProperty<>(cd.getValue().getStatus()));

        statusFilter.setItems(FXCollections.observableArrayList(BookingStatus.values()));
        sortCombo.setItems(FXCollections.observableArrayList(
                "ID", "Дата ↑", "Дата ↓", "Сумма ↑", "Сумма ↓", "Билетов ↑", "Билетов ↓"));

        table.setItems(data);

        // Настройка колонок посетителей
        vColId.setCellValueFactory(cd -> new SimpleIntegerProperty(cd.getValue().getId()).asObject());
        vColName.setCellValueFactory(cd -> new SimpleStringProperty(cd.getValue().getFullName()));
        vColEmail.setCellValueFactory(cd -> new SimpleStringProperty(cd.getValue().getEmail()));
        vColPhone.setCellValueFactory(cd -> new SimpleStringProperty(cd.getValue().getPhone()));

        visitorTable.setItems(visitorData);

        refresh();
        refreshVisitors();
    }

    // ================== БРОНИ ==================

    private void loadVisitorNames() {
        visitorNames.clear();
        try {
            for (Visitor v : visitorRepo.findAll()) {
                visitorNames.put(v.getId(), v.getFullName());
            }
        } catch (Exception ignored) {}
    }

    private void refresh() {
        try {
            loadVisitorNames();
            List<Booking> list = bookingRepo.findAll();

            // Поиск
            String q = searchField.getText();
            if (q != null && !q.isBlank()) {
                String lower = q.trim().toLowerCase();
                List<Booking> filtered = new ArrayList<>();
                for (Booking b : list) {
                    String name = visitorNames.getOrDefault(b.getVisitorId(), "").toLowerCase();
                    if (name.contains(lower)) filtered.add(b);
                }
                list = filtered;
            }

            // Фильтр
            BookingStatus s = statusFilter.getValue();
            if (s != null) {
                List<Booking> filtered = new ArrayList<>();
                for (Booking b : list) if (b.getStatus() == s) filtered.add(b);
                list = filtered;
            }

            // Сортировка
            String sort = sortCombo.getValue();
            if (sort != null) {
                List<Booking> sortable = new ArrayList<>(list);
                switch (sort) {
                    case "Дата ↑"    -> sortable.sort((a, b) -> a.getVisitDate().compareTo(b.getVisitDate()));
                    case "Дата ↓"    -> sortable.sort((a, b) -> b.getVisitDate().compareTo(a.getVisitDate()));
                    case "Сумма ↑"   -> sortable.sort((a, b) -> safePrice(a).compareTo(safePrice(b)));
                    case "Сумма ↓"   -> sortable.sort((a, b) -> safePrice(b).compareTo(safePrice(a)));
                    case "Билетов ↑" -> sortable.sort((a, b) -> Integer.compare(a.getTicketCount(), b.getTicketCount()));
                    case "Билетов ↓" -> sortable.sort((a, b) -> Integer.compare(b.getTicketCount(), a.getTicketCount()));
                    default          -> sortable.sort((a, b) -> Integer.compare(a.getId(), b.getId()));
                }
                list = sortable;
            }

            data.setAll(list);
            updateStats();
        } catch (Exception e) {
            System.err.println("Ошибка загрузки броней: " + e.getMessage());
        }
    }

    private BigDecimal safePrice(Booking b) {
        return b.getPrice() != null ? b.getPrice() : BigDecimal.ZERO;
    }

    private void updateStats() {
        try {
            int total = bookingRepo.countAll();
            int created = bookingRepo.countByStatus(BookingStatus.CREATED);
            int confirmed = bookingRepo.countByStatus(BookingStatus.CONFIRMED);
            int completed = bookingRepo.countByStatus(BookingStatus.COMPLETED);
            int cancelled = bookingRepo.countByStatus(BookingStatus.CANCELLED);
            int visitors = visitorRepo.findAll().size();

            statsLabel.setText(String.format(
                    "Всего: %d | CREATED: %d | CONFIRMED: %d | COMPLETED: %d | CANCELLED: %d | Посетителей: %d",
                    total, created, confirmed, completed, cancelled, visitors));
        } catch (Exception e) {
            statsLabel.setText("Ошибка статистики: " + e.getMessage());
        }
    }

    @FXML private void onRefresh() {
        searchField.clear();
        statusFilter.setValue(null);
        sortCombo.setValue(null);
        refresh();
    }
    @FXML private void onSearch() { refresh(); }

    @FXML private void onAdd()   { openBookingForm(null); }
    @FXML private void onEdit()  {
        Booking sel = table.getSelectionModel().getSelectedItem();
        if (sel == null) { showError("Выберите запись"); return; }
        openBookingForm(sel);
    }

    private void openBookingForm(Booking booking) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/booking-form.fxml"));
            Parent root = loader.load();
            BookingFormController c = loader.getController();
            c.setBooking(booking);
            Stage stage = new Stage();
            stage.setTitle(booking == null ? "Новое бронирование" : "Редактирование брони");
            stage.initOwner(table.getScene().getWindow());
            stage.setScene(new Scene(root));
            stage.showAndWait();
            refresh();
        } catch (Exception e) {
            showError("Ошибка открытия формы: " + e.getMessage());
        }
    }

    @FXML
    private void onDelete() {
        Booking sel = table.getSelectionModel().getSelectedItem();
        if (sel == null) { showError("Выберите запись"); return; }
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                "Удалить бронь #" + sel.getId() + "?",
                ButtonType.OK, ButtonType.CANCEL);
        confirm.showAndWait().ifPresent(bt -> {
            if (bt == ButtonType.OK) {
                try {
                    bookingRepo.delete(sel.getId());
                    refresh();
                } catch (Exception e) {
                    showError("Ошибка удаления: " + e.getMessage());
                }
            }
        });
    }

    @FXML
    private void onExport() {
        FileChooser fc = new FileChooser();
        fc.setTitle("Экспорт броней");
        fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("Excel", "*.xlsx"));
        fc.setInitialFileName("bookings_report.xlsx");

        File file = fc.showSaveDialog(table.getScene().getWindow());
        if (file == null) return;

        try {
            com.zoo.untils.ExcelExporter.exportBookings(data, file.getAbsolutePath());
            Alert info = new Alert(Alert.AlertType.INFORMATION);
            info.setTitle("Успех");
            info.setHeaderText(null);
            info.setContentText("Экспортировано: " + file.getName());
            info.showAndWait();
        } catch (Exception e) {
            showError("Ошибка экспорта: " + e.getMessage());
        }
    }



    private void refreshVisitors() {
        try {
            visitorData.setAll(visitorRepo.findAll());
        } catch (Exception e) {
            System.err.println("Ошибка загрузки посетителей: " + e.getMessage());
        }
    }

    @FXML private void onRefreshVisitors() { refreshVisitors(); }

    @FXML private void onAddVisitor() { openVisitorForm(null); }

    @FXML private void onEditVisitor() {
        Visitor sel = visitorTable.getSelectionModel().getSelectedItem();
        if (sel == null) { showError("Выберите посетителя"); return; }
        openVisitorForm(sel);
    }

    private void openVisitorForm(Visitor visitor) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/visitor-form.fxml"));
            Parent root = loader.load();
            VisitorFormController c = loader.getController();
            c.setVisitor(visitor);
            Stage stage = new Stage();
            stage.setTitle(visitor == null ? "Новый посетитель" : "Редактирование посетителя");
            stage.initOwner(visitorTable.getScene().getWindow());
            stage.setScene(new Scene(root));
            stage.showAndWait();
            refreshVisitors();
            refresh();
        } catch (Exception e) {
            showError("Ошибка открытия формы: " + e.getMessage());
        }
    }

    @FXML
    private void onDeleteVisitor() {
        Visitor sel = visitorTable.getSelectionModel().getSelectedItem();
        if (sel == null) { showError("Выберите посетителя"); return; }
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                "Удалить посетителя \"" + sel.getFullName() + "\"?",
                ButtonType.OK, ButtonType.CANCEL);
        confirm.showAndWait().ifPresent(bt -> {
            if (bt == ButtonType.OK) {
                try {
                    visitorRepo.delete(sel.getId());
                    refreshVisitors();
                    refresh();
                } catch (Exception e) {
                    showError("Ошибка удаления: " + e.getMessage());
                }
            }
        });
    }

    @FXML
    private void onExportVisitors() {
        FileChooser fc = new FileChooser();
        fc.setTitle("Экспорт посетителей");
        fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("Excel", "*.xlsx"));
        fc.setInitialFileName("visitors_report.xlsx");

        File file = fc.showSaveDialog(visitorTable.getScene().getWindow());
        if (file == null) return;

        try {
            com.zoo.untils.ExcelExporter.exportVisitors(visitorData, file.getAbsolutePath());
            Alert info = new Alert(Alert.AlertType.INFORMATION);
            info.setTitle("Успех");
            info.setHeaderText(null);
            info.setContentText("Экспортировано: " + file.getName());
            info.showAndWait();
        } catch (Exception e) {
            showError("Ошибка экспорта: " + e.getMessage());
        }
    }


    @FXML private void onExit() { System.exit(0); }

    private void showError(String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Ошибка");
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.getDialogPane().setGraphic(null);
        alert.showAndWait();
    }
}