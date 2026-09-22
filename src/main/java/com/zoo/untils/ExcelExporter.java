package com.zoo.untils;

import com.zoo.model.Booking;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

public class ExcelExporter {
    
    public static void exportBookings(List<Booking> bookings, String filePath) throws IOException {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Бронирования Зоопарка");

            // Настройка стилей для шапки таблицы
            CellStyle headerStyle = workbook.createCellStyle();
            Font font = workbook.createFont();
            font.setBold(true);
            headerStyle.setFont(font);
            headerStyle.setFillForegroundColor(IndexedColors.LIGHT_CORNFLOWER_BLUE.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

            // Создание строки заголовков
            Row headerRow = sheet.createRow(0);
            String[] columns = {"ID Бронирования", "ID Посетителя", "Дата и время визита", "Стоимость билета", "Статус"};
            for (int i = 0; i < columns.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(columns[i]);
                cell.setCellStyle(headerStyle);
            }

            // Заполнение строк данными
            int rowIdx = 1;
            for (Booking booking : bookings) {
                Row row = sheet.createRow(rowIdx++);
                row.createCell(0).setCellValue(booking.getId());
                row.createCell(1).setCellValue(booking.getVisitorId());
                row.createCell(2).setCellValue(booking.getVisitDate().toString());
                row.createCell(3).setCellValue(booking.getPrice());
                row.createCell(4).setCellValue(booking.getStatus().name());
            }

            // Автоподбор ширины столбцов
            for (int i = 0; i < columns.length; i++) {
                sheet.autoSizeColumn(i);
            }

            // Запись файла на диск
            try (FileOutputStream fileOut = new FileOutputStream(filePath)) {
                workbook.write(fileOut);
            }
        }
    }
}
