package com.berk.table_tennis_tournament_management_backend.bracket;

import com.berk.table_tennis_tournament_management_backend.participant.Participant;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/bracket")
@AllArgsConstructor
public class BracketController {

    private final BracketService bracketService;

    @PostMapping("/create/{ageCategory}")
    public Bracket createWinnerBracket(@PathVariable int ageCategory) {
        return bracketService.createWinnersBracket(ageCategory);
    }
}
