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

@Component
@AllArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final ParticipantRepository participantRepository;
    private final AgeCategoryRepository ageCategoryRepository;
    private final ParticipantAgeCategoryRepository participantAgeCategoryRepository;
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
            List<Participant> participants = generateParticipants();
            participantRepository.saveAll(participants);
            readExcelFile("/example_participants.xlsx");
            System.out.println("Saved participants to the database.");
        }
    }

    private List<Participant> generateParticipants() {
        List<Participant> participants = new ArrayList<>();
        Random random = new Random();
        String[] firstNames = {"Sabriye", "Ali", "Ayşe", "Mehmet", "Fatma", "Mustafa", "Zeynep", "Ahmet", "Elif", "Murat"};
        String[] lastNames = {"Ceylan", "Yılmaz", "Demir", "Kaya", "Şahin", "Çelik", "Arslan", "Öztürk", "Koç", "Acar"};
        String[] cities = {"İzmir", "Ankara", "İstanbul", "Bursa", "Antalya", "Konya", "Adana", "Gaziantep", "Mersin", "Eskişehir"};
        String[] genders = {"Kadın", "Erkek"};
        List<AgeCategory> ageCategories = createAllValidAgeCategoryCombinations();

        ageCategoryRepository.saveAll(ageCategories);

//        for (int i = 0; i < 128; i++) {
//            Participant participant = new Participant();
//            participant.setFirstName(firstNames[random.nextInt(firstNames.length)]);
//            participant.setLastName(lastNames[random.nextInt(lastNames.length)]);
//            participant.setEmail("participant" + i + "@example.com");
//            participant.setPhoneNumber(String.format("%03d %03d %02d %02d", random.nextInt(1000), random.nextInt(1000), random.nextInt(100), random.nextInt(100)));
//            participant.setGender(genders[random.nextInt(genders.length)]);
//            participant.setBirthDate(LocalDate.now().minusYears(random.nextInt(50) + 18));
//            participant.setAgeCategory(ageCategories.get(random.nextInt(5))); // random.nextInt(5)
//            participant.setCity(cities[random.nextInt(cities.length)]);
//            participant.setRating((random.nextInt(201) + 100) * 10);
//
//            participants.add(participant);
//        }

        return participants;
    }

    private List<AgeCategory> createAllValidAgeCategoryCombinations() {
        List<AgeCategory> ageCategories = new ArrayList<>();

        for (AGE_CATEGORY ageCategory : AGE_CATEGORY.values()) {
            for (AGE age : ageCategory.ageList) {
                AgeCategory newCategory = new AgeCategory(ageCategory, age);
                ageCategories.add(newCategory);
            }
        }

        return ageCategories;
    }

    private void readExcelFile(String path) {
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
                String firstName = String.join(
                        " ", Arrays.copyOfRange(splitName, 0, splitName.length - 1));
                String lastName = splitName[splitName.length - 1];

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
