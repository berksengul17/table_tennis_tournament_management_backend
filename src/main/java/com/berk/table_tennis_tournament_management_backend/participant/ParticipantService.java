package com.berk.table_tennis_tournament_management_backend.participant;

import com.berk.table_tennis_tournament_management_backend.categorizedParticipant.CategorizedParticipant;
import com.berk.table_tennis_tournament_management_backend.categorizedParticipant.CategorizedParticipantRepository;
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
            List<Participant> players = categorizedParticipants.getOrDefault(ageCategory, new ArrayList<>());
            players.add(participant);
            categorizedParticipants.put(ageCategory, players);
        }

        saveCategorizedParticipants(categorizedParticipants);

        return categorizedParticipants;
    }

    private void saveCategorizedParticipants(Map<Integer, List<Participant>> categorizedParticipants) {
        categorizedParticipantRepository.deleteAll(); // Clear previous data
        for (Map.Entry<Integer, List<Participant>> entry : categorizedParticipants.entrySet()) {
            CategorizedParticipant cp = new CategorizedParticipant();
            cp.setAgeCategory(entry.getKey());
            cp.setParticipants(entry.getValue());
            categorizedParticipantRepository.save(cp);
        }
    }

    public Map<Integer, List<Participant>> loadCategorizedParticipants() {
        List<CategorizedParticipant> categorizedEntities = categorizedParticipantRepository.findAll();
        Map<Integer, List<Participant>> categorizedParticipants = new HashMap<>();
        for (CategorizedParticipant entity : categorizedEntities) {
            categorizedParticipants.put(entity.getAgeCategory(), entity.getParticipants());
        }
        return categorizedParticipants;
    }
}
