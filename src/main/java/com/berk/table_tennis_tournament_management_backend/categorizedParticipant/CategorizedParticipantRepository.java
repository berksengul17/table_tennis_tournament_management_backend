package com.berk.table_tennis_tournament_management_backend.categorizedParticipant;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategorizedParticipantRepository extends JpaRepository<CategorizedParticipant, Long> {
}
