package com.berk.table_tennis_tournament_management_backend.age_category;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class AgeCategoryCount {
    private AgeCategory ageCategory;
    private long count;
}
