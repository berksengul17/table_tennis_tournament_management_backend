package com.berk.table_tennis_tournament_management_backend.age_category;

import com.berk.table_tennis_tournament_management_backend.participant.Participant;
import com.berk.table_tennis_tournament_management_backend.participant.ParticipantService;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
public class AgeCategoryService {

    private final AgeCategoryRepository ageCategoryRepository;
    private final ParticipantService participantService;

    public AgeCategoryService(AgeCategoryRepository ageCategoryRepository, ParticipantService participantService) {
        this.ageCategoryRepository = ageCategoryRepository;
        this.participantService = participantService;
    }

    @Transactional
    public List<AgeCategory> createAgeCategories() {
        List<Participant> participants = participantService.getParticipants();
        List<AgeCategory> ageCategories = new ArrayList<>();

        for (Participant participant : participants) {
            AgeCategory existingCategory = ageCategories.stream()
                    .filter(ageCategory -> ageCategory.getCategory() == participant.getAgeCategory().getCategory())
                    .findFirst()
                    .orElse(null);

            if (existingCategory == null) {
                AgeCategory newCategory = new AgeCategory();
                newCategory.setCategory(participant.getAgeCategory().getCategory());
                newCategory.setParticipants(new ArrayList<>());
                newCategory.getParticipants().add(participant);
                ageCategories.add(newCategory);
            } else {
                existingCategory.getParticipants().add(participant);
            }
        }

        return ageCategoryRepository.saveAll(ageCategories);
    }

    public List<AgeCategory> loadAgeCategories() {
        return ageCategoryRepository.findAll();
    }

}
