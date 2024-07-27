package com.berk.table_tennis_tournament_management_backend.age_category;

import com.berk.table_tennis_tournament_management_backend.participant.Participant;
import com.berk.table_tennis_tournament_management_backend.participant.ParticipantService;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.sql.Array;
import java.util.ArrayList;
import java.util.Arrays;
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
        List<AgeCategory> ageCategories = loadAgeCategories();

        if (!ageCategories.isEmpty()) {
            return ageCategories;
        }

        List<Participant> participants = participantService.getParticipants();

        for (Participant participant : participants) {
            AGE_CATEGORY category = participant.getAgeCategory().getCategory();
            AGE age = participant.getAgeCategory().getAge();

            // Validate if the participant's age is valid for the category
            if (!category.ageList.contains(age)) {
                throw new IllegalArgumentException("Invalid age " + age + " for category " + category);
            }

            AgeCategory existingCategory = ageCategories.stream()
                    .filter(ageCategory -> ageCategory.getCategory() == category && ageCategory.getAge() == age)
                    .findFirst()
                    .orElse(null);

            if (existingCategory == null) {
                AgeCategory newCategory = new AgeCategory(category, age);
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

    public List<String> getCategories() {
        return Arrays.stream(AGE_CATEGORY.values())
                .map(category -> category.label)
                .toList();
    }

    public List<String> getAgeListByCategory(int category) {
        return AGE_CATEGORY
                .valueOf(category)
                .ageList.stream()
                .map(age -> age.age)
                .toList();
    }
}
