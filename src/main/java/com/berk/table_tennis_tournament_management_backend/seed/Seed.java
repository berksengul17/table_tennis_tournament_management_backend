package com.berk.table_tennis_tournament_management_backend.seed;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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
//    @ManyToMany
//    @JoinTable(name = "seed_participant",
//            joinColumns = @JoinColumn(name="seed_id"),
//            inverseJoinColumns = @JoinColumn(name = "participant_id"))
//    private List<Participant> participants;
//
//    public Seed(List<Participant> participants) {
//        this.participants = participants;
//    }

    // FIXME: Burdaki sort advanceToNextRound u bozuyor. Nerde lazımsa orda sort olması lazım
//    public List<Participant> getParticipants() {
//        if (participants != null) {
//            participants.sort(new ParticipantComparator());
//            return participants;
//        }
//
//        return null;
//    }
}
