package com.berk.table_tennis_tournament_management_backend.age_category;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public enum AGE_CATEGORY {
    SINGLE_MEN(0, "Tek Erkek", new ArrayList<>(Arrays.asList(AGE.THIRTY_TO_THIRTY_NINE,
            AGE.FORTY_TO_FORTY_NINE,
            AGE.FIFTY_TO_FIFTY_NINE,
            AGE.SIXTY_TO_SIXTY_FOUR,
            AGE.SIXTY_FIVE_TO_SIXTY_NINE,
            AGE.SEVENTY_TO_SEVENTY_FOUR,
            AGE.SEVENTY_FIVE_PLUS))),

    SINGLE_WOMEN(1, "Tek Kadın", new ArrayList<>(Arrays.asList(AGE.THIRTY_TO_FORTY_NINE,
            AGE.FIFTY_TO_FIFTY_NINE,
            AGE.SIXTY_PLUS))),

    DOUBLE_MEN(2, "Çift Erkek", new ArrayList<>(Arrays.asList(AGE.THIRTY_TO_THIRTY_NINE,
            AGE.FORTY_TO_FORTY_NINE,
            AGE.FIFTY_TO_FIFTY_NINE,
            AGE.SIXTY_TO_SIXTY_FOUR,
            AGE.SIXTY_FIVE_TO_SIXTY_NINE,
            AGE.SEVENTY_PLUS))),

    DOUBLE_WOMEN(3, "Çift Kadın", new ArrayList<>(Arrays.asList(AGE.THIRTY_TO_FORTY_NINE,
            AGE.FIFTY_PLUS))),

    MIX(4, "Karışık", new ArrayList<>(Arrays.asList(AGE.THIRTY_TO_FORTY_NINE,
            AGE.FIFTY_PLUS)));

    public final int value;
    public final String label;
    public final List<AGE> ageList;

    AGE_CATEGORY(int value, String label, List<AGE> ageList) {
        this.value = value;
        this.label = label;
        this.ageList = ageList;
    }

    public static AGE_CATEGORY valueOf(int value) {
        return Arrays.stream(values())
                .filter(category -> category.value == value)
                .findFirst()
                .orElse(null);
    }

    public static AGE_CATEGORY getByLabel(String label) {
        return Arrays.stream(values())
                .filter(category -> category.label.equals(label))
                .findFirst()
                .orElse(null);
    }

    public static List<AGE_CATEGORY> getMenCategoryList() {
        return Arrays.asList(SINGLE_MEN, DOUBLE_MEN, MIX);
    }
    public static List<AGE_CATEGORY> getWomenCategoryList() {
        return Arrays.asList(SINGLE_WOMEN, DOUBLE_WOMEN, MIX);
    }
    public static List<AGE_CATEGORY> getSingleCategories() {return Arrays.asList(SINGLE_MEN, SINGLE_WOMEN);}
}
