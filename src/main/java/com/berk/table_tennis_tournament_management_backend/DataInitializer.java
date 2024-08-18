package com.berk.table_tennis_tournament_management_backend;

import com.berk.table_tennis_tournament_management_backend.age_category.AGE;
import com.berk.table_tennis_tournament_management_backend.age_category.AGE_CATEGORY;
import com.berk.table_tennis_tournament_management_backend.age_category.AgeCategory;
import com.berk.table_tennis_tournament_management_backend.age_category.AgeCategoryRepository;
import com.berk.table_tennis_tournament_management_backend.participant.GENDER;
import com.berk.table_tennis_tournament_management_backend.participant.Participant;
import com.berk.table_tennis_tournament_management_backend.participant.ParticipantRepository;
import com.berk.table_tennis_tournament_management_backend.participant_age_category.ParticipantAgeCategory;
import com.berk.table_tennis_tournament_management_backend.participant_age_category.ParticipantAgeCategoryRepository;
import com.berk.table_tennis_tournament_management_backend.rating.Rating;
import com.berk.table_tennis_tournament_management_backend.rating.RatingRepository;
import com.berk.table_tennis_tournament_management_backend.table.Table;
import com.berk.table_tennis_tournament_management_backend.table.TableRepository;
import com.berk.table_tennis_tournament_management_backend.table_time.TableTime;
import com.berk.table_tennis_tournament_management_backend.table_time.TableTimeRepository;
import com.berk.table_tennis_tournament_management_backend.time.Time;
import com.berk.table_tennis_tournament_management_backend.time.TimeRepository;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.parser.PdfTextExtractor;
import lombok.AllArgsConstructor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.io.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Component
@AllArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final ParticipantRepository participantRepository;
    private final AgeCategoryRepository ageCategoryRepository;
    private final ParticipantAgeCategoryRepository participantAgeCategoryRepository;
    private final RatingRepository ratingRepository;
    private final TimeRepository timeRepository;
    private final TableRepository tableRepository;
    private final TableTimeRepository tableTimeRepository;
    private final List<DateTimeFormatter> DATE_FORMATTERS = List.of(
            DateTimeFormatter.ofPattern("dd.MM.yyyy"),
            new DateTimeFormatterBuilder()
                    .appendPattern("yyyy")
                    .parseDefaulting(ChronoField.MONTH_OF_YEAR, 1)
                    .parseDefaulting(ChronoField.DAY_OF_MONTH, 1)
                    .toFormatter(),
            DateTimeFormatter.ofPattern("dd/MM/yyyy"),
            DateTimeFormatter.ofPattern("dd MMMM yyyy", new Locale("tr", "TR")),
            DateTimeFormatter.ofPattern("dd MM yyyy"),
            DateTimeFormatter.ofPattern("yyyy-MM-dd")
    );

    @Override
    public void run(String... args) {
//        initializeTimeData();
//        initializeTableData();
//        initializeTableTimeData();
    }

    private void initializeTimeData() {
        LocalTime startTime = LocalTime.of(10, 0);
        LocalTime endTime = LocalTime.of(19, 0);

        timeRepository.saveAll(List.of(
                new Time(LocalTime.of(10, 0), LocalTime.of(12, 0)),
                new Time(LocalTime.of(13, 0), LocalTime.of(15, 0)),
                new Time(LocalTime.of(15, 0), LocalTime.of(17, 0)),
                new Time(LocalTime.of(17, 0), LocalTime.of(19, 0))));
//        while (startTime.isBefore(endTime)) {
//            timeRepository.save(
//                    new Time(startTime,
//                            startTime.plusHours(2)));
//            startTime = startTime.plusHours(2);
//        }
    }

    private void initializeTableData() {
        int NUM_OF_TABLES = 16;

        for (int i = 0; i < NUM_OF_TABLES; i++) {
            tableRepository.save(new Table("Masa " + (i + 1)));
        }
    }

    private void initializeTableTimeData() {
        for (Time time : timeRepository.findAll()) {
            for (Table table : tableRepository.findAll()) {
                tableTimeRepository.save(new TableTime(table, time, true));
            }
        }
    }

    private void createAllValidAgeCategoryCombinations() {
        List<AgeCategory> ageCategories = new ArrayList<>();

        for (AGE_CATEGORY ageCategory : AGE_CATEGORY.values()) {
            for (AGE age : ageCategory.ageList) {
                AgeCategory newCategory = new AgeCategory(ageCategory, age);
                ageCategories.add(newCategory);
            }
        }

        ageCategoryRepository.saveAll(ageCategories);
    }

    private void createParticipantsUsingCsvFile() {
        List<String[]> data = CSVHelper.readFile();

        for (String[] line : data) {
            String fullName = line[0].trim();
            String[] splitName = fullName.split(" ");
            String firstName = toLowerCaseTurkish(String.join(
                    " ", Arrays.copyOfRange(splitName, 0, splitName.length - 1)).trim());
            String lastName = toLowerCaseTurkish(splitName[splitName.length - 1].trim());
            String email = line[1].trim();
            String birthDate = line[2].trim();
            String gender = line[3].trim();
            String city = line[4].trim();
            String phone = line[5].trim();
            String age = line[7].trim();
            String rating = line[8].trim();

            if (age.isEmpty()) continue;

            Participant participant = new Participant();
            participant.setFirstName(firstName);
            participant.setLastName(lastName);
            participant.setEmail(email);
            LocalDate parsedDate = parseDate(birthDate);
            if (parsedDate == null) {
                parsedDate = parseDate("17.04.1962");
            }
            participant.setBirthDate(parsedDate);
            participant.setGender(GENDER.getByLabel(gender));
            participant.setCity(city);
            participant.setPhoneNumber(phone);
            participant.setRating(Integer.parseInt(rating));

            participantRepository.save(participant);

            line[6] = participant.getId().toString();

            ParticipantAgeCategory participantAgeCategory = new ParticipantAgeCategory();
            participantAgeCategory.setParticipant(participant);
            participantAgeCategory.setPairName("");

            AGE ageEnum = AGE.getByAge(age);
            AGE_CATEGORY category = participant.getGender() == GENDER.MALE ?
                    AGE_CATEGORY.SINGLE_MEN :
                    AGE_CATEGORY.SINGLE_WOMEN;

            participantAgeCategory
                    .setAgeCategory(ageCategoryRepository
                            .findByAgeAndCategory(ageEnum, category));

            participantAgeCategoryRepository.save(participantAgeCategory);
        }

        CSVHelper.createDataCsv(data);
    }

    private void createParticipantsUsingExcelFile(String path) {
        try (InputStream stream = new FileInputStream(path)) {
            Workbook workbook = new XSSFWorkbook(stream);
            Sheet sheet = workbook.getSheetAt(0);
            Iterator<Row> rowIterator = sheet.iterator();
            // skip the column name row
            int rowCount = 1;
            if (rowIterator.hasNext()) {
                Row row = rowIterator.next();
                Cell id = row.createCell(11);
                id.setCellValue("ID");

                Cell age = row.createCell(12);
                age.setCellValue("Yaş Grubu");

                Cell rating = row.createCell(13);
                rating.setCellValue("Puan");
            }
            while (rowIterator.hasNext()) {
                Row row = rowIterator.next();
                Cell time = row.getCell(0);
                if (time == null) break;
                String fullName = row.getCell(1).getStringCellValue().trim();
                String[] splitName = fullName.split(" ");
                String firstName = toLowerCaseTurkish(String.join(
                        " ", Arrays.copyOfRange(splitName, 0, splitName.length - 1)).trim());
                String lastName = toLowerCaseTurkish(splitName[splitName.length - 1].trim());

                String email = getCellValue(row.getCell(2)).trim();
                String birthDate = getCellValue(row.getCell(3)).trim();
                String gender = getCellValue(row.getCell(4)).trim();
                String city = getCellValue(row.getCell(5)).trim();
                String phoneNumber = getCellValue(row.getCell(10)).trim();

                Participant participant = new Participant();
                participant.setEmail(email);
                participant.setFirstName(firstName);
                participant.setLastName(lastName);
                LocalDate parsedDate = parseDate(birthDate);
                if (parsedDate == null) {
                    parsedDate = parseDate("17.04.1962");
                }
                participant.setBirthDate(parsedDate);
                participant.setGender(gender.equals("Erkek") ? GENDER.MALE : GENDER.FEMALE);
                participant.setCity(city);
                participant.setPhoneNumber(phoneNumber);

                Rating rating = ratingRepository
                        .findByParticipantName(
                                participant.getFirstName() + " "
                                        + participant.getLastName());

                if (rating != null) {
                    participant.setRating(rating.getRating());
                }

                participantRepository.save(participant);

                ParticipantAgeCategory participantAgeCategory = new ParticipantAgeCategory();
                participantAgeCategory.setParticipant(participant);
                participantAgeCategory.setPairName("");
                if (participant.getGender() == GENDER.MALE) {
                    calculateAgeCategory(participantAgeCategory,
                            participant.getBirthDate(),
                            AGE_CATEGORY.SINGLE_MEN);
                } else {
                    calculateAgeCategory(participantAgeCategory,
                            participant.getBirthDate(),
                            AGE_CATEGORY.SINGLE_WOMEN);
                }

                Cell age = row.getCell(12);
                if (age != null) {
                    String ageVal = getCellValue(age).trim();
                    if (participantAgeCategory.getAgeCategory() != null) {
                        participantAgeCategory.setAgeCategory(
                                ageCategoryRepository.findByAgeAndCategory(
                                        AGE.getByAge(ageVal),
                                        participantAgeCategory.getAgeCategory().getCategory()
                                )
                        );
                    }
                } else {
                    age = row.createCell(12);
                    if (participantAgeCategory.getAgeCategory() != null) {
                        String ageStr = participantAgeCategory.getAgeCategory().getAge().age;
                        age.setCellValue(ageStr != null ? ageStr : "");
                    }
                }

                participantAgeCategoryRepository.save(participantAgeCategory);
                rowCount++;

                Cell id = row.getCell(11);
                if (id == null) {
                    id = row.createCell(11);
                }
                id.setCellValue(participant.getId());

                Cell ratingCell = row.createCell(13);
                ratingCell.setCellValue(participant.getRating());
            }

            try (OutputStream os = new FileOutputStream(path)) {
                workbook.write(os);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void readRatingsFromPdfFile(String path) {
        try {
            PdfReader reader = new PdfReader(getClass().getResourceAsStream(path));

            List<String[]> pages = new ArrayList<>();
            for (int i = 1; i <= reader.getNumberOfPages(); i++) {
                pages.add(PdfTextExtractor.getTextFromPage(reader, i).split("\n"));
            }

            for (String[] page : pages) {
                page = Arrays.copyOfRange(page, 4, page.length);

                for (String line : page) {
                    String[] content = extractNameAndNumber(line);
                    if (content != null) {
                        int rating = content[1].equals("#N/A") ? 0 : Integer.parseInt(content[1]);
                        ratingRepository.save(new Rating(content[0], rating));
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void matchRatings() {
        List<Rating> ratings = ratingRepository.findAll();
//        List<Participant> participants = participantRepository.findAll();
        for (Rating rating : ratings) {
            List<String> variations = generateVariations(rating.getParticipantName());
            for (String variation : variations) {
                String[] names = variation.split(" ");
                String firstName = String.join(" ", Arrays.copyOfRange(names, 0, names.length - 1));
                String lastname = names[names.length - 1];
                Participant participant = participantRepository.findByFirstNameAndLastName(firstName, lastname);
                if (participant != null) {
                    participant.setRating(rating.getRating());
                    participantRepository.save(participant);
                    break;
                }
            }
        }
    }

    private List<String> generateVariations(String input) {
        List<String> variations = new ArrayList<>();
        generateVariationsHelper(input, 0, new StringBuilder(), variations);
        return variations;
    }

    private void generateVariationsHelper(String input, int index, StringBuilder current, List<String> variations) {
        if (index == input.length()) {
            variations.add(current.toString());
            return;
        }

        char ch = input.charAt(index);
        if (ch == 'ü' || ch == 'u') {
            current.append('ü');
            generateVariationsHelper(input, index + 1, current, variations);
            current.setLength(current.length() - 1);

            current.append('u');
            generateVariationsHelper(input, index + 1, current, variations);
            current.setLength(current.length() - 1);
        } else if (ch == 'i' || ch == 'ı') {
            current.append('ı');
            generateVariationsHelper(input, index + 1, current, variations);
            current.setLength(index);

            current.append('i');
            generateVariationsHelper(input, index + 1, current, variations);
            current.setLength(current.length() - 1);
        } else if (ch == 'ö' || ch == 'o') {
            current.append('ö');
            generateVariationsHelper(input, index + 1, current, variations);
            current.setLength(index);

            current.append('o');
            generateVariationsHelper(input, index + 1, current, variations);
            current.setLength(current.length() - 1);
        } else if (ch == 'ğ' || ch == 'g') {
            current.append('ğ');
            generateVariationsHelper(input, index + 1, current, variations);
            current.setLength(index);

            current.append('g');
            generateVariationsHelper(input, index + 1, current, variations);
            current.setLength(index);
        } else if (ch == 'ç' || ch == 'c') {
            current.append('ç');
            generateVariationsHelper(input, index + 1, current, variations);
            current.setLength(index);

            current.append('c');
            generateVariationsHelper(input, index + 1, current, variations);
            current.setLength(index);
        } else if (ch == 'ş' || ch == 's') {
            current.append('ş');
            generateVariationsHelper(input, index + 1, current, variations);
            current.setLength(index);

            current.append('s');
            generateVariationsHelper(input, index + 1, current, variations);
            current.setLength(index);
        } else {
            current.append(Character.toLowerCase(ch));
            generateVariationsHelper(input, index + 1, current, variations);
        }
    }

    private String[] extractNameAndNumber(String input) {
        // Split the input by spaces
        String[] parts = input.split("\\s+");
        StringBuilder nameBuilder = new StringBuilder();
        String number = parts[parts.length - 1];

        // Reassemble the name part and find the first number part or #N/A
        for (String part : parts) {
            if (!(part.matches("\\d+") || part.equals("#N/A"))) {
                for (char ch : part.toCharArray()) {
                    if (!Character.isDigit(ch)) {
                        nameBuilder.append(ch);
                    }
                }
                nameBuilder.append(" ");
            }
        }

        String name = toLowerCaseTurkish(nameBuilder.toString().trim());
        if (number.equals("#N/A")) {
            number = "0";
        }
        return new String[]{name, number};

    }

    private String toLowerCaseTurkish(String input) {
        if (input == null) {
            return null;
        }
        return input.toLowerCase(new Locale("tr", "TR"));
    }

    private String getCellValue(Cell cell) {
        return switch (cell.getCellType()) {
            case STRING -> cell.getStringCellValue();
            case NUMERIC -> Integer.toString((int) cell.getNumericCellValue());
            default -> "";
        };
    }

    private LocalDate parseDate(String date) {
        for (DateTimeFormatter formatter : DATE_FORMATTERS) {
            try {
                return LocalDate.parse(date, formatter);
            } catch (DateTimeParseException e) {
            }
        }

        return null;
    }

    private long calculateAge(LocalDate birthDate) {
        LocalDate now = LocalDate.now();
        return ChronoUnit.YEARS.between(birthDate, now);
    }

    private void calculateAgeCategory(ParticipantAgeCategory participantAgeCategory,
                                      LocalDate birthDate,
                                      AGE_CATEGORY category) {
        long age = calculateAge(birthDate);
        List<AGE> ageList = category.ageList;
        for (AGE ageEnum : ageList) {
            List<Integer> ageRange = Arrays.stream(ageEnum.age
                            .split("-"))
                    .map(a -> {
                        if (a.contains("+")) a = a.substring(0, a.indexOf("+"));
                        return Integer.parseInt(a);
                    })
                    .toList();

            if (age >= ageRange.get(0) &&
                    (ageRange.size() == 1 || age <= ageRange.get(1))) {
                participantAgeCategory
                        .setAgeCategory(ageCategoryRepository
                                .findByAgeAndCategory(ageEnum, category));
                break;
            }
        }
    }
}
