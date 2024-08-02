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
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.parser.PdfTextExtractor;
import lombok.AllArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.boot.CommandLineRunner;
import org.springframework.cglib.core.Local;
import org.springframework.stereotype.Component;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
@AllArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final ParticipantRepository participantRepository;
    private final AgeCategoryRepository ageCategoryRepository;
    private final ParticipantAgeCategoryRepository participantAgeCategoryRepository;
    private final RatingRepository ratingRepository;
    private final List<DateTimeFormatter> DATE_FORMATTERS = List.of(
            DateTimeFormatter.ofPattern("dd.MM.yyyy"),
            new DateTimeFormatterBuilder()
                    .appendPattern("yyyy")
                    .parseDefaulting(ChronoField.MONTH_OF_YEAR, 1)
                    .parseDefaulting(ChronoField.DAY_OF_MONTH, 1)
                    .toFormatter(),
            DateTimeFormatter.ofPattern("dd/MM/yyyy"),
            DateTimeFormatter.ofPattern("dd MMMM yyyy", new Locale("tr", "TR")),
            DateTimeFormatter.ofPattern("dd MM yyyy")
    );

    @Override
    public void run(String... args) {
        if (participantRepository.count() == 0) {
            createAllValidAgeCategoryCombinations();
            readRatingsFromPdfFile("/ratings.pdf");
            createParticipantsUsingExcelFile("/example_participants.xlsx");
//            matchRatings();
            System.out.println("Saved participants to the database.");
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

    private void createParticipantsUsingExcelFile(String path) {
        try(InputStream stream = getClass().getResourceAsStream(path)) {
            Workbook workbook = new XSSFWorkbook(stream);
            Sheet sheet = workbook.getSheetAt(0);
            Iterator<Row> rowIterator = sheet.iterator();
            // skip the column name row
            int rowCount = 1;
            if (rowIterator.hasNext()) rowIterator.next();
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

                participantAgeCategoryRepository.save(participantAgeCategory);
                rowCount++;
            }
        } catch(FileNotFoundException e) {
            e.printStackTrace();
        } catch(IOException e) {
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
        StringBuilder numberBuilder = new StringBuilder();

        // Reassemble the name part and find the first number part or #N/A
        for (String part : parts) {
            if (part.matches("\\d+") || part.equals("#N/A")) {
                numberBuilder.append(part);
            } else {
                // Separate digits within the name part and add to numberBuilder
                for (char ch : part.toCharArray()) {
                    if (Character.isDigit(ch)) {
                        numberBuilder.append(ch);
                    } else {
                        nameBuilder.append(ch);
                    }
                }
                nameBuilder.append(" ");
            }

            // Stop if we have at least 4 digits
            if (numberBuilder.length() >= 4) {
                break;
            }
        }

        if (numberBuilder.length() > 0) {
            String name = toLowerCaseTurkish(nameBuilder.toString().trim());
            String number = numberBuilder.toString().substring(0, 4); // Ensure the number is exactly 4 digits
            return new String[]{name, number};
        }

        return null;
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
        long yearsBetween = ChronoUnit.YEARS.between(birthDate, now);
        int monthDifference = now.getMonthValue() - birthDate.getMonthValue();

        if (monthDifference < 0 || (monthDifference == 0 && now.getDayOfMonth() < birthDate.getDayOfMonth())) {
            yearsBetween--;
        }

        return yearsBetween;
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
            }
        }
    }
}
