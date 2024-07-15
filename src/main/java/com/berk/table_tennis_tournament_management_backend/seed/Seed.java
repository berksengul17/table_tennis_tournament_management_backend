package com.berk.table_tennis_tournament_management_backend.seed;

import com.berk.table_tennis_tournament_management_backend.participant.Participant;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Seed {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToMany
    @JoinTable(name = "seed_participant",
            joinColumns = @JoinColumn(name="seed_id"),
            inverseJoinColumns = @JoinColumn(name = "participant_id"))
    private List<Participant> participants;

    public Seed(List<Participant> participants) {
        this.participants = participants;
    }
}
