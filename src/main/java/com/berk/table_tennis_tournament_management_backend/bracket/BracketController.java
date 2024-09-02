package com.berk.table_tennis_tournament_management_backend.bracket;

import com.berk.table_tennis_tournament_management_backend.participant.Participant;
import com.berk.table_tennis_tournament_management_backend.round.RoundSeedResponse;
import com.berk.table_tennis_tournament_management_backend.seed.Seed;
import com.berk.table_tennis_tournament_management_backend.seed_participant.SeedParticipant;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/bracket")
@AllArgsConstructor
public class BracketController {

    private final BracketService bracketService;

    @GetMapping("/get-winners-bracket/{category}/{age}")
    public Bracket getWinnersBracket(@PathVariable int category, @PathVariable int age) {
        return bracketService.getWinnersBracket(category, age);
    }

    @GetMapping("/get-losers-bracket/{category}/{age}")
    public Bracket getLosersBracket(@PathVariable int category, @PathVariable int age) {
        return bracketService.getLosersBracket(category, age);
    }

    @GetMapping("/get-participant-count/{bracketId}")
    public int getParticipantCount(@PathVariable Long bracketId) {
        return bracketService.getFirstRoundNumOfParticipants(bracketId);
    }

    @PostMapping("/create-winners-bracket/{category}/{age}")
    public Bracket createWinnersBracket(@PathVariable int category, @PathVariable int age) {
        return bracketService.createWinnersBracket(category, age);
    }

    @PutMapping("/advance-to-next-round")
    public Bracket advanceToNextRound(@RequestParam Long participantId,
                                      @RequestParam Long bracketId,
                                      @RequestParam Long roundId) {
        return bracketService.advanceToNextRound(participantId, bracketId, roundId);
    }

    @GetMapping("/get-next-seed-id")
    public int getNextSeedId(@RequestParam Long seedId) {
        return bracketService.getNextSeedId(seedId);
    }

    @PostMapping("/connect-seeds")
    public RoundSeedResponse connectSeeds(@RequestParam Long firstSeedId,
                                          @RequestParam(required = false, defaultValue = "-1") Long secondSeedId) {
        return bracketService.connectSeeds(firstSeedId, secondSeedId);
    }
}
