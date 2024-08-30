package com.berk.table_tennis_tournament_management_backend.document;

import com.berk.table_tennis_tournament_management_backend.age_category.AgeCategoryRepository;
import com.berk.table_tennis_tournament_management_backend.participant_age_category.ParticipantAgeCategoryRepository;
import com.itextpdf.text.*;
import lombok.AllArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@AllArgsConstructor
@RestController
@RequestMapping("/api/document")
public class DocumentController {

    private final ParticipantAgeCategoryRepository participantAgeCategoryRepository;
    private final AgeCategoryRepository ageCategoryRepository;
    private final DocumentService documentService;

    @GetMapping("/download-bracket")
    public ResponseEntity<?> downloadBracketPdf() {
        try {
            byte[] eligibleStudentsPdf = documentService.createBracketPdf();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDisposition(
                    ContentDisposition.builder("attachment")
                            .filename("bracket.pdf")
                            .build());
            return new ResponseEntity<>(eligibleStudentsPdf, headers, HttpStatus.OK);
        } catch (DocumentException e ) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Document error:" + e.getMessage());
        }
    }
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

    @GetMapping("/download-group-table-time/{category}/{age}")
    public ResponseEntity<?> downloadGroupTableTimePdf(@PathVariable int category,
                                                       @PathVariable int age,
                                                       @RequestParam boolean createEmpty) {
        try {
            byte[] eligibleStudentsPdf = documentService.createGroupTableTimePdf(category, age, createEmpty);
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDisposition(
                    ContentDisposition.builder("attachment")
                            .filename("grup_masa_saatler.pdf")
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

    @GetMapping("/download-all-group-table-time")
    public ResponseEntity<?> downloadAllGroupTableTimePdf(@RequestParam boolean createEmpty) {
        try {
            byte[] eligibleStudentsPdf = documentService.createAllGroupTableTimePdf(createEmpty);
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDisposition(
                    ContentDisposition.builder("attachment")
                            .filename("grup_masa_saatler.pdf")
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
