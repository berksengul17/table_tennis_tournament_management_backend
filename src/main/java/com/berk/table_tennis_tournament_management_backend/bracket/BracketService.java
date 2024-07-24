package com.berk.table_tennis_tournament_management_backend.bracket;

import com.berk.table_tennis_tournament_management_backend.age_category.AgeCategoryRepository;
import com.berk.table_tennis_tournament_management_backend.participant.Participant;
import com.berk.table_tennis_tournament_management_backend.participant.ParticipantRepository;
import com.berk.table_tennis_tournament_management_backend.round.Round;
import com.berk.table_tennis_tournament_management_backend.round.RoundRepository;
import com.berk.table_tennis_tournament_management_backend.seed.Seed;
import com.berk.table_tennis_tournament_management_backend.seed.SeedRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@AllArgsConstructor
public class BracketService {

    private final BracketRepository bracketRepository;
    private final AgeCategoryRepository ageCategoryRepository;
    private final ParticipantRepository participantRepository;
    private final RoundRepository roundRepository;
    private final SeedRepository seedRepository;

    public Bracket getWinnersBracket(int ageCategory) {
        return bracketRepository.findByAgeCategory_CategoryAndBracketType(ageCategory, BRACKET_TYPE.WINNERS);
    }

    public Bracket getLosersBracket(int ageCategory) {
        return bracketRepository.findByAgeCategory_CategoryAndBracketType(ageCategory, BRACKET_TYPE.LOSERS);
    }

//        List<Round> rounds = bracket.getRounds();
//
//        if (rounds == null) return null;
//
//        List<Round> foundRounds = rounds
//                .stream()
//                .filter(r -> Objects.equals(r.getId(), roundId))
//                .toList();
//
//        if (foundRounds.isEmpty()) return null;
//        Round round = foundRounds.get(0);
//
//        int index = rounds.indexOf(round);
//        if (index == -1) return null;

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

        List<Participant> nextRoundSeedParticipants = nextRoundSeed.getParticipants();
        // FIXME BURA BÖYLE OLMAZ BAŞKA BİR ÇÖZÜM BUL
        // SIZE DB DE NULL PARTICIPANT_ID OLMADIĞI İÇİN 1 GELİYO OLABİLİR
        if (isLowerSeed && (nextRoundSeedParticipants.size() == 1 || nextRoundSeedParticipants.isEmpty())) {
            nextRoundSeedParticipants.add(participant);
        } else if (!isLowerSeed && nextRoundSeedParticipants.size() == 1) {
            nextRoundSeedParticipants.add(nextRoundSeedParticipants.get(0));
            nextRoundSeedParticipants.set(0, participant);
        } else if (!isLowerSeed && nextRoundSeedParticipants.isEmpty()) {
            nextRoundSeedParticipants.add(participant);
        } else {
            nextRoundSeedParticipants.set(isLowerSeed ? 1 : 0, participant);
        }

        seedRepository.save(nextRoundSeed);
        roundRepository.save(nextRound);
        return bracketRepository.save(bracket);
    }

    public Bracket createWinnersBracket(int ageCategoryId) {
        List<Participant> participants = participantRepository.findAllByAgeCategory_Category(ageCategoryId)
                .stream()
                .filter(participant -> participant.getGroupRanking() == 1
                        || participant.getGroupRanking() == 2)
                .sorted(Comparator.comparingLong(p -> p.getGroup().getId()))
                .sorted(Comparator.comparingInt(Participant::getGroupRanking))
                .toList();

        int upperBracketParticipantCount = (int) Math.ceil(participants.size() / 2.0);
        int lowerBracketParticipantCount = participants.size() - upperBracketParticipantCount;

        Bracket bracket = new Bracket(BRACKET_TYPE.WINNERS);
        bracket.setAgeCategory(ageCategoryRepository.findByCategory(ageCategoryId));
        bracketRepository.save(bracket);

        List<Round> allRounds = new ArrayList<>();
        for (int i=calculatePerfectParticipantSize(participants.size()) / 2; i>=1; i=i/2) {
            Round round = new Round(bracket);
            List<Seed> seeds = new ArrayList<>();
            for (int j = 0; j < i; j++) {
//                Collections.nCopies(2, null)
                seeds.add(new Seed(new ArrayList<>(Arrays.asList(null, null))));
            }
            round.setSeeds(seeds);
            seedRepository.saveAll(seeds);
            allRounds.add(round);
        }

        roundRepository.saveAll(allRounds);
        Round firstRound = allRounds.get(0);
        List<Seed> firstRoundSeeds = firstRound.getSeeds();
        int firstRoundSeedSize = firstRoundSeeds.size();

        List<Seed> upperSeeds = createSeeds(firstRoundSeeds.subList(0, firstRoundSeedSize / 2),
                upperBracketParticipantCount, 0, participants);
        List<Seed> lowerSeeds = createSeeds(firstRoundSeeds.subList(firstRoundSeedSize / 2, firstRoundSeedSize),
                lowerBracketParticipantCount, 1, participants);

        List<Seed> seeds = new ArrayList<>(upperSeeds);
        seeds.addAll(lowerSeeds);
        seedRepository.saveAll(seeds);

        firstRound.setSeeds(seeds);

        bracket.setRounds(allRounds);
        for (Seed seed : firstRound.getSeeds()) {
            List<Participant> seedParticipants = seed.getParticipants();
            if (seedParticipants.contains(null)) {
                advanceToNextRound(seedParticipants.get(0).getId(), bracket.getId(), firstRound.getId());
            }
        }

        return bracketRepository.save(bracket);
    }

    private List<Seed> createSeeds(List<Seed> seeds, int participantCount,
                                   int startingIndex, List<Participant> participants) {
        int perfectParticipantSize = calculatePerfectParticipantSize(participantCount);
        int numOfByes = perfectParticipantSize - participantCount;

        calculateByes(seeds, numOfByes, startingIndex, participants);

        // TODO geri kalan boşluk nasıl doldurulcak?
        int currIndex = startingIndex == 0 ? 0 : 3;
        for (int i=0; i<seeds.size(); i++) {
            Seed seed = seeds.get(i);
            if (isSeedEmpty(seed)) {
                // if upper bracket and the first group winner
                if (startingIndex == 0 && currIndex == 0) {
                    seed.setParticipants(new ArrayList<>(
                            List.of(participants.get(currIndex))
                    ));
                    // match the participant with the last odd numbered participant
                    int size = participants.size();
                    if (size % 2 == 0) {
                        seed.getParticipants().add(participants.get(size - 2));
                        currIndex = size - 4;
                    } else {
                        seed.getParticipants().add(participants.get(size - 1));
                        currIndex = size - 3;
                    }
                } else if (startingIndex == 1 && currIndex == 5) {
                    seed.setParticipants(new ArrayList<>(
                            List.of(participants.get(currIndex), participants.get(currIndex - 4))
                    ));
                } else if (startingIndex == 1 && currIndex == 3) {
                    seed.setParticipants(new ArrayList<>(
                            List.of(participants.get(currIndex))
                    ));
                    // match the participant with the last odd numbered participant
                    int size = participants.size();
                    if (size % 2 == 0) {
                        seed.getParticipants().add(participants.get(size - 1));
                        currIndex = size - 3;
                    } else {
                        seed.getParticipants().add(participants.get(size - 2));
                        currIndex = size - 4;
                    }
                } else {
                    if (currIndex - 2 > 0 && currIndex < participants.size()) {
                        seed.setParticipants(new ArrayList<>(
                                List.of(participants.get(currIndex),
                                        participants.get(currIndex - 2))));
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

    private void calculateByes(List<Seed> seeds,
                               int numOfByes, int startingIndex,
                               List<Participant> participants) {
        int currIndex = startingIndex;
        int seedIndex = startingIndex == 0 ? 0 : seeds.size() - 1;
        for (int i=0; i<numOfByes; i++) {
            Participant byeParticipant = participants.get(currIndex);
            seeds.set(seedIndex, new Seed(new ArrayList<>(Arrays.asList(byeParticipant, null))));

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
            if (seeds.get(i).getParticipants().contains(participant)) {
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
        return seed.getParticipants().stream().allMatch(Objects::isNull);
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