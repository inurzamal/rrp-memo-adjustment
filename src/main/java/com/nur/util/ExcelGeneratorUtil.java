package com.nur.util;

import com.nur.dto.BaseDTO;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.util.Date;
import java.util.List;

@Slf4j
@UtilityClass
public class ExcelGeneratorUtil {

    public static ByteArrayInputStream exportToExcel(
            String sheetName, String[] headerNames,
            List<? extends BaseDTO> dtos, String[] properties, int maxColumnWidth) {

        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet(sheetName);
            createHeaderRow(sheet, headerNames);
            populateDataRows(sheet, dtos, properties);

            // Adjust column widths
            for (int i = 0; i < headerNames.length; i++) {
                sheet.autoSizeColumn(i);
                if (sheet.getColumnWidth(i) > maxColumnWidth * 256) {
                    sheet.setColumnWidth(i, maxColumnWidth * 256); // Limit column width
                }
            }

            workbook.write(out);
            return new ByteArrayInputStream(out.toByteArray());
        } catch (IOException e) {
            log.error("Error occurred while exporting to Excel: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to export data to Excel.");
        }
    }

    private static void createHeaderRow(Sheet sheet, String[] headerNames) {
        Row headerRow = sheet.createRow(0);
        CellStyle headerStyle = sheet.getWorkbook().createCellStyle();
        Font headerFont = sheet.getWorkbook().createFont();
        headerFont.setBold(true);
        headerStyle.setFont(headerFont);

        for (int i = 0; i < headerNames.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headerNames[i]);
            cell.setCellStyle(headerStyle);
        }
    }

    private static void populateDataRows(Sheet sheet, List<? extends BaseDTO> dtos, String[] fieldNames) {
        int rowNum = 1;
        for (BaseDTO dto : dtos) {
            Row row = sheet.createRow(rowNum++);
            populateRowWithData(row, dto, fieldNames);
        }
    }

    private static void populateRowWithData(Row row, BaseDTO dto, String[] fieldNames) {
        try {
            // Define a date formatter for LocalDateTime, LocalDate, etc.
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

            for (int i = 0; i < fieldNames.length; i++) {
                Field field = dto.getClass().getDeclaredField(fieldNames[i]);
                field.setAccessible(true);
                Object value = field.get(dto);

                Cell cell = row.createCell(i);

                // Handling different data types
                if (value instanceof Number) {
                    cell.setCellValue(((Number) value).doubleValue());
                } else if (value instanceof Boolean) {
                    cell.setCellValue((Boolean) value);
                } else if (value instanceof Date) {
                    // Handling Date
                    cell.setCellValue((Date) value);
                    CellStyle dateStyle = row.getSheet().getWorkbook().createCellStyle();
                    CreationHelper createHelper = row.getSheet().getWorkbook().getCreationHelper();
                    dateStyle.setDataFormat(createHelper.createDataFormat().getFormat("yyyy-MM-dd HH:mm:ss"));
                    cell.setCellStyle(dateStyle);
                } else if (value instanceof TemporalAccessor) {
                    // Handling LocalDateTime, LocalDate, etc.
                    cell.setCellValue(dateFormatter.format((TemporalAccessor) value));
                } else if (value != null) {
                    // Handling other object types as strings
                    cell.setCellValue(value.toString());
                } else {
                    cell.setCellValue(""); // Handle nulls gracefully
                }
            }
        } catch (NoSuchFieldException | IllegalAccessException e) {
            log.error("Error accessing field value: {}", e.getMessage(), e);
        }
    }
}
