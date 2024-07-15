package com.berk.table_tennis_tournament_management_backend.round;

import com.berk.table_tennis_tournament_management_backend.bracket.Bracket;
import com.berk.table_tennis_tournament_management_backend.seed.Seed;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Round {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "bracket_id")
    @JsonIgnore
    private Bracket bracket;
    @ManyToMany
    @JoinTable(name = "round_seed",
                joinColumns = @JoinColumn(name = "round_id"),
                inverseJoinColumns = @JoinColumn(name = "seed_id"))
    private List<Seed> seeds;

    public Round(Bracket bracket) {
        this.bracket = bracket;
    }

    public Round(Bracket bracket, List<Seed> seeds) {
        this.bracket = bracket;
        this.seeds = seeds;
    }
}
