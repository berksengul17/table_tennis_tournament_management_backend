package com.berk.table_tennis_tournament_management_backend.match;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/match")
@AllArgsConstructor
public class MatchController {

    private final MatchService matchService;

    @GetMapping("/{category}/{age}")
    public List<List<Match>> getAllMatches(@PathVariable int category,
                                           @PathVariable int age) {
        return matchService.getMatches(category, age);
    }

    @GetMapping("/create/{category}/{age}")
    public List<List<Match>> createMatches(@PathVariable int category,
                                           @PathVariable int age) {
        return matchService.createMatches(category, age);
    }
}
