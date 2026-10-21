package com.zoo.untils;

import com.zoo.model.Booking;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

public class ExcelExporter {

    /**
     * Экспорт списка броней в Excel.
     *
     * @param bookings список броней (можно взять из таблицы или из БД)
     * @param filePath путь к файлу .xlsx
     */
    public static void exportBookings(List<Booking> bookings, String filePath) throws IOException {
        try (Workbook workbook = new XSSFWorkbook();
             FileOutputStream out = new FileOutputStream(filePath)) {

            Sheet sheet = workbook.createSheet("Бронирования");

            // Заголовок
            String[] headers = {
                    "ID", "ID посетителя", "Дата посещения",
                    "Билетов", "Сумма", "Статус"
            };

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

            // Данные
            int rowIdx = 1;
            for (Booking b : bookings) {
                Row row = sheet.createRow(rowIdx++);

                row.createCell(0).setCellValue(b.getId() != null ? b.getId() : 0);
                row.createCell(1).setCellValue(b.getVisitorId() != null ? b.getVisitorId() : 0);
                row.createCell(2).setCellValue(
                        b.getVisitDate() != null ? b.getVisitDate().toString() : "");
                row.createCell(3).setCellValue(b.getTicketCount() != null ? b.getTicketCount() : 0);
                row.createCell(4).setCellValue(
                        b.getPrice() != null ? b.getPrice().doubleValue() : 0.0);
                row.createCell(5).setCellValue(
                        b.getStatus() != null ? b.getStatus().name() : "");
            }

            // Автоширина колонок
            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }

            workbook.write(out);
        }
    }
}