package com.berk.table_tennis_tournament_management_backend.participant;

import com.berk.table_tennis_tournament_management_backend.age_category.AGE;
import com.berk.table_tennis_tournament_management_backend.age_category.AGE_CATEGORY;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ParticipantRepository extends JpaRepository<Participant, Long> {
    Participant findByFirstNameAndLastName(String firstName, String lastName);
    List<Participant> findByGender(GENDER gender);
    @Query("SELECT p " +
            "FROM Participant p " +
            "WHERE lower(concat(trim(p.firstName), ' ', trim(p.lastName))) = lower(:fullName)")
    Participant findByFullName(String fullName);
}
