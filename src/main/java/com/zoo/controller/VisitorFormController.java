package com.zoo.controller;

import com.zoo.model.Visitor;
import com.zoo.repository.VisitorRepository;
import com.zoo.repository.impl.VisitorRepositoryImpl;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class VisitorFormController {

    @FXML private TextField nameField;
    @FXML private TextField phoneField;
    @FXML private TextField ageField;   // оставим для совместимости с FXML, но Visitor.age нет

    private final VisitorRepository repo = new VisitorRepositoryImpl();
    private Visitor visitor;

    public void setVisitor(Visitor v) {
        this.visitor = v;
        if (v != null) {
            nameField.setText(v.getFullName());
            phoneField.setText(v.getPhone());
        }
    }

    @FXML
    private void onSave() {
        try {
            Visitor v = visitor == null ? new Visitor() : visitor;
            v.setFullName(nameField.getText());
            v.setPhone(phoneField.getText());

            if (v.getFullName() == null || v.getFullName().isBlank())
                throw new Exception("Введите ФИО");
            if (v.getPhone() == null || v.getPhone().isBlank())
                throw new Exception("Введите телефон");

            if (visitor == null) repo.save(v); else repo.update(v);

            ((Stage) nameField.getScene().getWindow()).close();
        } catch (Exception e) {
            new Alert(Alert.AlertType.ERROR, e.getMessage()).showAndWait();
        }
    }

    @FXML
    private void onCancel() {
        ((Stage) nameField.getScene().getWindow()).close();
    }
}