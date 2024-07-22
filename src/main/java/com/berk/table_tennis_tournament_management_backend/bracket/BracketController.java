package com.berk.table_tennis_tournament_management_backend.bracket;

import com.berk.table_tennis_tournament_management_backend.participant.Participant;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/bracket")
@AllArgsConstructor
public class BracketController {

    private final BracketService bracketService;

    @GetMapping("/get-winners-bracket/{ageCategory}")
    public Bracket getWinnersBracket(@PathVariable int ageCategory) {
        return bracketService.getWinnersBracket(ageCategory);
    }

    @PostMapping("/create-winners-bracket/{ageCategory}")
    public Bracket createWinnersBracket(@PathVariable int ageCategory) {
        return bracketService.createWinnersBracket(ageCategory);
    }

    @PutMapping("/advance-to-next-round")
    public Bracket advanceToNextRound(@RequestParam Long participantId,
                                      @RequestParam Long bracketId,
                                      @RequestParam Long roundId) {
        return bracketService.advanceToNextRound(participantId, bracketId, roundId);
    }
}
