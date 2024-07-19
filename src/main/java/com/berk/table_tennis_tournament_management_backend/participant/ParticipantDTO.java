package com.berk.table_tennis_tournament_management_backend.participant;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class ParticipantDTO {
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private String gender;
    private LocalDate birthDate;
    private int ageCategory;
    private String city;
}
