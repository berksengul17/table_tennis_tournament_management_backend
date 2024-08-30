package com.berk.table_tennis_tournament_management_backend.seed_participant;

import com.berk.table_tennis_tournament_management_backend.participant.Participant;
import com.berk.table_tennis_tournament_management_backend.seed.Seed;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class SeedParticipant {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "seed_id")
    private Seed seed;
    @ManyToOne
    @JoinColumn(name = "participant_id")
    private Participant participant;
    private int pIndex;
    private Integer score;

    public SeedParticipant(Seed seed) {
        this.seed = seed;
    }

    public SeedParticipant(Seed seed, Participant participant, int pIndex) {
        this.seed = seed;
        this.participant = participant;
        this.pIndex = pIndex;
    }
}
