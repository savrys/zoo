package com.zoo.untils;

import com.zoo.model.Booking;
import com.zoo.model.Visitor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

public class ExcelExporter {

    // ЭКСПОРТ БРОНЕЙ
    public static void exportBookings(List<Booking> bookings, String filePath) throws IOException {
        try (Workbook workbook = new XSSFWorkbook();
             FileOutputStream out = new FileOutputStream(filePath)) {

            Sheet sheet = workbook.createSheet("Бронирования");
            String[] headers = {"ID", "ID Посетителя", "Дата и время", "Билетов", "Сумма", "Статус"};

            Row headerRow = sheet.createRow(0);
            CellStyle headerStyle = workbook.createCellStyle();
            Font font = workbook.createFont();
            font.setBold(true);
            headerStyle.setFont(font);

            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }

            int rowIdx = 1;
            for (Booking b : bookings) {
                Row row = sheet.createRow(rowIdx++);
                row.createCell(0).setCellValue(b.getId() != null ? b.getId() : 0);
                row.createCell(1).setCellValue(b.getVisitorId() != null ? b.getVisitorId() : 0);
                row.createCell(2).setCellValue(b.getVisitDate() != null ? b.getVisitDate().toString() : "");
                row.createCell(3).setCellValue(b.getTicketCount() != null ? b.getTicketCount() : 0);
                row.createCell(4).setCellValue(b.getPrice() != null ? b.getPrice().doubleValue() : 0.0);
                row.createCell(5).setCellValue(b.getStatus() != null ? b.getStatus().name() : "");
            }

            for (int i = 0; i < headers.length; i++) sheet.autoSizeColumn(i);
            workbook.write(out);
        }
    }

    // ЭКСПОРТ ПОСЕТИТЕЛЕЙ
    public static void exportVisitors(List<Visitor> visitors, String filePath) throws IOException {
        try (Workbook workbook = new XSSFWorkbook();
             FileOutputStream out = new FileOutputStream(filePath)) {

            Sheet sheet = workbook.createSheet("Посетители");
            String[] headers = {"ID", "ФИО", "Email", "Телефон"};

            Row headerRow = sheet.createRow(0);
            CellStyle headerStyle = workbook.createCellStyle();
            Font font = workbook.createFont();
            font.setBold(true);
            headerStyle.setFont(font);

            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }

            int rowIdx = 1;
            for (Visitor v : visitors) {
                Row row = sheet.createRow(rowIdx++);
                row.createCell(0).setCellValue(v.getId());
                row.createCell(1).setCellValue(v.getFullName() != null ? v.getFullName() : "");
                row.createCell(2).setCellValue(v.getEmail() != null ? v.getEmail() : "");
                row.createCell(3).setCellValue(v.getPhone() != null ? v.getPhone() : "");
            }

            for (int i = 0; i < headers.length; i++) sheet.autoSizeColumn(i);
            workbook.write(out);
        }
    }
}