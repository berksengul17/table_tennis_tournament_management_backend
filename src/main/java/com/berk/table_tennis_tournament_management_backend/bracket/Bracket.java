package com.berk.table_tennis_tournament_management_backend.bracket;

import com.berk.table_tennis_tournament_management_backend.age_category.AgeCategory;
import com.berk.table_tennis_tournament_management_backend.round.Round;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Bracket {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private BRACKET_TYPE bracketType;
    @ManyToOne
    @JoinColumn(name = "age_category_id")
    private AgeCategory ageCategory;
    @OneToMany(mappedBy = "bracket")
    private List<Round> rounds;

    public Bracket(BRACKET_TYPE bracketType) {
        this.bracketType = bracketType;
    }
}
