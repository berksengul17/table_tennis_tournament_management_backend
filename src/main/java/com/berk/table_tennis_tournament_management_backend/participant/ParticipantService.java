package com.berk.table_tennis_tournament_management_backend.participant;

import com.berk.table_tennis_tournament_management_backend.age_category.AgeCategoryRepository;
import org.springframework.stereotype.Service;

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
public class ParticipantService {

    private final ParticipantRepository participantRepository;

    public ParticipantService(ParticipantRepository participantRepository) {
        this.participantRepository = participantRepository;
    }

    public Participant register(Participant participant) {
        return participantRepository.save(participant);
    }

    public List<Participant> getParticipants() {
        return participantRepository.findAll();
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
