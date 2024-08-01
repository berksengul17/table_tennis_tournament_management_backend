package com.berk.table_tennis_tournament_management_backend.rating;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RatingRepository extends JpaRepository<Rating, Long> {
    Rating findByParticipantName(String name);
}
