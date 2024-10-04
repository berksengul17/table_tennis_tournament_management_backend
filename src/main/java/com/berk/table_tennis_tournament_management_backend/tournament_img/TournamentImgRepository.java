package com.berk.table_tennis_tournament_management_backend.tournament_img;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TournamentImgRepository extends JpaRepository<TournamentImg, Long> {
}
