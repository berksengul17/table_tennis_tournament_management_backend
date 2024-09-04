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

    @PostMapping("/create-losers-bracket/{category}/{age}")
    public Bracket createLosersBracket(@PathVariable int category, @PathVariable int age) {
        return bracketService.createLosersBracket(category, age);
    }

    @PostMapping("/refresh-bracket/{bracketId}")
    public Bracket refreshBracket(@PathVariable Long bracketId) {
        return bracketService.refreshBracket(bracketId);
    }

    @PostMapping("/connect-seeds")
    public RoundSeedResponse connectSeeds(@RequestParam Long firstSeedId,
                                          @RequestParam(required = false, defaultValue = "-1") Long secondSeedId) {
        return bracketService.connectSeeds(firstSeedId, secondSeedId);
    }
}
