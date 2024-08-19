package com.berk.table_tennis_tournament_management_backend.match;

import com.berk.table_tennis_tournament_management_backend.group.Group;
import com.berk.table_tennis_tournament_management_backend.participant.Participant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MatchRepository extends JpaRepository<Match, Long> {
    List<Match> findAllByGroup(Group group);
    @Query("select m from Match m where m.p1 = :participant or m.p2 = :participant")
    List<Match> findAllByParticipant(Participant participant);
    Match findByP1AndP2(Participant p1, Participant p2);
}
