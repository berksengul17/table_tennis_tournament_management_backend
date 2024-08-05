package com.berk.table_tennis_tournament_management_backend;

import com.berk.table_tennis_tournament_management_backend.participant.Participant;
import com.berk.table_tennis_tournament_management_backend.participant_age_category.ParticipantAgeCategory;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Iterator;
import java.util.Locale;

public class ExcelHelper {

    private static final String path = "example_participants.xlsx";

    public static void editRow(ParticipantAgeCategory participantAgeCategory,
                               boolean createNewRow) {
        try (InputStream stream = new FileInputStream(path)) {
            if (stream == null) {
                throw new FileNotFoundException("Resource not found: " + path);
            }

            Participant participant = participantAgeCategory.getParticipant();

            Workbook workbook = new XSSFWorkbook(stream);
            Sheet sheet = workbook.getSheetAt(0);

            int numOfRows = getNumberOfRows(sheet);
            Row row = null;
            if (createNewRow) {
                row = sheet.createRow(numOfRows);
            } else {
                row = getRowByParticipantId(sheet, participant.getId());
            }

            if (row == null) {
                throw new IllegalArgumentException("No row found");
            }

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("M/d/yyyy h:mm:ss");
            Cell time = row.createCell(0);
            time.setCellValue(formatter.format(LocalDateTime.now()));

            Cell name = row.createCell(1);
            name.setCellValue(participant.getFirstName() + " " + participant.getLastName());

            Cell email = row.createCell(2);
            email.setCellValue(participant.getEmail());

            Cell birthDate = row.createCell(3);
            birthDate.setCellValue(participant.getBirthDate().toString());

            Cell gender = row.createCell(4);
            gender.setCellValue(participant.getGender().label);

            Cell city = row.createCell(5);
            city.setCellValue(participant.getCity());

            Cell phone = row.createCell(10);
            phone.setCellValue(participant.getPhoneNumber());

            Cell id = row.createCell(11);
            id.setCellValue(participant.getId());

            Cell age = row.createCell(12);
            if (participantAgeCategory.getAgeCategory() != null) {
                String ageStr = participantAgeCategory.getAgeCategory().getAge().age;
                age.setCellValue(ageStr != null ? ageStr : "");
            }

            Cell rating = row.createCell(13);
            rating.setCellValue(participant.getRating());

            File file = new File(path);

            try (OutputStream output = new FileOutputStream(file)) {
                workbook.write(output);
            }

            workbook.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void deleteRow(Long participantId) {
        try (InputStream stream = new FileInputStream(path)) {
            Workbook workbook = new XSSFWorkbook(stream);
            Sheet sheet = workbook.getSheetAt(0);
            Iterator<Row> rowIterator = sheet.iterator();

            Row row = null;
            int rowCount = 0;
            if (rowIterator.hasNext()) rowIterator.next();
            while (rowIterator.hasNext()) {
                row = rowIterator.next();
                Cell cell = row.getCell(11);
                if (cell != null) {
                    long id = (long) cell.getNumericCellValue();
                    if (participantId == id) {
                        break;
                    }
                }
                rowCount++;
            }

            int numberOfRows = getNumberOfRows(sheet);
            if (rowCount + 1 == numberOfRows) {
                sheet.removeRow(sheet.getRow(rowCount));
            } else {
                sheet.shiftRows(rowCount + 1, numberOfRows - 1, -1);
            }

            try(OutputStream output = new FileOutputStream(path)) {
                workbook.write(output);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static int getNumberOfRows(Sheet sheet) {
        Iterator<Row> rowIterator = sheet.iterator();
        int rowCount = 0;
        while (rowIterator.hasNext()) {
            Row row = rowIterator.next();
            Cell time = row.getCell(0);
            if (time == null) break;
            rowCount++;
        }

        return rowCount;
    }

    private static Row getRowByParticipantId(Sheet sheet, Long participantId) {
        Iterator<Row> iterator = sheet.iterator();

        // skip column names
        if (iterator.hasNext()) iterator.next();
        while (iterator.hasNext()) {
            Row row = iterator.next();
            Cell id = row.getCell(11);
            if (participantId == (int) id.getNumericCellValue()) {
                return row;
            }
        }

        return null;
    }
}
