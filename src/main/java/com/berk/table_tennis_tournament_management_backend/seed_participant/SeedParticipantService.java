package com.berk.table_tennis_tournament_management_backend.seed_participant;

import com.berk.table_tennis_tournament_management_backend.StringHelper;
import com.berk.table_tennis_tournament_management_backend.participant.Participant;
import com.berk.table_tennis_tournament_management_backend.participant.ParticipantRepository;
import lombok.AllArgsConstructor;
import org.springframework.boot.autoconfigure.data.jpa.JpaRepositoriesAutoConfiguration;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class SeedParticipantService {

    private final SeedParticipantRepository seedParticipantRepository;
    private final ParticipantRepository participantRepository;

    public List<SeedParticipant> getAllBySeedId(Long seedId) {
        return seedParticipantRepository.findAllBySeedId(seedId);
    }

    public String saveParticipantName(Long seedId, int pIndex, String participantName) {
        SeedParticipant seedParticipant =
                seedParticipantRepository.findAllBySeedId(seedId)
                        .stream()
                        .filter(sp -> sp.getPIndex() == pIndex)
                        .findFirst()
                        .orElse(null);

        if (seedParticipant == null) return "";

        Participant participant = participantRepository.findByFullName(participantName);

        if (participant == null) {
            seedParticipant.setParticipant(null);
            seedParticipantRepository.save(seedParticipant);
            return "";
        }

        seedParticipant.setParticipant(participant);
        seedParticipantRepository.save(seedParticipant);
        return StringHelper.formatName(participant.getFullName());
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
