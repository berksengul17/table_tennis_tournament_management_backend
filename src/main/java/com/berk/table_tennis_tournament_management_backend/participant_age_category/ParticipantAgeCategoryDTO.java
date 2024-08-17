package com.berk.table_tennis_tournament_management_backend.participant_age_category;

import com.berk.table_tennis_tournament_management_backend.age_category.AGE;
import com.berk.table_tennis_tournament_management_backend.age_category.AGE_CATEGORY;
import com.berk.table_tennis_tournament_management_backend.age_category.AgeCategory;
import com.berk.table_tennis_tournament_management_backend.participant.GENDER;
import com.berk.table_tennis_tournament_management_backend.participant.Participant;
import com.berk.table_tennis_tournament_management_backend.participant.ParticipantDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
public class ParticipantAgeCategoryDTO {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private String gender;
    private LocalDate birthDate;
    private String city;
    private int rating;
    private String category;
    private String age;
    private String pairName;
    private String hotel;

    public ParticipantAgeCategoryDTO(ParticipantAgeCategory participantAgeCategory) {
        Participant participant = participantAgeCategory.getParticipant();
        AgeCategory ageCategory = participantAgeCategory.getAgeCategory();

        this.id = participantAgeCategory.getId();
        this.firstName = participant.getFirstName();
        this.lastName = participant.getLastName();
        this.email = participant.getEmail();
        this.phoneNumber = participant.getPhoneNumber();
        this.gender = initGender(participant);
        this.birthDate = participant.getBirthDate();
        this.city = participant.getCity();
        this.rating = participant.getRating();
        this.category = initCategory(participantAgeCategory.getAgeCategory(),
                GENDER.getByLabel(this.gender));
        this.age = initAge(ageCategory);
        this.pairName = participantAgeCategory.getPairName();

    }

    private String initGender(Participant participant) {
        return participant.getGender() != null ?
                participant.getGender().label :
                GENDER.MALE.label;
    }

    private String initCategory(AgeCategory ageCategory, GENDER gender) {
        if (ageCategory.getCategory() != null) {
            return ageCategory.getCategory().label;
        } else {
            return gender == GENDER.MALE ?
                    AGE_CATEGORY.SINGLE_MEN.label :
                    AGE_CATEGORY.SINGLE_WOMEN.label;
        }
    }

    private String initAge(AgeCategory ageCategory) {
        return ageCategory.getAge() != null ?
                ageCategory.getAge().age :
                ageCategory.getCategory().ageList.get(0).age;
    }
}
