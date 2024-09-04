package com.berk.table_tennis_tournament_management_backend.round;

public class RoundSeedResponse {
    private Long roundId;
    private Long seedId;
    private Long prevSeedId;

    public RoundSeedResponse(Long roundId, Long seedId, Long prevSeedId) {
        this.roundId = roundId;
        this.seedId = seedId;
        this.prevSeedId = prevSeedId;
    }

    public Long getRoundId() {
        return roundId;
    }

    public Long getSeedId() {
        return seedId;
    }

    public Long getPrevSeedId() {
        return prevSeedId;
    }
}
