package com.berk.table_tennis_tournament_management_backend;

import com.berk.table_tennis_tournament_management_backend.age_category.AGE;
import com.berk.table_tennis_tournament_management_backend.age_category.AGE_CATEGORY;
import com.berk.table_tennis_tournament_management_backend.age_category.AgeCategory;
import com.berk.table_tennis_tournament_management_backend.age_category.AgeCategoryRepository;
import com.berk.table_tennis_tournament_management_backend.participant.Participant;
import com.berk.table_tennis_tournament_management_backend.participant.ParticipantRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Component
public class DataInitializer implements CommandLineRunner {

    private final ParticipantRepository participantRepository;
    private final AgeCategoryRepository ageCategoryRepository;

    public DataInitializer(ParticipantRepository participantRepository, AgeCategoryRepository ageCategoryRepository) {
        this.participantRepository = participantRepository;
        this.ageCategoryRepository = ageCategoryRepository;
    }

    @Override
    public void run(String... args) {
        if (participantRepository.count() == 0) {
            List<Participant> participants = generateParticipants();
            participantRepository.saveAll(participants);
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
}
