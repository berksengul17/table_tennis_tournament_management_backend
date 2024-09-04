package com.berk.table_tennis_tournament_management_backend.bracket;

import com.berk.table_tennis_tournament_management_backend.age_category.AGE;
import com.berk.table_tennis_tournament_management_backend.age_category.AGE_CATEGORY;
import com.berk.table_tennis_tournament_management_backend.age_category.AgeCategory;
import com.berk.table_tennis_tournament_management_backend.age_category.AgeCategoryRepository;
import com.berk.table_tennis_tournament_management_backend.participant.Participant;
import com.berk.table_tennis_tournament_management_backend.participant_age_category.ParticipantAgeCategory;
import com.berk.table_tennis_tournament_management_backend.participant_age_category.ParticipantAgeCategoryRepository;
import com.berk.table_tennis_tournament_management_backend.round.Round;
import com.berk.table_tennis_tournament_management_backend.round.RoundRepository;
import com.berk.table_tennis_tournament_management_backend.round.RoundSeedResponse;
import com.berk.table_tennis_tournament_management_backend.seed.Seed;
import com.berk.table_tennis_tournament_management_backend.seed.SeedRepository;
import com.berk.table_tennis_tournament_management_backend.seed_participant.SeedParticipant;
import com.berk.table_tennis_tournament_management_backend.seed_participant.SeedParticipantRepository;
import jakarta.servlet.http.Part;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
@AllArgsConstructor
public class BracketService {

    private final BracketRepository bracketRepository;
    private final AgeCategoryRepository ageCategoryRepository;
    private final ParticipantAgeCategoryRepository participantAgeCategoryRepository;
    private final RoundRepository roundRepository;
    private final SeedRepository seedRepository;
    private final SeedParticipantRepository seedParticipantRepository;

    public Bracket getWinnersBracket(int category, int age) {
        return bracketRepository.findByAgeCategory_CategoryAndAgeCategory_AgeAndBracketType(
                AGE_CATEGORY.valueOf(category),
                AGE.valueOf(age),
                BRACKET_TYPE.WINNERS);
    }

    public Bracket getLosersBracket(int category, int age) {
        return bracketRepository.findByAgeCategory_CategoryAndAgeCategory_AgeAndBracketType(
                AGE_CATEGORY.valueOf(category),
                AGE.valueOf(age),
                BRACKET_TYPE.LOSERS);
    }

    public Bracket createWinnersBracket(int categoryVal, int ageVal) {
        return createBracket(categoryVal, ageVal, BRACKET_TYPE.WINNERS, List.of(1, 2));
    }

    public Bracket createLosersBracket(int categoryVal, int ageVal) {
        return createBracket(categoryVal, ageVal, BRACKET_TYPE.LOSERS, List.of(3, 4));
    }

    private Bracket createBracket(int categoryVal, int ageVal, BRACKET_TYPE bracketType, List<Integer> groupRankings) {
        AGE_CATEGORY category = AGE_CATEGORY.valueOf(categoryVal);
        AGE age = AGE.valueOf(ageVal);

        List<Participant> participants = participantAgeCategoryRepository
                .findAllByAgeCategory(ageCategoryRepository.findByAgeAndCategory(age, category))
                .stream()
                .map(ParticipantAgeCategory::getParticipant)
                .filter(participant -> groupRankings.contains(participant.getGroupRanking()))
                .sorted(Comparator.comparingLong(p -> p.getGroup().getId()))
                .sorted(Comparator.comparingInt(Participant::getGroupRanking))
                .toList();

        Bracket bracket = new Bracket(bracketType);
        AgeCategory ageCategory = ageCategoryRepository.findByAgeAndCategory(age, category);
        bracket.setAgeCategory(ageCategory);
        bracketRepository.save(bracket);

        Round firstRound = new Round(bracket);
        List<Seed> seeds = new ArrayList<>();
        for (int i=0; i<participants.size(); i++) {
            Seed seed = new Seed();
            seedRepository.save(seed);
            seeds.add(seed);
            SeedParticipant seedParticipant = new SeedParticipant(seed, null, 0);
            seedParticipantRepository.save(seedParticipant);
        }

        firstRound.setSeeds(seeds);
        roundRepository.save(firstRound);

        List<Round> rounds = new ArrayList<>();
        rounds.add(firstRound);
        bracket.setRounds(rounds);

        return bracketRepository.save(bracket);
    }

    public Bracket refreshBracket(Long bracketId) {
        Bracket bracket = bracketRepository.findById(bracketId).orElse(null);
        if (bracket == null) return null;

        BRACKET_TYPE type = bracket.getBracketType();
        AgeCategory category = bracket.getAgeCategory();

        List<Round> rounds = bracket.getRounds();
        List<Seed> seeds = new ArrayList<>();
        for (Round round : rounds) {
            seeds.addAll(round.getSeeds());
        }

        List<SeedParticipant> seedParticipants = new ArrayList<>();
        for (Seed seed : seeds) {
            seedParticipants.addAll(seedParticipantRepository.findAllBySeed(seed));
        }

        seedParticipantRepository.deleteAll(seedParticipants);
        roundRepository.deleteAll(rounds);
        seedRepository.deleteAll(seeds);
        bracketRepository.delete(bracket);

        return type == BRACKET_TYPE.WINNERS ? createWinnersBracket(category.getCategory().value,
                                                                    category.getAge().value) :
                                                createLosersBracket(category.getCategory().value,
                                                                    category.getAge().value);
    }

    public RoundSeedResponse connectSeeds(Long firstSeedId, Long secondSeedId) {
        Seed firstSeed = seedRepository.findById(firstSeedId).orElse(null);
        Seed secondSeed = seedRepository.findById(secondSeedId).orElse(null);
        if (firstSeed == null) return null;

        Round round = roundRepository.findBySeed(firstSeed);
        if (round == null) return null;

        Bracket bracket = round.getBracket();
        List<Round> rounds = bracket.getRounds();
        Seed prevSeed = firstSeed;
        if (secondSeed != null) {
            boolean isFirstSeedLower = firstSeedId < secondSeedId;
            if (!isFirstSeedLower) prevSeed = secondSeed;

            List<SeedParticipant> firstSeedParticipants = seedParticipantRepository.findAllBySeedId(firstSeedId);
            List<SeedParticipant> secondSeedParticipants = seedParticipantRepository.findAllBySeedId(secondSeedId);

            handleSeedParticipants(isFirstSeedLower ? secondSeedParticipants : firstSeedParticipants,
                    isFirstSeedLower ? firstSeed : secondSeed
            );

            removeSeedFromRound(round, isFirstSeedLower ? secondSeed : firstSeed);
        }

        return createNextRound(bracket, rounds, round, prevSeed);
    }

    public int getFirstRoundNumOfParticipants(Long bracketId) {
        Bracket bracket = bracketRepository.findById(bracketId).orElse(null);
        int numOfParticipants = 0;
        if (bracket == null) return numOfParticipants;

        for (Seed seed : bracket.getRounds().get(0).getSeeds()) {
            numOfParticipants += seedParticipantRepository.findAllBySeed(seed).size();
        }

        return numOfParticipants;
    }

    private void handleSeedParticipants(List<SeedParticipant> otherSeedParticipants, Seed mainSeed) {
        SeedParticipant participantToMove = otherSeedParticipants.get(0);
        participantToMove.setSeed(mainSeed);
        participantToMove.setPIndex(1);
        seedParticipantRepository.save(participantToMove);
    }

    private void removeSeedFromRound(Round round, Seed seedToRemove) {
        round.getSeeds().remove(seedToRemove);
        roundRepository.save(round);
        seedRepository.delete(seedToRemove);
    }

    private RoundSeedResponse createNextRound(Bracket bracket, List<Round> rounds,
                                              Round round, Seed prevSeed) {
        int currRoundIndex = rounds.indexOf(round);
        Round nextRound = null;
        if (currRoundIndex == rounds.size() - 1) {
            nextRound = new Round(bracket, new ArrayList<>());
        } else {
            nextRound = rounds.get(currRoundIndex + 1);
        }
        Seed newSeed = new Seed();
        seedRepository.save(newSeed);
        SeedParticipant newSeedParticipant = new SeedParticipant(newSeed, prevSeed);
        seedParticipantRepository.save(newSeedParticipant);
        nextRound.getSeeds().add(newSeed);
        roundRepository.save(nextRound);
        return new RoundSeedResponse(nextRound.getId(), newSeed.getId(), prevSeed.getId());
    }
}