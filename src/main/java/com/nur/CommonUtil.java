package com.nur;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;

import java.util.Date;

@Slf4j
@UtilityClass
public class CommonUtil {

    // Method to check if a value is empty or null
    public static boolean isEmpty(Object value) {
        return value == null || value.toString().trim().isEmpty();
    }

    // Method to check multiple conditions in a single call
    public static boolean isAnyTrue(boolean... args) {
        for (boolean arg : args) {
            if (arg) return true;
        }
        return false;
    }

    // Method to get string value from a cell with type validation
    public static String getStringCellValue(Row row, int colIndex, String[] headerNames, String[] headerTypes) {
        validateHeader(colIndex, headerNames, headerTypes, "String");
        Cell cell = row.getCell(colIndex);
        return cell != null ? cell.getStringCellValue() : null;
    }

    // Method to get numeric value from a cell with type validation
    public static Integer getNumericCellValue(Row row, int colIndex, String[] headerNames, String[] headerTypes) {
        validateHeader(colIndex, headerNames, headerTypes, "Numeric");
        Cell cell = row.getCell(colIndex);
        return cell != null ? (int) cell.getNumericCellValue() : null;
    }

    // Method to get date value from a cell with type validation
    public static Date getDateCellValue(Row row, int colIndex, String[] headerNames, String[] headerTypes) {
        validateHeader(colIndex, headerNames, headerTypes, "Date");
        Cell cell = row.getCell(colIndex);
        return cell != null ? cell.getDateCellValue() : null;
    }

    // Header validation based on column index and expected type
    private static void validateHeader(int colIndex, String[] headerNames, String[] headerTypes, String expectedType) {
        if (!headerTypes[colIndex].equalsIgnoreCase(expectedType)) {
            throw new IllegalArgumentException("Column " + headerNames[colIndex] + " is expected to be of type " + expectedType);
        }
    }

}
