package com.berk.table_tennis_tournament_management_backend.age_category;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AgeCategoryRepository extends JpaRepository<AgeCategory, Long> {
    AgeCategory findByAgeAndCategory(AGE age,  AGE_CATEGORY category);
}
