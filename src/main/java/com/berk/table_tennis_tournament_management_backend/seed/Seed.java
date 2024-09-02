package com.berk.table_tennis_tournament_management_backend.seed;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Seed {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @OneToOne
    @JoinColumn(name = "prev_seed_id")
    private Seed prevSeed;

    public Seed(Seed prevSeed) {
        this.prevSeed = prevSeed;
    }
}
