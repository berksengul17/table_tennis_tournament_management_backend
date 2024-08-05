package com.berk.table_tennis_tournament_management_backend.participant_age_category;

import com.berk.table_tennis_tournament_management_backend.ExcelHelper;
import com.berk.table_tennis_tournament_management_backend.age_category.AGE;
import com.berk.table_tennis_tournament_management_backend.age_category.AGE_CATEGORY;
import com.berk.table_tennis_tournament_management_backend.age_category.AgeCategory;
import com.berk.table_tennis_tournament_management_backend.age_category.AgeCategoryRepository;
import com.berk.table_tennis_tournament_management_backend.participant.GENDER;
import com.berk.table_tennis_tournament_management_backend.participant.Participant;
import com.berk.table_tennis_tournament_management_backend.participant.ParticipantDTO;
import com.berk.table_tennis_tournament_management_backend.participant.ParticipantRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class ParticipantAgeCategoryService {

    private final ParticipantAgeCategoryRepository participantAgeCategoryRepository;
    private final ParticipantRepository participantRepository;
    private final AgeCategoryRepository ageCategoryRepository;

    public List<ParticipantAgeCategoryDTO> getParticipantAgeCategory(Integer categoryVal, Integer ageVal) {
        AGE_CATEGORY category = null;
        AGE age = null;

        if (categoryVal != null && ageVal != null) {
            category = AGE_CATEGORY.valueOf(categoryVal);
            age = category.ageList.get(ageVal);
        }

        List<ParticipantAgeCategory> participantAgeCategories = participantAgeCategoryRepository
                .findAll(Sort.by(Sort.Direction.ASC, "participant.firstName", "participant.lastName"));

        List<ParticipantAgeCategoryDTO> participants = new ArrayList<>();
        for (ParticipantAgeCategory participantAgeCategory : participantAgeCategories) {
            Participant participant = participantAgeCategory.getParticipant();
            AgeCategory ageCategory = participantAgeCategory.getAgeCategory();
            if (ageCategory == null ||
                    (category != null && ageCategory.getCategory() != category) ||
                    (age != null && ageCategory.getAge() != age)) continue;
            participants.add(new ParticipantAgeCategoryDTO(
                participantAgeCategory.getId(),
                participant.getFirstName(),
                participant.getLastName(),
                participant.getEmail(),
                participant.getPhoneNumber(),
                participant.getGender().label,
                participant.getBirthDate(),
                participant.getCity(),
                participant.getRating(),
                ageCategory.getCategory().label,
                ageCategory.getAge().age,
                participantAgeCategory.getPairName()
            ));
        }

        return participants;
    }

    //TODO excel dosyasını güncelle
    public String updateParticipant(Long id,
                                    ParticipantAgeCategoryDTO participantAgeCategoryDTO) {
        ParticipantAgeCategory participantAgeCategory = participantAgeCategoryRepository
                .findById(id)
                .orElse(null);

        if (participantAgeCategory == null) {
            throw new IllegalArgumentException("Participant age category not found");
        }
        
        Participant participant = participantAgeCategory.getParticipant();
        AgeCategory ageCategory = participantAgeCategory.getAgeCategory();

        participant.setFirstName(participantAgeCategoryDTO.getFirstName());
        participant.setLastName(participantAgeCategoryDTO.getLastName());
        participant.setEmail(participantAgeCategoryDTO.getEmail());
        participant.setPhoneNumber(participantAgeCategoryDTO.getPhoneNumber());
        participant.setGender(GENDER.getByLabel(participantAgeCategoryDTO.getGender()));
        participant.setBirthDate(participantAgeCategoryDTO.getBirthDate());
        participant.setCity(participantAgeCategoryDTO.getCity());
        participant.setRating(participantAgeCategoryDTO.getRating());
        participantAgeCategory.setAgeCategory(
                ageCategoryRepository.findByAgeAndCategory(
                        AGE.getByAge(participantAgeCategoryDTO.getAge()),
                        AGE_CATEGORY.getByLabel(participantAgeCategoryDTO.getCategory())
                )
        );
        participantAgeCategory.setPairName(participantAgeCategoryDTO.getPairName());

        participantRepository.save(participant);
        ageCategoryRepository.save(ageCategory);
        participantAgeCategoryRepository.save(participantAgeCategory);

        ExcelHelper.editRow(participantAgeCategory, false);

        return "Participant age category updated successfully";
    }
}
