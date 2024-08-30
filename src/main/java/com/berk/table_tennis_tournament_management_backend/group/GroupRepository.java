package com.berk.table_tennis_tournament_management_backend.group;

import com.berk.table_tennis_tournament_management_backend.age_category.AGE;
import com.berk.table_tennis_tournament_management_backend.age_category.AGE_CATEGORY;
import com.berk.table_tennis_tournament_management_backend.age_category.AgeCategory;
import com.berk.table_tennis_tournament_management_backend.age_category.AgeCategoryCount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GroupRepository extends JpaRepository<Group, Long> {
    List<Group> findByAgeCategory_CategoryAndAgeCategory_Age(AGE_CATEGORY category, AGE age);
    List<Group> findAllByAgeCategory(AgeCategory ageCategory);
    @Query("select new " +
            "com.berk.table_tennis_tournament_management_backend.age_category." +
            "AgeCategoryCount(g.ageCategory, count(g.ageCategory))" +
            "from Group g " +
            "group by g.ageCategory " +
            "order by count(g.ageCategory) desc" )
    List<AgeCategoryCount> getAgeCategoryCounts();
}