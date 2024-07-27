package com.berk.table_tennis_tournament_management_backend.bracket;

import com.berk.table_tennis_tournament_management_backend.age_category.AGE;
import com.berk.table_tennis_tournament_management_backend.age_category.AGE_CATEGORY;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BracketRepository extends JpaRepository<Bracket, Long> {
    Bracket findByAgeCategory_CategoryAndAgeCategory_AgeAndBracketType(AGE_CATEGORY category,
                                                                       AGE age,
                                                                       BRACKET_TYPE bracketType);
}
