package com.berk.table_tennis_tournament_management_backend.player;

import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class PlayerService {

    private final PlayerRepository playerRepository;

    public PlayerService(PlayerRepository playerRepository) {
        this.playerRepository = playerRepository;
    }

    public Player register(Player player) {
        return playerRepository.save(player);
    }

    public List<Player> getParticipants() {
        return playerRepository.findAll();
    }

    public Map<Integer, List<Player>> categorizeParticipants() {
        List<Player> participants = getParticipants();
        Map<Integer, List<Player>> categorizedParticipants = new HashMap<>();

        for (Player participant : participants) {
            int ageCategory = participant.getAgeCategory();
            List<Player> players = categorizedParticipants.get(ageCategory);
            players.add(participant);
            categorizedParticipants.put(ageCategory, players);
        }

        return categorizedParticipants;
    }
}
