package com.berk.table_tennis_tournament_management_backend.round;

public class RoundSeedResponse {
    private Long roundId;
    private Long seedId;

    public RoundSeedResponse(Long roundId, Long seedId) {
        this.roundId = roundId;
        this.seedId = seedId;
    }

    public Long getRoundId() {
        return roundId;
    }

    public Long getSeedId() {
        return seedId;
    }
}
