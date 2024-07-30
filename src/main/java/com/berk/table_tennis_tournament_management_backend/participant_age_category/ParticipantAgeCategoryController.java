package com.berk.table_tennis_tournament_management_backend.participant_age_category;

import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/participant-age-category")
@AllArgsConstructor
public class ParticipantAgeCategoryController {

    private final ParticipantAgeCategoryService participantAgeCategoryService;

    @GetMapping("/get-participants")
    public List<ParticipantAgeCategoryDTO> getParticipants(@RequestParam(required = false) Integer categoryVal,
                                                           @RequestParam(required = false) Integer ageVal) {
        return participantAgeCategoryService.getParticipantAgeCategory(categoryVal, ageVal);
    }

    @PutMapping("/update-participant/{id}")
    public ResponseEntity<String> updateParticipant(@PathVariable Long id,
                                            @RequestBody ParticipantAgeCategoryDTO participantAgeCategoryDTO) {
        try {
            return ResponseEntity.ok(participantAgeCategoryService
                    .updateParticipant(id, participantAgeCategoryDTO));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }
}
