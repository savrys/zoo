package com.zoo.controller;

import com.zoo.model.Visitor;
import com.zoo.repository.VisitorRepository;
import com.zoo.repository.impl.VisitorRepositoryImpl;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.InitialDirContext;
import java.util.Hashtable;
import java.util.regex.Pattern;

public class VisitorFormController {

    @FXML private TextField nameField;
    @FXML private TextField phoneField;
    @FXML private TextField emailField;

    private final VisitorRepository repo = new VisitorRepositoryImpl();
    private Visitor visitor;

    // Проверка формата Email
    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

    // Проверка формата Телефона
    private static final Pattern PHONE_PATTERN =
            Pattern.compile("^\\+?[0-9\\s\\-\\(\\)]{7,20}$");

    public void setVisitor(Visitor v) {
        this.visitor = v;
        if (v != null) {
            nameField.setText(v.getFullName());
            phoneField.setText(v.getPhone());
            emailField.setText(v.getEmail());
        }
    }

    @FXML
    private void onSave() {
        try {
            Visitor v = visitor == null ? new Visitor() : visitor;

            String name = nameField.getText().trim();
            String phone = phoneField.getText().trim();
            String email = emailField.getText().trim();

            // ВАЛИДАЦИЯ
            if (name.isEmpty()) {
                throw new Exception("Введите ФИО");
            }
            if (!PHONE_PATTERN.matcher(phone).matches()) {
                throw new Exception("Некорректный телефон.\nПример: +7 900 123-45-67");
            }
            if (!EMAIL_PATTERN.matcher(email).matches()) {
                throw new Exception("Некорректный Email.\nПример: ivan@mail.ru");
            }
            if (!isDomainExists(email)) {
                throw new Exception("Домен почты не существует.\nПроверьте адрес (например, gmail.com, mail.ru, yandex.ru).");
            }

            // Сохранение в БД
            v.setFullName(name);
            v.setPhone(phone);
            v.setEmail(email);

            if (visitor == null) {
                repo.save(v);
            } else {
                repo.update(v);
            }

            // ВЫЗОВ ОТПРАВКИ ПИСЬМА ПРИ РЕГИСТРАЦИИ
            System.out.println(">>> Вызываю EmailService для регистрации: " + v.getEmail());
            com.zoo.service.EmailService.sendRegistrationConfirmation(
                    v.getEmail(),
                    v.getFullName()
            );

            ((Stage) nameField.getScene().getWindow()).close();
        } catch (Exception e) {
            System.out.println(">>> Ошибка: " + e.getMessage());
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Ошибка ввода");
            alert.setHeaderText(null);
            alert.setContentText(e.getMessage());
            alert.showAndWait();
        }
    }

    @FXML
    private void onCancel() {
        ((Stage) nameField.getScene().getWindow()).close();
    }

    // Проверка MX-записи домена
    private boolean isDomainExists(String email) {
        try {
            String domain = email.substring(email.indexOf("@") + 1);

            Hashtable<String, String> env = new Hashtable<>();
            env.put("java.naming.factory.initial", "com.sun.jndi.dns.DnsContextFactory");
            InitialDirContext ictx = new InitialDirContext(env);

            Attributes attrs = ictx.getAttributes(domain, new String[]{"MX"});
            Attribute attr = attrs.get("MX");

            return attr != null && attr.size() > 0;
        } catch (NamingException e) {
            return false;
        }
    }
}