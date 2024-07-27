package com.berk.table_tennis_tournament_management_backend.age_category;

import com.berk.table_tennis_tournament_management_backend.participant.Participant;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class AgeCategory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AGE_CATEGORY category;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AGE age;

    @OneToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch = FetchType.LAZY, mappedBy = "ageCategory")
    private List<Participant> participants;

    public AgeCategory(AGE_CATEGORY category, AGE age) {
        this.category = category;
        this.age = age;
    }
}