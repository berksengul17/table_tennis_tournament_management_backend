package com.berk.table_tennis_tournament_management_backend.bracket;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BracketRepository extends JpaRepository<Bracket, Long> {
    Bracket findByAgeCategory_CategoryAndBracketType(int ageCategory, BRACKET_TYPE bracketType);
}
