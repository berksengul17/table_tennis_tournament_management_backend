package com.berk.table_tennis_tournament_management_backend.bracket;

import com.berk.table_tennis_tournament_management_backend.age_category.AGE;
import com.berk.table_tennis_tournament_management_backend.age_category.AGE_CATEGORY;
import com.berk.table_tennis_tournament_management_backend.age_category.AgeCategory;
import com.berk.table_tennis_tournament_management_backend.age_category.AgeCategoryRepository;
import com.berk.table_tennis_tournament_management_backend.participant.Participant;
import com.berk.table_tennis_tournament_management_backend.participant.ParticipantRepository;
import com.berk.table_tennis_tournament_management_backend.participant_age_category.ParticipantAgeCategory;
import com.berk.table_tennis_tournament_management_backend.participant_age_category.ParticipantAgeCategoryRepository;
import com.berk.table_tennis_tournament_management_backend.round.Round;
import com.berk.table_tennis_tournament_management_backend.round.RoundRepository;
import com.berk.table_tennis_tournament_management_backend.round.RoundSeedResponse;
import com.berk.table_tennis_tournament_management_backend.seed.Seed;
import com.berk.table_tennis_tournament_management_backend.seed.SeedRepository;
import com.berk.table_tennis_tournament_management_backend.seed_participant.SeedParticipant;
import com.berk.table_tennis_tournament_management_backend.seed_participant.SeedParticipantRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@AllArgsConstructor
public class BracketService {

    private final BracketRepository bracketRepository;
    private final AgeCategoryRepository ageCategoryRepository;
    private final ParticipantRepository participantRepository;
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

    public int getFirstRoundNumOfParticipants(Long bracketId) {
        Bracket bracket = bracketRepository.findById(bracketId).orElse(null);
        int numOfParticipants = 0;
        if (bracket == null) return numOfParticipants;

        for (Seed seed : bracket.getRounds().get(0).getSeeds()) {
            numOfParticipants += seedParticipantRepository.findAllBySeed(seed).size();
        }

        return numOfParticipants;
    }

    public Bracket advanceToNextRound(Long participantId, Long bracketId, Long roundId) {
        Bracket bracket = bracketRepository.findById(bracketId).orElse(null);
        Participant participant = participantRepository.findById(participantId).orElse(null);

        if (bracket == null) return null;
        if (participant == null) return null;

        Round round = roundRepository.findById(roundId).orElse(null);
        Round nextRound = roundRepository.findById(roundId + 1).orElse(null);
        if (round == null || nextRound == null) return null;

        List<Seed> seeds = round.getSeeds();

        SeedResult result = calculateSeedInfo(seeds, participant);
        int nextRoundSeedIndex = result.getNextRoundSeedIndex();
        boolean isLowerSeed = result.isLowerSeed();

        List<Seed> nextRoundSeeds = nextRound.getSeeds();
        Seed nextRoundSeed = nextRoundSeeds.get(nextRoundSeedIndex);

        List<SeedParticipant> nextRoundSeedParticipants = seedParticipantRepository
                .findAllBySeed(nextRoundSeed);

        if (nextRoundSeedParticipants.isEmpty()) {
            nextRoundSeedParticipants.add(
                    seedParticipantRepository.save(new SeedParticipant(nextRoundSeed)));
            nextRoundSeedParticipants.add(
                    seedParticipantRepository.save(new SeedParticipant(nextRoundSeed)));
        }

        if (isLowerSeed) {
            nextRoundSeedParticipants.get(0).setPIndex(0);
            SeedParticipant sp = nextRoundSeedParticipants.get(1);
            sp.setParticipant(participant);
            sp.setPIndex(1);
        } else {
            nextRoundSeedParticipants.get(1).setPIndex(1);
            SeedParticipant sp = nextRoundSeedParticipants.get(0);
            sp.setParticipant(participant);
            sp.setPIndex(0);
        }

        roundRepository.save(nextRound);
        return bracketRepository.save(bracket);
    }

    public Bracket createWinnersBracket(int categoryVal, int ageVal) {
        AGE_CATEGORY category = AGE_CATEGORY.valueOf(categoryVal);
        AGE age = AGE.valueOf(ageVal);

        List<Participant> participants = participantAgeCategoryRepository
                .findAllByAgeCategory(ageCategoryRepository.findByAgeAndCategory(age, category))
                .stream()
                .map(ParticipantAgeCategory::getParticipant)
                .filter(participant -> participant.getGroupRanking() == 1 ||
                        participant.getGroupRanking() == 2)
                .sorted(Comparator.comparingLong(p -> p.getGroup().getId()))
                .sorted(Comparator.comparingInt(Participant::getGroupRanking))
                .toList();

        Bracket bracket = new Bracket(BRACKET_TYPE.WINNERS);
        AgeCategory ageCategory = ageCategoryRepository.findByAgeAndCategory(age, category);
        bracket.setAgeCategory(ageCategory);
        bracketRepository.save(bracket);
        Round firstRound = new Round(bracket);
        List<Seed> seeds = new ArrayList<>();
        for (int i = 0; i < participants.size(); i++) {
            Seed seed = new Seed();
            seedRepository.save(seed);
            seeds.add(seed);
            SeedParticipant seedParticipant = new SeedParticipant(seed, participants.get(i), 0);
            seedParticipantRepository.save(seedParticipant);
        }

        firstRound.setSeeds(seeds);
        roundRepository.save(firstRound);

        List<Round> rounds = new ArrayList<>();
        rounds.add(firstRound);
        bracket.setRounds(rounds);

        return bracketRepository.save(bracket);
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
        Seed newSeed = new Seed(prevSeed);
        seedRepository.save(newSeed);
        SeedParticipant newSeedParticipant = new SeedParticipant(newSeed);
        seedParticipantRepository.save(newSeedParticipant);
        nextRound.getSeeds().add(newSeed);
        roundRepository.save(nextRound);
        return new RoundSeedResponse(nextRound.getId(), newSeed.getId());
    }

    private List<Seed> createSeeds(List<Seed> seeds, int participantCount,
                                   int startingIndex, List<Participant> participants) {
        int perfectParticipantSize = calculatePerfectParticipantSize(participantCount);
        int numOfByes = perfectParticipantSize - participantCount;

        calculateByes(seeds, numOfByes, startingIndex, participants);

        // TODO geri kalan boşluk nasıl doldurulcak?
        int currIndex = startingIndex == 0 ? 0 : 3;
        for (int i = 0; i < seeds.size(); i++) {
            Seed seed = seeds.get(i);
            if (isSeedEmpty(seed)) {
                // if upper bracket and the first group winner
                if (startingIndex == 0 && currIndex == 0) {
                    seedParticipantRepository.save(
                            new SeedParticipant(seed, participants.get(currIndex), 0));
                    // match the participant with the last odd numbered participant
                    int size = participants.size();
                    if (size % 2 == 0) {
                        seedParticipantRepository.save(
                                new SeedParticipant(seed, participants.get(size - 2), 1));
//                        seed.getParticipants().add(participants.get(size - 2));
                        currIndex = size - 4;
                    } else {
                        seedParticipantRepository.save(
                                new SeedParticipant(seed, participants.get(size - 1), 1));
//                        seed.getParticipants().add(participants.get(size - 1));
                        currIndex = size - 3;
                    }
                } else if (startingIndex == 1 && currIndex == 5) {
                    seedParticipantRepository.save(
                            new SeedParticipant(seed, participants.get(currIndex), 0));
                    seedParticipantRepository.save(
                            new SeedParticipant(seed, participants.get(currIndex - 4), 1));
//                    seed.setParticipants(new ArrayList<>(
//                            List.of(participants.get(currIndex), participants.get(currIndex - 4))
//                    ));
                } else if (startingIndex == 1 && currIndex == 3) {
                    seedParticipantRepository.save(
                            new SeedParticipant(seed, participants.get(currIndex), 0));
//                    seed.setParticipants(new ArrayList<>(
//                            List.of(participants.get(currIndex))
//                    ));
                    // match the participant with the last odd numbered participant
                    int size = participants.size();
                    if (size % 2 == 0) {
                        seedParticipantRepository.save(
                                new SeedParticipant(seed, participants.get(size - 1), 1));
//                        seed.getParticipants().add(participants.get(size - 1));
                        currIndex = size - 3;
                    } else {
                        seedParticipantRepository.save(
                                new SeedParticipant(seed, participants.get(size - 2), 1));
//                        seed.getParticipants().add(participants.get(size - 2));
                        currIndex = size - 4;
                    }
                } else {
                    if (currIndex - 2 > 0 && currIndex < participants.size()) {
                        seedParticipantRepository.save(
                                new SeedParticipant(seed, participants.get(currIndex), 0));
                        seedParticipantRepository.save(
                                new SeedParticipant(seed, participants.get(currIndex - 2), 1));
//                        seed.setParticipants(new ArrayList<>(
//                                List.of(participants.get(currIndex),
//                                        participants.get(currIndex - 2))));
                        currIndex -= 4;
                    }
                }

                seeds.set(i, seed);
            } else {
                int size = participants.size();
                if (currIndex == 0) {
                    currIndex = size % 2 == 0 ? size - 2 : size - 1;
                } else if (currIndex == 3) {
                    currIndex = size % 2 == 0 ? size - 1 : size - 2;
                } else if (startingIndex == 1 && i == seeds.size() - 2) {
                    currIndex = 1;
                } else {
                    currIndex -= 2;
                }
            }
        }


        return seeds;
    }

    public int getNextSeedId(Long seedId) {
        List<SeedParticipant> seedParticipants =
                seedParticipantRepository.findAllBySeedId(seedId);
        for (SeedParticipant sp : seedParticipants) {
            if (sp.getParticipant() != null) {
                return calculateSeedInfo(seedRepository.findAll(), sp.getParticipant()).getNextRoundSeedIndex();
            }
        }

        return -1;
    }

    private void calculateByes(List<Seed> seeds,
                               int numOfByes, int startingIndex,
                               List<Participant> participants) {
        int currIndex = startingIndex;
        int seedIndex = startingIndex == 0 ? 0 : seeds.size() - 1;
        for (int i = 0; i < numOfByes; i++) {
            Participant byeParticipant = participants.get(currIndex);
            Seed currSeed = seeds.get(seedIndex);
            seedParticipantRepository.save(
                    new SeedParticipant(currSeed, byeParticipant, 0));
            seedParticipantRepository.save(
                    new SeedParticipant(currSeed, null, 1));
//            currSeed.getParticipants().set(0, byeParticipant);
//            currSeed.getParticipants().set(1, null);
            seeds.set(seedIndex, currSeed);
//            seeds.set(seedIndex, new Seed(new ArrayList<>(Arrays.asList(byeParticipant, null))));

            if (currIndex == 0) {
                seedIndex = seeds.size() - 1;
            } else if (currIndex == 1) {
                seedIndex = 0;
            } else if (currIndex == 3) {
                seedIndex = seeds.size() - 2;
            } else {
                seedIndex--;
            }

            currIndex += 2;
        }
    }

    private SeedResult calculateSeedInfo(List<Seed> seeds, Participant participant) {
        int nextRoundSeedIndex = 0;
        boolean isLowerSeed = false;

        for (int i = 0; i < seeds.size(); i++) {
            List<Participant> participants = seedParticipantRepository
                    .findAllBySeed(seeds.get(i))
                    .stream().map(SeedParticipant::getParticipant).toList();
            if (participants.contains(participant)) {
                double actualResult = i / 2.0;
                nextRoundSeedIndex = (int) Math.floor(actualResult);
                if (actualResult - nextRoundSeedIndex > 0) {
                    isLowerSeed = true;
                }
                break;
            }
        }

        return new SeedResult(nextRoundSeedIndex, isLowerSeed);
    }

    private int calculatePerfectParticipantSize(int n) {
        int power = (int) Math.ceil(Math.log(n) / Math.log(2));
        return (int) Math.pow(2, power);
    }

    private boolean isSeedEmpty(Seed seed) {
        return seedParticipantRepository
                .findAllBySeed(seed)
                .stream()
                .allMatch(seedParticipant -> seedParticipant.getParticipant() == null);
    }

    private class SeedResult {
        private final int nextRoundSeedIndex;
        private final boolean isLowerSeed;

        public SeedResult(int nextRoundSeedIndex, boolean isLowerSeed) {
            this.nextRoundSeedIndex = nextRoundSeedIndex;
            this.isLowerSeed = isLowerSeed;
        }

        public int getNextRoundSeedIndex() {
            return nextRoundSeedIndex;
        }

        public boolean isLowerSeed() {
            return isLowerSeed;
        }
    }

}