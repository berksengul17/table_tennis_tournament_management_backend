package com.berk.table_tennis_tournament_management_backend.seed_participant;

import com.berk.table_tennis_tournament_management_backend.participant.Participant;
import com.berk.table_tennis_tournament_management_backend.seed.Seed;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SeedParticipantRepository extends JpaRepository<SeedParticipant, Long> {
    List<SeedParticipant> findAllBySeed(Seed seed);
    List<SeedParticipant> findAllByParticipant(Participant participant);
}
