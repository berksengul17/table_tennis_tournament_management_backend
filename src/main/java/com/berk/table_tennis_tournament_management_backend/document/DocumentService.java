package com.berk.table_tennis_tournament_management_backend.document;

import com.berk.table_tennis_tournament_management_backend.StringHelper;
import com.berk.table_tennis_tournament_management_backend.age_category.AGE;
import com.berk.table_tennis_tournament_management_backend.age_category.AGE_CATEGORY;
import com.berk.table_tennis_tournament_management_backend.age_category.AgeCategoryRepository;
import com.berk.table_tennis_tournament_management_backend.group.Group;
import com.berk.table_tennis_tournament_management_backend.group.GroupRepository;
import com.berk.table_tennis_tournament_management_backend.participant.Participant;
import com.berk.table_tennis_tournament_management_backend.participant.ParticipantComparator;
import com.berk.table_tennis_tournament_management_backend.participant_age_category.ParticipantAgeCategory;
import com.berk.table_tennis_tournament_management_backend.participant_age_category.ParticipantAgeCategoryRepository;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import lombok.AllArgsConstructor;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

@Service
@AllArgsConstructor
public class DocumentService {

    private final ParticipantAgeCategoryRepository participantAgeCategoryRepository;
    private final AgeCategoryRepository ageCategoryRepository;
    private final GroupRepository groupRepository;

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
            for (AGE age : category.ageList) {
                List<ParticipantAgeCategory> participantAgeCategories =
                        participantAgeCategoryRepository.findAllByAgeCategory(
                                ageCategoryRepository.findByAgeAndCategory(age, category));

                Paragraph para = new Paragraph(category.label + " " + age.age + ":",
                        new Font(baseFont, 16));

                PdfPTable table = new PdfPTable(8);
                table.setSpacingAfter(10);
                table.setSpacingBefore(10);
                table.setWidthPercentage(100);

                if (participantAgeCategories.isEmpty()) continue;

                addTableHeader(table, font);
                addRows(table, participantAgeCategories, font);

                document.add(para);
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

        Paragraph para = new Paragraph(categoryEnum.label + " " + ageEnum.age + ":",
                new Font(baseFont, 16));



        for (Group group : groups) {
            PdfPTable table = new PdfPTable(2);
            table.setSpacingAfter(10);
            table.setSpacingBefore(10);
            addTableHeaderGroup(table, font);
            List<Participant> participants = group.getParticipants();
            participants.sort(new ParticipantComparator());
            addRowsGroup(document, table, participants, font);
            document.add(para);
            document.add(table);
        }

        document.close();

        return byteArrayOutputStream.toByteArray();
    }

    private void addTableHeader(PdfPTable table, Font font) {
        Stream.of("Sıra No.", "Ad-Soyad", "E-mail", "Cinsiyet", "Doğum Tarihi",
                        "Telefon Numarası", "Katıldığı Şehir", "Puan")
                .forEach(columnTitle -> {
                    PdfPCell header = new PdfPCell();
                    header.setBackgroundColor(BaseColor.LIGHT_GRAY);
                    header.setBorderWidth(1);
                    header.setPhrase(new Phrase(columnTitle, font));
                    table.addCell(header);
                });
    }

    private void addRows(PdfPTable table, List<ParticipantAgeCategory> participants, Font font) {
        for (int i=0; i<participants.size(); i++) {
            ParticipantAgeCategory participantAgeCategory = participants.get(i);
            Participant participant = participantAgeCategory.getParticipant();
            String[] names = (participant.getFirstName() + " " + participant.getLastName()).split(" ");
            String fullName = String.join(" ",
                    Arrays.stream(names)
                            .map(name -> StringHelper.toUpperCaseTurkish(name.substring(0, 1)) +
                                    name.substring(1)).toList());
            table.addCell(new Phrase(String.valueOf(i + 1), font));
            table.addCell(new Phrase(fullName, font));
            table.addCell(new Phrase(participant.getEmail(), font));
            table.addCell(new Phrase(participant.getGender().label, font));
            table.addCell(new Phrase(participant.getBirthDate().toString(), font));
            table.addCell(new Phrase(participant.getPhoneNumber(), font));
            table.addCell(new Phrase(participant.getCity(), font));
            table.addCell(new Phrase(String.valueOf(participant.getRating()), font));
        }
    }

    private void addTableHeaderGroup(PdfPTable table, Font font) {
        Stream.of("Sıra No.", "Ad-Soyad")
                .forEach(columnTitle -> {
                    PdfPCell header = new PdfPCell();
                    header.setBackgroundColor(BaseColor.LIGHT_GRAY);
                    header.setBorderWidth(1);
                    header.setPhrase(new Phrase(columnTitle, font));
                    table.addCell(header);
                });
    }

    private void addRowsGroup(Document document, PdfPTable table, List<Participant> participants, Font font) throws DocumentException {
        for (int i = 0; i < participants.size(); i++) {
            Participant participant = participants.get(i);
            String[] names = (participant.getFirstName() + " " + participant.getLastName()).split(" ");
            String fullName = String.join(" ",
                    Arrays.stream(names)
                            .map(name -> StringHelper.toUpperCaseTurkish(name.substring(0, 1)) +
                                    name.substring(1)).toList());

            // Add Sıra No. (Row Number)
            PdfPCell cell1 = new PdfPCell(new Phrase(String.valueOf(i + 1), font));
            table.addCell(cell1);

            // Add Ad-Soyad (Full Name)
            PdfPCell cell2 = new PdfPCell(new Phrase(fullName, font));
            table.addCell(cell2);
        }
        document.add(new Phrase("\n"));

    }

}
