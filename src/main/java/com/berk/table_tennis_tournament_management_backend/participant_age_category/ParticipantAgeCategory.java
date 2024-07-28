package com.berk.table_tennis_tournament_management_backend.participant_age_category;

import com.berk.table_tennis_tournament_management_backend.age_category.AgeCategory;
import com.berk.table_tennis_tournament_management_backend.participant.Participant;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class ParticipantAgeCategory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "age_category_id")
    private AgeCategory ageCategory;

    @ManyToOne
    @JoinColumn(name = "participant_id")
    private Participant participant;

//    @ManyToOne
//    @JoinColumn(name = "pair_id")
    private String pairName;

    public ParticipantAgeCategory(AgeCategory ageCategory, Participant participant, String pairName) {
        this.ageCategory = ageCategory;
        this.participant = participant;
        this.pairName = pairName;
    }
}
