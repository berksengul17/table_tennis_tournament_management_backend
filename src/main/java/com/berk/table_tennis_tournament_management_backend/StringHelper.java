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

    public static String upperCaseFirstLetter(String input) {
        String[] splitInput = input.split(" ");
        StringBuilder builder = new StringBuilder();
        for (String name : splitInput) {
            builder.append(toUpperCaseTurkish(name.substring(0, 1)))
                    .append(toLowerCaseTurkish(name.substring(1)));
            builder.append(' ');
        }

        return builder.toString();
    }
}
