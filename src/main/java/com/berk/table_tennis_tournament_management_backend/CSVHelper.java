package com.berk.table_tennis_tournament_management_backend;

import com.berk.table_tennis_tournament_management_backend.age_category.AgeCategory;
import com.berk.table_tennis_tournament_management_backend.participant.Participant;
import com.berk.table_tennis_tournament_management_backend.participant_age_category.ParticipantAgeCategory;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class CSVHelper {
    private static final String path = "data.csv";

    public static List<String[]> readFile() {
        List<String[]> data = new ArrayList<String[]>();
        BufferedReader br = null;
        String line = "";

        try {
            br = new BufferedReader(new FileReader(path));
            while ((line = br.readLine()) != null) {
                String[] row = line.split(",");
                data.add(new String[] {row[0], row[1], row[2],
                        row[3], row[4], row[5],
                        row[6], row[7], row[8]});
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return data;
    }

    public static void addLine(ParticipantAgeCategory participantAgeCategory) {
        try(BufferedWriter bw = new BufferedWriter(new FileWriter(path, true))) {
            bw.write(createLine(participantAgeCategory));
            bw.newLine();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void editLine(ParticipantAgeCategory participantAgeCategory) {
        List<String> newContent = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(path))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] columns = line.split(",");
                String id = columns[columns.length - 3].trim(); // Get the ID column
                if (id.equals(participantAgeCategory.getParticipant().getId().toString())) {
                    newContent.add(createLine(participantAgeCategory)); // Add the updated line
                } else {
                    newContent.add(line); // Add the original line
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(path))) {
            for (String line : newContent) {
                bw.write(line);
                bw.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void deleteLine(long participantId) {
        List<String> newContent = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(path))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] columns = line.split(",");
                String id = columns[columns.length - 3].trim(); // Get the ID column
                if (!id.equals(String.valueOf(participantId))) {
                    newContent.add(line); // Add the original line
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(path))) {
            for (String line : newContent) {
                bw.write(line);
                bw.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void createDataCsv(List<String[]> data) {
        try(BufferedWriter bw = new BufferedWriter(new FileWriter(path))) {
            for (String[] line : data) {
                bw.write(String.join(",", line));
                bw.newLine();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static String createLine(ParticipantAgeCategory participantAgeCategory) {
        Participant participant = participantAgeCategory.getParticipant();
        AgeCategory ageCategory = participantAgeCategory.getAgeCategory();

        String[] data = new String[] {participant.getFullName(), participant.getEmail(),
                participant.getBirthDate().toString(), participant.getGender().label,
                participant.getCity(), participant.getPhoneNumber(), participant.getId().toString(),
                ageCategory.getAge().age, String.valueOf(participant.getRating())};

        return String.join(",", data);
    }
}
