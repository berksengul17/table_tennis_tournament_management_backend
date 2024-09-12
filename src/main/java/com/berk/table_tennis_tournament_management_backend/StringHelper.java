package com.berk.table_tennis_tournament_management_backend;

import com.berk.table_tennis_tournament_management_backend.participant.Participant;

import java.util.Arrays;
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

    public static String formatName(Participant participant) {
        String[] names = (participant.getFirstName().trim() + " " +
                participant.getLastName().trim()).split(" ");
        return formatNameArray(names);
    }


    public static String formatName(String name) {
        String[] names = Arrays.stream(name.trim().split(" "))
                .filter(nameItem -> !nameItem.isEmpty()).toArray(String[]::new);
        return formatNameArray(names);
    }

    private static String formatNameArray(String[] names) {
        return String.join(" ",
                Arrays.stream(names)
                        .map(nameItem -> StringHelper.toUpperCaseTurkish(nameItem.substring(0, 1)) +
                                nameItem.substring(1)).toList());
    }
}
