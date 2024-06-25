package com.berk.table_tennis_tournament_management_backend.participant;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AgeCategoryWeight {
    private int ageCategory;
    private int numberOfParticipants;
    private int numberOfTables;
}
