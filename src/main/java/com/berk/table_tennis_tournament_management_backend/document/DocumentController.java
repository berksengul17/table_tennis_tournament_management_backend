package com.berk.table_tennis_tournament_management_backend.document;

import com.berk.table_tennis_tournament_management_backend.age_category.AgeCategoryRepository;
import com.berk.table_tennis_tournament_management_backend.participant_age_category.ParticipantAgeCategoryRepository;
import com.itextpdf.text.*;
import lombok.AllArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@AllArgsConstructor
@RestController
@RequestMapping("/api/document")
public class DocumentController {

    private final ParticipantAgeCategoryRepository participantAgeCategoryRepository;
    private final AgeCategoryRepository ageCategoryRepository;
    private final DocumentService documentService;

    @GetMapping("/download-age-categories")
    public ResponseEntity<?> downloadAgeCategoriesPdf() {
        try {
            byte[] eligibleStudentsPdf = documentService.createAgeCategoriesPdf();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDisposition(
                    ContentDisposition.builder("attachment")
                            .filename("yas_gruplari.pdf")
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

    @GetMapping("/download-groups/{category}/{age}")
    public ResponseEntity<?> downloadGroupsPdf(@PathVariable int category,
                                               @PathVariable int age) {
        try {
            byte[] eligibleStudentsPdf = documentService.createGroupsPdf(category, age);
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDisposition(
                    ContentDisposition.builder("attachment")
                            .filename("gruplar.pdf")
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


}
