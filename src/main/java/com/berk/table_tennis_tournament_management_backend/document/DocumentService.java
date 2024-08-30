package com.berk.table_tennis_tournament_management_backend.document;

import com.berk.table_tennis_tournament_management_backend.StringHelper;
import com.berk.table_tennis_tournament_management_backend.age_category.AGE;
import com.berk.table_tennis_tournament_management_backend.age_category.AGE_CATEGORY;
import com.berk.table_tennis_tournament_management_backend.age_category.AgeCategoryRepository;
import com.berk.table_tennis_tournament_management_backend.age_category.AgeCategoryService;
import com.berk.table_tennis_tournament_management_backend.group.Group;
import com.berk.table_tennis_tournament_management_backend.group.GroupRepository;
import com.berk.table_tennis_tournament_management_backend.group_table_time.GroupTableTime;
import com.berk.table_tennis_tournament_management_backend.group_table_time.GroupTableTimeRepository;
import com.berk.table_tennis_tournament_management_backend.match.Match;
import com.berk.table_tennis_tournament_management_backend.match.MatchService;
import com.berk.table_tennis_tournament_management_backend.participant.Participant;
import com.berk.table_tennis_tournament_management_backend.participant.ParticipantComparator;
import com.berk.table_tennis_tournament_management_backend.participant_age_category.ParticipantAgeCategory;
import com.berk.table_tennis_tournament_management_backend.participant_age_category.ParticipantAgeCategoryRepository;
import com.berk.table_tennis_tournament_management_backend.participant_age_category.ParticipantAgeCategoryService;
import com.berk.table_tennis_tournament_management_backend.table.Table;
import com.berk.table_tennis_tournament_management_backend.time.Time;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import lombok.AllArgsConstructor;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.Collator;
import java.util.*;
import java.util.List;
import java.util.stream.Stream;

@Service
@AllArgsConstructor
public class DocumentService {

    private final ParticipantAgeCategoryRepository participantAgeCategoryRepository;
    private final AgeCategoryRepository ageCategoryRepository;
    private final GroupRepository groupRepository;
    private final GroupTableTimeRepository groupTableTimeRepository;
    private final MatchService matchService;
    private final AgeCategoryService ageCategoryService;
    private final ParticipantAgeCategoryService participantAgeCategoryService;

    public byte[] createBracketPdf() throws DocumentException {

        Document document = new Document();
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        PdfWriter writer = PdfWriter.getInstance(document, byteArrayOutputStream);
        document.open();
        PdfContentByte canvas = writer.getDirectContent();

        // Example to draw a bracket
        float startX = 50;
        float startY = 700;
        float boxWidth = 100;
        float boxHeight = 30;
        float gap = 50;

        // Draw first round
        for (int i = 0; i < 4; i++) {
            canvas.rectangle(startX, startY - (i * (boxHeight + gap)), boxWidth, boxHeight);
            canvas.stroke();
        }

        // Draw connecting lines to the second round
        for (int i = 0; i < 2; i++) {
            float midX = startX + boxWidth + 10;
            float midY1 = startY - (i * 2 * (boxHeight + gap)) - boxHeight / 2;
            float midY2 = startY - ((i * 2 + 1) * (boxHeight + gap)) - boxHeight / 2;
            float midY = (midY1 + midY2) / 2; // Correct midpoint between the two rectangles

            canvas.moveTo(startX + boxWidth, midY1); // Start from the center of the right edge of the left box
            canvas.lineTo(midX, midY); // Go to the midpoint vertically
            canvas.lineTo(startX + boxWidth + 20, midY); // Connect to the center of the left edge of the right box
            canvas.stroke();
        }

        // Draw second round
        for (int i = 0; i < 2; i++) {
            canvas.rectangle(startX + boxWidth + 20, startY - (i * 2 * (boxHeight + gap)) - (boxHeight + gap) / 2, boxWidth, boxHeight);
            canvas.stroke();
        }

        document.close();
        System.out.println("PDF created");
        return byteArrayOutputStream.toByteArray();
    }

    public byte[] createAgeCategoriesPdf() throws IOException, DocumentException {
        Document document = new Document();
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        PdfWriter.getInstance(document, byteArrayOutputStream);

        ClassPathResource resource = new ClassPathResource("static/OpenSans-Regular.ttf");
        BaseFont baseFont = null;
        try (InputStream inputStream = resource.getInputStream()) {
            baseFont = BaseFont.createFont(
                    "OpenSans-Regular.ttf",
                    BaseFont.IDENTITY_H,
                    BaseFont.EMBEDDED,
                    false,
                    inputStream.readAllBytes(),
                    null
            );
            // Use the baseFont as needed
        } catch (IOException e) {
            e.printStackTrace();
        }

        Font font = new Font(baseFont, 12);

        document.open();

        for (AGE_CATEGORY category : AGE_CATEGORY.values()) {
            boolean isJoiningDouble = ageCategoryService.isDouble(category);
            for (AGE age : category.ageList) {
                List<ParticipantAgeCategory> participantAgeCategories =
                        participantAgeCategoryRepository.findAllByAgeCategory(
                                ageCategoryRepository.findByAgeAndCategory(age, category));

                Paragraph para = new Paragraph(category.label + " " + age.age + ":",
                        new Font(baseFont, 16));
                Paragraph countPara = new Paragraph("Katılımcı Sayısı: " +
                        participantAgeCategories.size(), new Font(baseFont, 12));

                PdfPTable table = new PdfPTable(isJoiningDouble ? 9 : 8);
                table.setSpacingAfter(10);
                table.setSpacingBefore(10);
                table.setWidthPercentage(100);

                if (participantAgeCategories.isEmpty()) continue;

                addTableHeader(table, font, isJoiningDouble);
                addRows(table, participantAgeCategories, font, isJoiningDouble);

                document.add(para);
                document.add(countPara);
                document.add(table);
            }
        }

        document.close();

        return byteArrayOutputStream.toByteArray();
    }

    public byte[] createGroupsPdf(int category, int age) throws IOException, DocumentException {
        Document document = new Document();
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        PdfWriter.getInstance(document, byteArrayOutputStream);

        ClassPathResource resource = new ClassPathResource("static/OpenSans-Regular.ttf");
        BaseFont baseFont = null;
        try (InputStream inputStream = resource.getInputStream()) {
            baseFont = BaseFont.createFont(
                    "OpenSans-Regular.ttf",
                    BaseFont.IDENTITY_H,
                    BaseFont.EMBEDDED,
                    false,
                    inputStream.readAllBytes(),
                    null
            );
            // Use the baseFont as needed
        } catch (IOException e) {
            e.printStackTrace();
        }

        Font font = new Font(baseFont, 12);

        document.open();

        AGE_CATEGORY categoryEnum = AGE_CATEGORY.valueOf(category);
        AGE ageEnum = categoryEnum.ageList.get(age);

        List<Group> groups = groupRepository
                .findByAgeCategory_CategoryAndAgeCategory_Age(categoryEnum, ageEnum);

        Paragraph para = new Paragraph(categoryEnum.label + " " + ageEnum.age,
                new Font(baseFont, 24));
        para.setAlignment(Element.ALIGN_CENTER);

        document.add(para);

        boolean isJoiningDoubles = ageCategoryService.isDouble(categoryEnum);

        for (int i=0; i<groups.size(); i++) {
            PdfPTable table = new PdfPTable(isJoiningDoubles ? 3 : 2);
            table.setSpacingAfter(10);
            table.setSpacingBefore(10);
            addTableHeaderGroup(table, font, isJoiningDoubles);
            List<Participant> participants = groups.get(i).getParticipants();
            participants.sort(new ParticipantComparator());
            addRowsGroup(document, table, participants, font, isJoiningDoubles);
            Paragraph titleAndTable = new Paragraph();
            titleAndTable.setKeepTogether(true);
            // Create and add the title
            Paragraph title = new Paragraph("Grup " + getGroupCode(categoryEnum, ageEnum) + (i + 1) + ":",
                    new Font(baseFont, 16));
            title.setKeepTogether(true);
            titleAndTable.add(title);

            // Add the table to the wrapper
            table.setKeepTogether(true);
            titleAndTable.add(table);

            // Add the wrapper to the document
            document.add(titleAndTable);
        }

        document.close();

        return byteArrayOutputStream.toByteArray();
    }

    public byte[] createGroupTableTimePdf(int category, int age, boolean createEmpty)
            throws IOException, DocumentException {
        Document document = new Document();
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        PdfWriter.getInstance(document, byteArrayOutputStream);

        ClassPathResource resource = new ClassPathResource("static/OpenSans-Regular.ttf");
        BaseFont baseFont = null;
        try (InputStream inputStream = resource.getInputStream()) {
            baseFont = BaseFont.createFont(
                    "OpenSans-Regular.ttf",
                    BaseFont.IDENTITY_H,
                    BaseFont.EMBEDDED,
                    false,
                    inputStream.readAllBytes(),
                    null
            );
            // Use the baseFont as needed
        } catch (IOException e) {
            e.printStackTrace();
        }

        Font font = new Font(baseFont, 12);

        document.open();

        AGE_CATEGORY categoryEnum = AGE_CATEGORY.valueOf(category);
        AGE ageEnum = categoryEnum.ageList.get(age);

        List<Group> groups = groupRepository
                .findByAgeCategory_CategoryAndAgeCategory_Age(categoryEnum, ageEnum);

        Paragraph para = new Paragraph(categoryEnum.label + " " + ageEnum.age,
                new Font(baseFont, 24));
        para.setAlignment(Element.ALIGN_CENTER);

        document.add(para);

        for (int i=0; i<groups.size(); i++) {
            PdfPTable table = new PdfPTable(9);
            table.setWidths(new float[] { 8, 1, 1, 1, 1, 1, 1, 1, 1 });
            Group group = groups.get(i);
            List<Participant> participants = group.getParticipants();
            participants.sort(new ParticipantComparator());
            addTableHeaderGroupTableTime(table,
                    font,
                    groupTableTimeRepository.findByGroup(group),
                    i + 1
                    );
            addRowsGroupTableTime(table, participants, createEmpty, font);
            Paragraph titleAndTable = new Paragraph();
            titleAndTable.setSpacingAfter(10);
            titleAndTable.setSpacingBefore(10);
            titleAndTable.setKeepTogether(true);
            // Create and add the title
            Paragraph title = new Paragraph("Grup " + getGroupCode(categoryEnum, ageEnum) + (i + 1) + ":",
                    new Font(baseFont, 16));
            title.setKeepTogether(true);
            titleAndTable.add(title);

            // Add the table to the wrapper
            table.setKeepTogether(true);
            titleAndTable.add(table);

            // Add the wrapper to the document
            document.add(titleAndTable);
        }

        document.close();

        return byteArrayOutputStream.toByteArray();
    }

    public byte[] createAllGroupTableTimePdf(boolean createEmpty)
            throws IOException, DocumentException{
        Document document = new Document();
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        PdfWriter.getInstance(document, byteArrayOutputStream);

        ClassPathResource resource = new ClassPathResource("static/OpenSans-Regular.ttf");
        BaseFont baseFont = null;
        try (InputStream inputStream = resource.getInputStream()) {
            baseFont = BaseFont.createFont(
                    "OpenSans-Regular.ttf",
                    BaseFont.IDENTITY_H,
                    BaseFont.EMBEDDED,
                    false,
                    inputStream.readAllBytes(),
                    null
            );
            // Use the baseFont as needed
        } catch (IOException e) {
            e.printStackTrace();
        }

        Font font = new Font(baseFont, 12);

        document.open();


        for (AGE_CATEGORY category : AGE_CATEGORY.values()) {
            for (AGE age : category.ageList) {
                //if (category != AGE_CATEGORY.DOUBLE_MEN &&
                //category != AGE_CATEGORY.DOUBLE_WOMEN) continue;
                List<Group> groups = groupRepository
                        .findByAgeCategory_CategoryAndAgeCategory_Age(category, age);

                if (groups.isEmpty()) continue;

                Paragraph para = new Paragraph(category.label + " " + age.age,
                        new Font(baseFont, 18));
                para.setAlignment(Element.ALIGN_CENTER);

                document.add(para);

                for (int i=0; i<groups.size(); i++) {
                    PdfPTable table = new PdfPTable(9);
                    table.setWidths(new float[] { 8, 1, 1, 1, 1, 1, 1, 1, 1 });
                    Group group = groups.get(i);
                    List<Participant> participants = group.getParticipants();
                    participants.sort((p1, p2) -> p1.getGroupRanking() > p2.getGroupRanking() ? 1 : -1);
                    GroupTableTime gtt = groupTableTimeRepository.findByGroup(group);
                    if (gtt == null) continue;
                    addTableHeaderGroupTableTime(table,
                            font,
                            gtt,
                            i + 1
                    );
                    addRowsGroupTableTime(table, participants, createEmpty, font);
                    Paragraph titleAndTable = new Paragraph();
                    titleAndTable.setSpacingAfter(5);
                    titleAndTable.setSpacingBefore(5);
                    titleAndTable.setKeepTogether(true);
                    // Create and add the title
                    Paragraph title = new Paragraph("Grup " + getGroupCode(category, age) + (i + 1) + ":",
                            new Font(baseFont, 16));
                    title.setKeepTogether(true);
                    // Add the table to the wrapper
                    table.setKeepTogether(true);

                    titleAndTable.add(title);
                    titleAndTable.add(table);

                    List<Match> matches = matchService.getGroupMatches(group);
                    //Paragraph matchTitle = new Paragraph("Maçlar:", new Font(baseFont, 14));
                    //matchTitle.setKeepTogether(true);
                    //titleAndTable.add(matchTitle);
                    for (Match match : matches) {
                        String p1Name = StringHelper.upperCaseFirstLetter(match.getP1().getFullName());
                        String p2Name = StringHelper.upperCaseFirstLetter(match.getP2().getFullName());

                        Paragraph matchPara = new Paragraph(
                                match.getStartTime() + "-" + match.getEndTime() + ": " +
                                p1Name + " - " + p2Name, font);
                        matchPara.setKeepTogether(true);
                        titleAndTable.add(matchPara);
                    }

                    // Add the wrapper to the document
                    document.add(titleAndTable);
                }
            }
        }

        document.close();

        return byteArrayOutputStream.toByteArray();
    }

    private void addRowsGroupTableTime(PdfPTable table,
                                       List<Participant> participants,
                                       boolean createEmpty,
                                       Font font) {
        int participantCount = participants.size();

        for (int i = 0; i < 4; i++) {  // Loop over the maximum number of participants (4)
            if (i < participantCount) {
                Participant participant = participants.get(i);
                String fullName = StringHelper.formatName(participant);
                String rating = String.valueOf(participant.getRating());
                String city = participant.getCity();

                int numOfWins = 0, numOfLoses = 0, score = 0;
                if (!createEmpty) {
                    numOfWins = matchService.calculateNumOfWins(participant);
                    numOfLoses = matchService.calculateNumOfLoses(participant);
                    score = matchService.calculateScore(numOfWins, numOfLoses);
                }

                String name = fullName + "(" + city + ")" + " - " + rating;
                //if (participant.getPair() != null && !participant.getPair().isEmpty()) {
                //    name = fullName + " - " + participant.getPair();
                //}

                PdfPCell nameCell = new PdfPCell(
                        new Phrase(name, font));
                table.addCell(nameCell);

                for (int j = 0; j < 4; j++) {
                    PdfPCell cell;
                    if (i == j) {
                        cell = new PdfPCell();
                        cell.setBackgroundColor(BaseColor.BLACK); // This shades the diagonal cells
                    } else if (j < participantCount) {
                        Participant p2 = participants.get(j);
                        Match match = matchService.getMatchBetweenP1AndP2(participant, p2);
                        String result = "";
                        if (!createEmpty) {
                            if (i > j) result = match.getP2Score() + "-" + match.getP1Score();
                            else result = match.getP1Score() + "-" + match.getP2Score();
                        }
                        cell = new PdfPCell(new Phrase(result, font));
                        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    } else {
                        cell = new PdfPCell(new Phrase("-", font));
                        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    }
                    table.addCell(cell);
                }

                // Adding G, M, P, S columns
                PdfPCell gCell = new PdfPCell(new Phrase(
                        createEmpty ? "" : String.valueOf(numOfWins),
                        font)); // Replace with actual data
                gCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                table.addCell(gCell);

                PdfPCell mCell = new PdfPCell(new Phrase(
                        createEmpty ? "" : String.valueOf(numOfLoses),
                        font)); // Replace with actual data
                mCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                table.addCell(mCell);

                PdfPCell pCell = new PdfPCell(new Phrase(
                        createEmpty ? "" : String.valueOf(score),
                        font)); // Replace with actual data
                pCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                table.addCell(pCell);

                // TODO BUNU NEREYE KOYABİLİRİM?
                List<Participant> groupParticipants = participant.getGroup().getParticipants();
                List<Integer> scores = new ArrayList<>();
                for (Participant groupParticipant : groupParticipants) {
                    scores.add(matchService.calculateScore(groupParticipant));
                }

                Collections.sort(scores, Collections.reverseOrder());
                int index = scores.indexOf(score);

                PdfPCell sCell = new PdfPCell(new Phrase(
                        createEmpty ? "" : String.valueOf(index + 1), font)); // Replace with actual data
                sCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                table.addCell(sCell);

            } else {
                // Add empty row with "-" for participants and match results
                PdfPCell emptyNameCell = new PdfPCell(new Phrase("-", font));
                emptyNameCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                table.addCell(emptyNameCell);

                for (int j = 0; j < 4; j++) {
                    PdfPCell emptyCell;
                    if (i == j) {
                        emptyCell = new PdfPCell();
                        emptyCell.setBackgroundColor(BaseColor.BLACK); // Black cell for "4-4" position
                    } else {
                        emptyCell = new PdfPCell(new Phrase("-", font));
                        emptyCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    }
                    table.addCell(emptyCell);
                }

                // Empty G, M, P, S columns
                for (int k = 0; k < 4; k++) {
                    PdfPCell emptyStatCell = new PdfPCell(new Phrase("", font));
                    emptyStatCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    table.addCell(emptyStatCell);
                }
            }
        }
    }

    private void addTableHeaderGroupTableTime(PdfPTable table,
                                          Font font,
                                          GroupTableTime groupTableTime,
                                          int groupOrder) {
        Time time = groupTableTime.getTableTime().getTime();
        Table groupTable = groupTableTime.getTableTime().getTable();
        String title = "Grup " + groupOrder +
                "    " + time.getStartTime() + "-" + time.getEndTime() + "    " +
                groupTable.getName();

        Stream.of( title, "1", "2", "3", "4", "G", "M", "P", "S")
                .forEach(columnTitle -> {
                    PdfPCell header = new PdfPCell();
                    header.setBorder(Rectangle.NO_BORDER);
                    header.setHorizontalAlignment(Element.ALIGN_CENTER);
                    header.setPhrase(new Phrase(columnTitle, font));
                    table.addCell(header);
                });
    }

    private void addTableHeader(PdfPTable table, Font font, boolean isJoiningDouble) {
        Stream.of("Sıra No.", "Ad-Soyad", isJoiningDouble ? "Eşi" : null,
                        "E-mail", "Cinsiyet", "Doğum Tarihi",
                        "Telefon Numarası", "Katıldığı Şehir", "Puan")
                .forEach(columnTitle -> {
                    PdfPCell header = new PdfPCell();
                    header.setBackgroundColor(BaseColor.LIGHT_GRAY);
                    header.setBorderWidth(1);
                    header.setPhrase(new Phrase(columnTitle, font));
                    table.addCell(header);
                });
    }

    private void addRows(PdfPTable table, List<ParticipantAgeCategory> participants,
                         Font font, boolean isJoiningDouble) {
        Collator collator = Collator.getInstance(new Locale("tr", "TR"));
        participants.sort(Comparator.comparing(p -> p.getParticipant().getFullName(), collator::compare));
        int rowCount = 1;
        for (ParticipantAgeCategory participantAgeCategory : participants) {
            Participant participant = participantAgeCategory.getParticipant();
            String fullName = StringHelper.formatName(participant);
            table.addCell(new Phrase(String.valueOf(rowCount), font));
            table.addCell(new Phrase(fullName, font));
            if (isJoiningDouble) {
                table.addCell(new Phrase(
                        StringHelper.formatName(participantAgeCategory.getPairName()), font));
            }
            table.addCell(new Phrase(participant.getEmail(), font));
            table.addCell(new Phrase(participant.getGender().label, font));
            table.addCell(new Phrase(participant.getBirthDate().toString(), font));
            table.addCell(new Phrase(participant.getPhoneNumber(), font));
            table.addCell(new Phrase(participant.getCity(), font));
            table.addCell(new Phrase(String.valueOf(participant.getRating()), font));
            rowCount++;
        }
    }

    private void addTableHeaderGroup(PdfPTable table, Font font, boolean isDouble) {
        Stream.of("Sıra No.", "Ad-Soyad", isDouble ? "Eşi" : null)
                .forEach(columnTitle -> {
                    PdfPCell header = new PdfPCell();
                    header.setBackgroundColor(BaseColor.LIGHT_GRAY);
                    header.setBorderWidth(1);
                    header.setPhrase(new Phrase(columnTitle, font));
                    table.addCell(header);
                });
    }

    private void addRowsGroup(Document document, PdfPTable table, List<Participant> participants,
                              Font font, boolean isJoiningDoubles) throws DocumentException {
        participants.sort((p1, p2) -> p1.getGroupRanking() > p2.getGroupRanking() ? 1 : -1);
        for (int i = 0; i < participants.size(); i++) {
            Participant participant = participants.get(i);
            ParticipantAgeCategory participantAgeCategory = null;
            if (isJoiningDoubles) {
                participantAgeCategory =
                        participantAgeCategoryRepository.findByParticipant(participant);
            }
            String fullName = StringHelper.formatName(participant);
            // Add Sıra No. (Row Number)
            PdfPCell cell1 = new PdfPCell(new Phrase(String.valueOf(i + 1), font));
            table.addCell(cell1);

            // Add Ad-Soyad (Full Name)
            PdfPCell cell2 = new PdfPCell(new Phrase(fullName, font));
            table.addCell(cell2);

            if (participantAgeCategory != null) {
                PdfPCell cell3 = new PdfPCell(new Phrase(participantAgeCategory.getPairName(), font));
                table.addCell(cell3);
            }
        }
        document.add(new Phrase("\n"));

    }

    private String getGroupCode(AGE_CATEGORY category, AGE age) {
        if (category == AGE_CATEGORY.SINGLE_MEN) {
            switch(age) {
                case FIFTY_TO_FIFTY_NINE:
                    return "A";
                case FORTY_TO_FORTY_NINE:
                    return "B";
                case SIXTY_TO_SIXTY_FOUR:
                    return "C";
                case THIRTY_TO_THIRTY_NINE:
                    return "D";
                case SEVENTY_TO_SEVENTY_FOUR:
                    return "E";
                case SIXTY_FIVE_TO_SIXTY_NINE:
                    return "F";
            }
        } else if (category == AGE_CATEGORY.SINGLE_WOMEN) {
            switch(age) {
                case THIRTY_TO_FORTY_NINE:
                    return "G";
                case FIFTY_TO_FIFTY_NINE:
                    return "H";
                case SIXTY_PLUS:
                    return "J";
            }
        } else if (category == AGE_CATEGORY.DOUBLE_MEN) {
            switch(age) {
                case FIFTY_TO_FIFTY_NINE: return "A";
                case FORTY_TO_FORTY_NINE: return "B";
                case SEVENTY_PLUS: return "D";
                case THIRTY_TO_THIRTY_NINE: return "E";
                case SIXTY_TO_SIXTY_FOUR: return "F";
            }
        } else if (category == AGE_CATEGORY.DOUBLE_WOMEN) {
            return "K";
        }

        return "";
    }

}
