package com.berk.table_tennis_tournament_management_backend.age_category;

import java.util.Arrays;
import java.util.Objects;

public enum AGE {
    THIRTY_TO_THIRTY_NINE(0, "30-39"),
    THIRTY_TO_FORTY_NINE(1, "30-49"),
    FORTY_TO_FORTY_NINE(2, "40-49"),
    FIFTY_PLUS(3, "50+"),
    FIFTY_TO_FIFTY_NINE (4, "50-59"),
    SIXTY_PLUS(5, "60+"),
    SIXTY_TO_SIXTY_FOUR(6, "60-64"),
    SIXTY_FIVE_TO_SIXTY_NINE(7, "65-69"),
    SEVENTY_PLUS(8, "70+"),
    SEVENTY_TO_SEVENTY_FOUR(9, "70-74"),
    SEVENTY_FIVE_PLUS(10, "75+");

    private final int value;
    public final String age;

    AGE(int value, String age) {
        this.value = value;
        this.age = age;
    }

    public static AGE valueOf(int value) {
        return Arrays.stream(values())
                .filter(age -> age.value == value)
                .findFirst()
                .orElse(null);
    }

    public static AGE getByAge(String age) {
        return Arrays.stream(values())
                .filter(a -> Objects.equals(a.age, age))
                .findFirst()
                .orElse(null);
    }
}