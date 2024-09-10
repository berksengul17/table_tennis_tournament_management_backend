package com.berk.table_tennis_tournament_management_backend.age_category;

import com.berk.table_tennis_tournament_management_backend.participant.GENDER;
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

    public List<AgeCategory> loadAgeCategories() {
        return ageCategoryRepository.findAll();
    }

    public List<String> getCategories(boolean showDoubles) {
        if (showDoubles) {
            return Arrays.stream(AGE_CATEGORY.values())
                    .map(category -> category.label)
                    .toList();
        } else {
            return AGE_CATEGORY.getSingleCategories()
                    .stream()
                    .map(category -> category.label)
                    .toList();
        }
    }

    public String getAgeCategoryString(int category, int age) {
        AGE_CATEGORY categoryEnum = AGE_CATEGORY.valueOf(category);
        AGE ageEnum = categoryEnum.ageList.get(age);

        return categoryEnum.label + " " + ageEnum.age;
    }

    public List<String> getAgeListByCategoryAndGender(int category, int gender) {
        List<AGE_CATEGORY> categories;

        // gender is not provided
        if (gender == -1) {
            categories = Arrays.asList(AGE_CATEGORY.values());
        } else {
            categories = GENDER.valueOf(gender) == GENDER.MALE ?
                    AGE_CATEGORY.getMenCategoryList() : AGE_CATEGORY.getWomenCategoryList();
        }

        return category == -1 ?
                Arrays.stream(AGE.values())
                        .map(a -> a.age)
                        .toList() :
                categories.get(category)
                        .ageList
                        .stream()
                        .map(a -> a.age)
                        .toList();
    }

    public boolean isDouble(AGE_CATEGORY category) {
        return AGE_CATEGORY.DOUBLE_OPEN == category;
    }
}
