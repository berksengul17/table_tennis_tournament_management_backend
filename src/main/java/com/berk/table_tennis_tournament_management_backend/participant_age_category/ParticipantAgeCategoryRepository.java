package com.berk.table_tennis_tournament_management_backend.participant_age_category;

import com.berk.table_tennis_tournament_management_backend.age_category.AgeCategory;
import com.berk.table_tennis_tournament_management_backend.participant.Participant;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ParticipantAgeCategoryRepository extends JpaRepository<ParticipantAgeCategory, Long> {
    ParticipantAgeCategory findByParticipant(Participant participant);
    List<ParticipantAgeCategory> findAllByAgeCategory(AgeCategory ageCategory);
}
