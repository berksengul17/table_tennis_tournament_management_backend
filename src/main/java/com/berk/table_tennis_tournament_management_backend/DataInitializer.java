package com.berk.table_tennis_tournament_management_backend;

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

    public DataInitializer(ParticipantRepository participantRepository) {
        this.participantRepository = participantRepository;
    }

    @Override
    public void run(String... args) {
        if (participantRepository.count() == 0) {
            List<Participant> participants = generateParticipants();
            participantRepository.saveAll(participants);
            System.out.println("Saved 50 participants to the database.");
        }
    }

    private List<Participant> generateParticipants() {
        List<Participant> participants = new ArrayList<>();
        Random random = new Random();
        String[] firstNames = {"Sabriye", "Ali", "Ayşe", "Mehmet", "Fatma", "Mustafa", "Zeynep", "Ahmet", "Elif", "Murat"};
        String[] lastNames = {"Ceylan", "Yılmaz", "Demir", "Kaya", "Şahin", "Çelik", "Arslan", "Öztürk", "Koç", "Acar"};
        String[] cities = {"İzmir", "Ankara", "İstanbul", "Bursa", "Antalya", "Konya", "Adana", "Gaziantep", "Mersin", "Eskişehir"};
        String[] genders = {"Kadın", "Erkek"};

        for (int i = 0; i < 50; i++) {
            Participant participant = new Participant();
            participant.setFirstName(firstNames[random.nextInt(firstNames.length)]);
            participant.setLastName(lastNames[random.nextInt(lastNames.length)]);
            participant.setEmail("participant" + i + "@example.com");
            participant.setPhoneNumber(String.format("%03d %03d %02d %02d", random.nextInt(1000), random.nextInt(1000), random.nextInt(100), random.nextInt(100)));
            participant.setGender(genders[random.nextInt(genders.length)]);
            participant.setBirthDate(LocalDate.now().minusYears(random.nextInt(50) + 18));
            participant.setAgeCategory(random.nextInt(4)); // 0, 1, 2, or 3
            participant.setCity(cities[random.nextInt(cities.length)]);

            participants.add(participant);
        }

        return participants;
    }
}
