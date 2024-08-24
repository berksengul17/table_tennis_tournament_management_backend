package com.berk.table_tennis_tournament_management_backend.seed_participant;

import com.berk.table_tennis_tournament_management_backend.participant.Participant;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/seed-participant")
@AllArgsConstructor
public class SeedParticipantController {

    private final SeedParticipantService seedParticipantService;

    @GetMapping("/{id}")
    public List<SeedParticipant> getById(@PathVariable Long id) {
        return seedParticipantService.getAllBySeedId(id);
    }

    @PostMapping("/save-scores")
    public void saveScores(@RequestParam Long seedId,
                                            @RequestParam int p1Score,
                                            @RequestParam int p2Score) {
        seedParticipantService.saveScores(seedId, p1Score, p2Score);
    }
}
