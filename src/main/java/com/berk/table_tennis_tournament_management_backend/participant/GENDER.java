package com.berk.table_tennis_tournament_management_backend.participant;

import java.util.Arrays;

public enum GENDER {
    MALE(0, "Erkek"),
    FEMALE(1, "Kadın");

    public final int value;
    public final String label;

    GENDER(int value, String label) {
        this.value = value;
        this.label = label;
    }

    public static GENDER valueOf(int value) {
        return Arrays.stream(values())
                .filter(g -> g.value == value)
                .findFirst()
                .orElse(null);
    }

    public static GENDER getByLabel(String label) {
        return Arrays.stream(values())
                .filter(g -> g.label.equals(label))
                .findFirst()
                .orElse(null);
    }
}
