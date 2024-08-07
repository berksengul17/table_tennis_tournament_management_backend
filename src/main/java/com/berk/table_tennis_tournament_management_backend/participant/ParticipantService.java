package com.berk.table_tennis_tournament_management_backend.participant;

import com.berk.table_tennis_tournament_management_backend.ExcelHelper;
import com.berk.table_tennis_tournament_management_backend.age_category.AGE;
import com.berk.table_tennis_tournament_management_backend.age_category.AGE_CATEGORY;
import com.berk.table_tennis_tournament_management_backend.age_category.AgeCategory;
import com.berk.table_tennis_tournament_management_backend.age_category.AgeCategoryRepository;
import com.berk.table_tennis_tournament_management_backend.participant_age_category.ParticipantAgeCategory;
import com.berk.table_tennis_tournament_management_backend.participant_age_category.ParticipantAgeCategoryDTO;
import com.berk.table_tennis_tournament_management_backend.participant_age_category.ParticipantAgeCategoryRepository;
import com.berk.table_tennis_tournament_management_backend.rating.Rating;
import com.berk.table_tennis_tournament_management_backend.rating.RatingRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/*
@Service
public class ParticipantService {

    private final ParticipantRepository participantRepository;
    private final CategorizedParticipantRepository categorizedParticipantRepository;

    public ParticipantService(ParticipantRepository participantRepository, CategorizedParticipantRepository categorizedParticipantRepository) {
        this.participantRepository = participantRepository;
        this.categorizedParticipantRepository = categorizedParticipantRepository;
    }

    public Participant register(Participant participant) {
        return participantRepository.save(participant);
    }

    public List<Participant> getParticipants() {
        return participantRepository.findAll();
    }

    public Map<Integer, List<Participant>> categorizeParticipants() {
        List<Participant> participants = getParticipants();
        Map<Integer, List<Participant>> categorizedParticipants = new HashMap<>();

        for (Participant participant : participants) {
            int ageCategory = participant.getAgeCategory();
            List<Participant> players = categorizedParticipants.get(ageCategory);
            if (players == null) {
                players = new ArrayList<>();
            }
            players.add(participant);
            categorizedParticipants.put(ageCategory, players);
        }

        saveCategorizedParticipants(categorizedParticipants);

        return categorizedParticipants;
    }

    private void saveCategorizedParticipants(Map<Integer, List<Participant>> categorizedParticipants) {
        for (Map.Entry<Integer, List<Participant>> entry : categorizedParticipants.entrySet()) {
            CategorizedParticipant cp = new CategorizedParticipant();
            cp.setAgeCategory(entry.getKey());
            cp.setParticipants(entry.getValue());
            categorizedParticipantRepository.save(cp);
        }
    }
}
*/
@Service
@AllArgsConstructor
public class ParticipantService {

    private final ParticipantRepository participantRepository;
    private final AgeCategoryRepository ageCategoryRepository;
    private final ParticipantAgeCategoryRepository participantAgeCategoryRepository;
    private final RatingRepository ratingRepository;

    public ParticipantAgeCategoryDTO register(ParticipantDTO participantDTO) {
        if (participantDTO.getFirstName() == null) {
            participantDTO.setFirstName("");
        }

        if (participantDTO.getLastName() == null) {
            participantDTO.setLastName("");
        }

        if (participantDTO.getEmail() == null) {
            participantDTO.setEmail("");
        }

        if (participantDTO.getPhoneNumber() == null) {
            participantDTO.setPhoneNumber("");
        }

        if (participantDTO.getBirthDate() == null) {
            participantDTO.setBirthDate(LocalDate.now());
        }

        if (participantDTO.getPairName() == null) {
            participantDTO.setPairName("");
        }

        if (participantDTO.getCity() == null) {
            participantDTO.setCity("");
        }

        Participant participant = participantRepository.save(new Participant(participantDTO));

        updateRating(participant);

        GENDER gender = GENDER.valueOf(participantDTO.getGender());

        List<AGE_CATEGORY> categories = gender == GENDER.MALE ?
                AGE_CATEGORY.getMenCategoryList() : AGE_CATEGORY.getWomenCategoryList();

        AGE_CATEGORY category = categories.get(participantDTO.getCategory());
        AGE age = category.ageList.get(participantDTO.getAge());
        AgeCategory ageCategory = ageCategoryRepository.findByAgeAndCategory(age, category);

        ParticipantAgeCategory participantAgeCategory = new ParticipantAgeCategory(ageCategory,
                participant,
                participantDTO.getPairName());

        participantAgeCategoryRepository.save(participantAgeCategory);

//        ExcelHelper.editRow(participantAgeCategory, true);

        return new ParticipantAgeCategoryDTO(participantAgeCategory);
    }

    public void deleteParticipant(Long participantId) {
        Participant participant = participantRepository.findById(participantId)
                .orElse(null);

        if (participant == null) {
            throw new IllegalArgumentException("Participant not found");
        }

        ParticipantAgeCategory participantAgeCategory = participantAgeCategoryRepository
                .findByParticipant(participant);

        participantAgeCategoryRepository.delete(participantAgeCategory);
        participantRepository.delete(participant);

//        ExcelHelper.deleteRow(participantId);
    }

    public List<AgeCategoryWeight> calculateAgeCategoryWeights(Map<Integer, List<Participant>> categorizedParticipants, int totalTables) {
        int totalParticipants = categorizedParticipants.values().stream().mapToInt(List::size).sum();
        List<AgeCategoryWeight> ageCategoryWeights = new ArrayList<>();

        for (Map.Entry<Integer, List<Participant>> entry : categorizedParticipants.entrySet()) {
            int ageCategory = entry.getKey();
            int numberOfParticipants = entry.getValue().size();
            int numberOfTables = (int) Math.round((double) numberOfParticipants / totalParticipants * totalTables);
            ageCategoryWeights.add(new AgeCategoryWeight(ageCategory, numberOfParticipants, numberOfTables));
        }

        return ageCategoryWeights;
    }

    public void updateRating(Participant participant) {
        String fullName = participant.getFullName();
        if (fullName != null && !fullName.trim().isEmpty()) {
            Rating rating = ratingRepository
                    .findByParticipantName(participant.getFullName());
            participant.setRating(rating != null ? rating.getRating() : 0);
        }
    }

    public Map<Integer, List<List<Participant>>> distributeParticipantsToTables(Map<Integer, List<Participant>> categorizedParticipants, int totalTables) {
        List<AgeCategoryWeight> ageCategoryWeights = calculateAgeCategoryWeights(categorizedParticipants, totalTables);
        Map<Integer, List<List<Participant>>> tablesDistribution = new HashMap<>();

        for (AgeCategoryWeight weight : ageCategoryWeights) {
            List<Participant> participants = categorizedParticipants.get(weight.getAgeCategory());
            List<List<Participant>> tables = new ArrayList<>();

            int participantsPerTable = participants.size() / weight.getNumberOfTables();
            int remainder = participants.size() % weight.getNumberOfTables();

            int startIndex = 0;
            for (int i = 0; i < weight.getNumberOfTables(); i++) {
                int endIndex = startIndex + participantsPerTable + (remainder > 0 ? 1 : 0);
                tables.add(participants.subList(startIndex, endIndex));
                startIndex = endIndex;
                remainder--;
            }

            tablesDistribution.put(weight.getAgeCategory(), tables);
        }

        return tablesDistribution;
    }
}
