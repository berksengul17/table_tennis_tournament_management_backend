package com.berk.table_tennis_tournament_management_backend.round;

import com.berk.table_tennis_tournament_management_backend.seed.Seed;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface RoundRepository extends JpaRepository<Round, Long> {
    @Query("SELECT r FROM Round r JOIN r.seeds s WHERE s = :seed")
    Round findBySeed(Seed seed);
}
