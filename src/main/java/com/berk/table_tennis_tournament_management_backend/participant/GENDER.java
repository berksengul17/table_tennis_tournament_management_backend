package com.berk.table_tennis_tournament_management_backend.participant;

import java.util.Arrays;

public enum GENDER {
    MALE(0),
    FEMALE(1);

    public final int value;

    GENDER(int value) {
        this.value = value;
    }

    public static GENDER valueOf(int value) {
        return Arrays.stream(values())
                .filter(g -> g.value == value)
                .findFirst()
                .orElse(null);
    }
}
