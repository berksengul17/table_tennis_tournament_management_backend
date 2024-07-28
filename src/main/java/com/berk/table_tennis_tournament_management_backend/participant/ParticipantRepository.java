package com.berk.table_tennis_tournament_management_backend.participant;

import com.berk.table_tennis_tournament_management_backend.age_category.AGE;
import com.berk.table_tennis_tournament_management_backend.age_category.AGE_CATEGORY;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ParticipantRepository extends JpaRepository<Participant, Long> {
//    List<Participant> findAllByAgeCategory_CategoryAndAgeCategory_Age(AGE_CATEGORY category, AGE age);
    Participant findByFirstNameAndLastName(String firstName, String lastName);
}
