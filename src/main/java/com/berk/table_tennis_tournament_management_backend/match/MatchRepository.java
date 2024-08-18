package com.berk.table_tennis_tournament_management_backend.match;

import com.berk.table_tennis_tournament_management_backend.group.Group;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MatchRepository extends JpaRepository<Match, Long> {
    List<Match> findAllByGroup(Group group);
}
