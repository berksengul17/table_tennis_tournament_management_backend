package com.berk.table_tennis_tournament_management_backend.participant;

import com.berk.table_tennis_tournament_management_backend.age_category.AGE;
import com.berk.table_tennis_tournament_management_backend.age_category.AGE_CATEGORY;
import com.berk.table_tennis_tournament_management_backend.age_category.AgeCategory;
import com.berk.table_tennis_tournament_management_backend.age_category.AgeCategoryRepository;
import com.berk.table_tennis_tournament_management_backend.hotel.HotelRepository;
import com.berk.table_tennis_tournament_management_backend.match.Match;
import com.berk.table_tennis_tournament_management_backend.match.MatchRepository;
import com.berk.table_tennis_tournament_management_backend.participant_age_category.ParticipantAgeCategory;
import com.berk.table_tennis_tournament_management_backend.participant_age_category.ParticipantAgeCategoryDTO;
import com.berk.table_tennis_tournament_management_backend.participant_age_category.ParticipantAgeCategoryRepository;
import com.berk.table_tennis_tournament_management_backend.rating.Rating;
import com.berk.table_tennis_tournament_management_backend.rating.RatingRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Service
@AllArgsConstructor
public class ParticipantService {

    private final ParticipantRepository participantRepository;
    private final AgeCategoryRepository ageCategoryRepository;
    private final ParticipantAgeCategoryRepository participantAgeCategoryRepository;
    private HotelRepository hotelRepository;
    private final RatingRepository ratingRepository;
    private final MatchRepository matchRepository;

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
        participant.setHotel(hotelRepository.
                findById(participantDTO.getHotel() + 1)
                .orElse(null));

        updateRating(participant);

        GENDER gender = GENDER.valueOf(participantDTO.getGender());

        if (participantDTO.getPairName() != null) {
            Participant foundParticipant = participantRepository
                    .findByFullName(participantDTO.getPairName());
            if (foundParticipant == null) {
                AGE_CATEGORY category = gender == GENDER.MALE ? AGE_CATEGORY.DOUBLE_MEN : AGE_CATEGORY.DOUBLE_WOMEN;
                AGE age = calculateAgeCategory(participantDTO.getBirthDate(), category);
                AgeCategory ageCategory = ageCategoryRepository.findByAgeAndCategory(age, category);

                ParticipantAgeCategory participantAgeCategory = new ParticipantAgeCategory(ageCategory,
                        participant,
                        participantDTO.getPairName());

                participantAgeCategoryRepository.save(participantAgeCategory);
            }
        }

        AGE_CATEGORY category = gender == GENDER.MALE ? AGE_CATEGORY.SINGLE_MEN : AGE_CATEGORY.SINGLE_WOMEN;
        AGE age = calculateAgeCategory(participantDTO.getBirthDate(), category);
        AgeCategory ageCategory = ageCategoryRepository.findByAgeAndCategory(age, category);

        ParticipantAgeCategory participantAgeCategory = new ParticipantAgeCategory(ageCategory,
                participant, "");

        participantAgeCategoryRepository.save(participantAgeCategory);

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
        List<Match> matches = matchRepository.findAllByParticipant(participant);

        // delete all matches where the participant is involved
        matchRepository.deleteAllById(matches.stream().map(Match::getId).toList());

        participantAgeCategoryRepository.delete(participantAgeCategory);
        participantRepository.deleteById(participant.getId());

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

    private AGE calculateAgeCategory(LocalDate birthDate,
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
                return ageEnum;
            }
        }

        return null;
    }

    private long calculateAge(LocalDate birthDate) {
        LocalDate now = LocalDate.now();
        return ChronoUnit.YEARS.between(birthDate, now);
    }
}
