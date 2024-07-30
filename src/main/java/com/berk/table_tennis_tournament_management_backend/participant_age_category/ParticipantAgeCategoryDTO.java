package com.berk.table_tennis_tournament_management_backend.participant_age_category;

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
}
