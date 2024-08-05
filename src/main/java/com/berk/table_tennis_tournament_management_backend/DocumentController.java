package com.berk.table_tennis_tournament_management_backend;

import com.berk.table_tennis_tournament_management_backend.age_category.AGE;
import com.berk.table_tennis_tournament_management_backend.age_category.AGE_CATEGORY;
import com.berk.table_tennis_tournament_management_backend.age_category.AgeCategoryRepository;
import com.berk.table_tennis_tournament_management_backend.participant.GENDER;
import com.berk.table_tennis_tournament_management_backend.participant.Participant;
import com.berk.table_tennis_tournament_management_backend.participant.ParticipantRepository;
import com.berk.table_tennis_tournament_management_backend.participant_age_category.ParticipantAgeCategory;
import com.berk.table_tennis_tournament_management_backend.participant_age_category.ParticipantAgeCategoryRepository;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import lombok.AllArgsConstructor;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

@AllArgsConstructor
@RestController
@RequestMapping("/api/document")
public class DocumentController {

    private final ParticipantAgeCategoryRepository participantAgeCategoryRepository;
    private final AgeCategoryRepository ageCategoryRepository;

    @GetMapping("/download-age-categories")
    public ResponseEntity<?> downloadAgeCategoriesPdf() {
        try {
            byte[] eligibleStudentsPdf = createAgeCategoriesPdf();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDisposition(
                    ContentDisposition.builder("attachment")
                            .filename("yaş_grupları.pdf")
                            .build());
            return new ResponseEntity<>(eligibleStudentsPdf, headers, HttpStatus.OK);
        } catch (DocumentException e ) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Document error:" + e.getMessage());
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error while creating file:" + e.getMessage());
        }
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
            for (AGE age : category.ageList) {
                List<ParticipantAgeCategory> participantAgeCategories =
                        participantAgeCategoryRepository.findAllByAgeCategory(
                                ageCategoryRepository.findByAgeAndCategory(age, category));

                Paragraph para = new Paragraph(category.label + " " + age.age + ":",
                        new Font(baseFont, 16));

                PdfPTable table = new PdfPTable(7);
                table.setSpacingAfter(10);
                table.setSpacingBefore(10);
                table.setWidthPercentage(100);

                addTableHeader(table, font);
                addRows(table, participantAgeCategories, font);

                document.add(para);
                document.add(table);
            }
        }

        document.close();

        return byteArrayOutputStream.toByteArray();
    }

    private void addTableHeader(PdfPTable table, Font font) {
        Stream.of("Ad-Soyad", "E-mail", "Cinsiyet", "Doğum Tarihi",
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
        for (ParticipantAgeCategory participantAgeCategory :participants) {
            Participant participant = participantAgeCategory.getParticipant();
            String[] names = (participant.getFirstName() + " " + participant.getLastName()).split(" ");
            String fullName = String.join(" ",
                    Arrays.stream(names)
                            .map(name -> StringHelper.toUpperCaseTurkish(name.substring(0, 1)) +
                                    name.substring(1)).toList());
            table.addCell(new Phrase(fullName, font));
            table.addCell(new Phrase(participant.getEmail(), font));
            table.addCell(new Phrase(participant.getGender().label, font));
            table.addCell(new Phrase(participant.getBirthDate().toString(), font));
            table.addCell(new Phrase(participant.getPhoneNumber(), font));
            table.addCell(new Phrase(participant.getCity(), font));
            table.addCell(new Phrase(String.valueOf(participant.getRating()), font));
        }
    }
}
