package com.berk.table_tennis_tournament_management_backend.seed_participant;

import com.berk.table_tennis_tournament_management_backend.participant.Participant;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/seed-participant")
@AllArgsConstructor
public class SeedParticipantController {

    private final SeedParticipantRepository seedParticipantRepository;

    @GetMapping("/{id}")
    public List<SeedParticipant> getById(@PathVariable Long id) {
        return seedParticipantRepository.findAllBySeedId(id);
    }
}
