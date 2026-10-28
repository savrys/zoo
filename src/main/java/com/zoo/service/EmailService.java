package com.zoo.service;

import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import javafx.scene.control.Alert;

import java.util.Properties;

public class EmailService {

    private static final String SENDER_EMAIL = "8marlboro@inbox.ru";
    private static final String SENDER_PASSWORD = "MvSG8XyrZaihT0IPFA29";

    private static void sendEmail(String toEmail, String subject, String text) {
        System.out.println(">>> [EmailService] Отправка письма на " + toEmail);
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.ssl.enable", "true");
        props.put("mail.smtp.host", "smtp.mail.ru");
        props.put("mail.smtp.port", "465");

        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(SENDER_EMAIL, SENDER_PASSWORD);
            }
        });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(SENDER_EMAIL));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
            message.setSubject(subject);
            message.setText(text);
            Transport.send(message);
            System.out.println(">>> [EmailService] ПИСЬМО УСПЕШНО ОТПРАВЛЕНО!");
        } catch (MessagingException e) {
            System.out.println(">>> [EmailService] ОШИБКА: " + e.getMessage());
            javafx.application.Platform.runLater(() -> {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Ошибка почты");
                alert.setHeaderText("Не удалось отправить письмо");
                alert.setContentText(e.getMessage());
                alert.showAndWait();
            });
        }
    }

    // 1. Для РЕГИСТРАЦИИ
    public static void sendRegistrationConfirmation(String toEmail, String visitorName) {
        String subject = "Регистрация в системе Зоопарка";
        String text = "Здравствуйте, " + visitorName + "!\n\n" +
                "Вы успешно зарегистрированы в системе бронирования Зоопарка.\n" +
                "Теперь вы можете бронировать билеты онлайн.\n\n" +
                "Ждем вас в Зоопарке!";
        sendEmail(toEmail, subject, text);
    }

    // 2. Для БРОНИРОВАНИЯ (добавлена цена)
    public static void sendBookingConfirmation(String toEmail, String visitorName, String date, int tickets, String price) {
        String subject = "Подтверждение бронирования в Зоопарке";
        String text = "Здравствуйте, " + visitorName + "!\n\n" +
                "Ваша бронь успешно создана.\n" +
                "Дата и время посещения: " + date + "\n" +
                "Количество билетов: " + tickets + "\n" +
                "Стоимость: " + price + " руб.\n\n" +
                "Ждем вас в Зоопарке!";
        sendEmail(toEmail, subject, text);
    }
}