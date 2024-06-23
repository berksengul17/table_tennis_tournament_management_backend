package com.berk.table_tennis_tournament_management_backend.player;

import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/player")
public class PlayerController {

    private final PlayerService playerService;

    public PlayerController(PlayerService playerService) {
        this.playerService = playerService;
    }

    @PostMapping("/register")
    public Player register(@RequestBody Player newPlayer) {
        return playerService.register(newPlayer);
    }

    @GetMapping("/participants")
    public List<Player> getParticipants() {
        return playerService.getParticipants();
    }

    @GetMapping("/participants/categorize")
    public Map<Integer, List<Player>> categorizeParticipants() {
        return playerService.categorizeParticipants();
    }
}
