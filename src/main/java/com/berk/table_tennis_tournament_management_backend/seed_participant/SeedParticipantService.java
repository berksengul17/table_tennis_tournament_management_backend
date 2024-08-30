package com.berk.table_tennis_tournament_management_backend.seed_participant;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class SeedParticipantService {

    private final SeedParticipantRepository seedParticipantRepository;

    public List<SeedParticipant> getAllBySeedId(Long seedId) {
        return seedParticipantRepository.findAllBySeedId(seedId);
    }

    public void saveScores(Long seedId, int p1Score, int p2Score) {
        List<SeedParticipant> seedParticipants = seedParticipantRepository
                .findAllBySeedId(seedId);

        for (SeedParticipant seedParticipant : seedParticipants) {
            if (seedParticipant.getPIndex() == 0)
                seedParticipant.setScore(p1Score);
            else
                seedParticipant.setScore(p2Score);

            seedParticipantRepository.save(seedParticipant);
        }
    }
}
