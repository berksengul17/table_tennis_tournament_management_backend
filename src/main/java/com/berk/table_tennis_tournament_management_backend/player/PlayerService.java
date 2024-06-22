package com.berk.table_tennis_tournament_management_backend.player;

import org.springframework.stereotype.Service;

@Service
public class PlayerService {

    private final PlayerRepository playerRepository;

    public PlayerService(PlayerRepository playerRepository) {
        this.playerRepository = playerRepository;
    }

    public Player register(Player player) {
        return playerRepository.save(player);
    }
}
