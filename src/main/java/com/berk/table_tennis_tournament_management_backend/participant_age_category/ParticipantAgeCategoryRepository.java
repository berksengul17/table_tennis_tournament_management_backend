package com.berk.table_tennis_tournament_management_backend.participant_age_category;

import com.berk.table_tennis_tournament_management_backend.age_category.AgeCategory;
import com.berk.table_tennis_tournament_management_backend.participant.Participant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ParticipantAgeCategoryRepository extends JpaRepository<ParticipantAgeCategory, Long> {
    @Query("select pa " +
            "from ParticipantAgeCategory pa " +
            "where pa.participant = :participant and pa.pairName = ''")
    ParticipantAgeCategory findSingleByParticipant(Participant participant);
    @Query("select pa " +
            "from ParticipantAgeCategory pa " +
            "where pa.participant = :participant and pa.pairName <> ''")
    ParticipantAgeCategory findDoubleByParticipant(Participant participant);
    List<ParticipantAgeCategory> findAllByAgeCategory(AgeCategory ageCategory);
    ParticipantAgeCategory findByAgeCategoryAndParticipant(AgeCategory ageCategory, Participant participant);

    @Query("select pa " +
            "from ParticipantAgeCategory pa " +
            "where lower(pa.pairName) = lower(:pairName)")
    ParticipantAgeCategory findByPairName(String pairName);
}
