package com.berk.table_tennis_tournament_management_backend;

import java.util.Locale;

public class StringHelper {

    public static String toLowerCaseTurkish(String input) {
        if (input == null) {
            return null;
        }
        return input.toLowerCase(new Locale("tr", "TR"));
    }

    public static String toUpperCaseTurkish(String input) {
        if (input == null) {
            return null;
        }
        return input.toUpperCase(new Locale("tr", "TR"));
    }
}
