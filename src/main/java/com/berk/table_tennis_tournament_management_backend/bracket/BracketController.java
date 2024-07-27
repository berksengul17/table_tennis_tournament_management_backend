package com.berk.table_tennis_tournament_management_backend.bracket;

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
}
