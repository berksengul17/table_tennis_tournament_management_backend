package com.berk.table_tennis_tournament_management_backend.participant;

import com.berk.table_tennis_tournament_management_backend.StringHelper;
import com.berk.table_tennis_tournament_management_backend.age_category.AgeCategory;
import com.berk.table_tennis_tournament_management_backend.group.Group;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Participant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private GENDER gender;
    private LocalDate birthDate;
//    @ManyToOne
//    @JoinColumn(name="age_category_id", nullable=false)
//    @JsonIgnoreProperties("participants")
//    private AgeCategory ageCategory;
    @ManyToOne
    @JoinColumn(name = "group_id")
    @JsonIgnore
    private Group group;
    private String city;
    private int rating;
    private int groupRanking;

    public Participant(ParticipantDTO participantDTO) {
        this.firstName = StringHelper.toLowerCaseTurkish(participantDTO.getFirstName());
        this.lastName = StringHelper.toLowerCaseTurkish(participantDTO.getLastName());
        this.email = participantDTO.getEmail();
        this.phoneNumber = participantDTO.getPhoneNumber();
        this.gender = GENDER.valueOf(participantDTO.getGender());
        this.birthDate = participantDTO.getBirthDate();
        this.city = participantDTO.getCity();
    }

    public String getFullName() {
        return StringHelper.toLowerCaseTurkish(firstName + " " + lastName);
    }
}
