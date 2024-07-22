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

    public Bracket advanceToNextRound(Long participantId, Long bracketId, Long roundId) {
        Bracket bracket = bracketRepository.findById(bracketId).orElse(null);
        Participant participant = participantRepository.findById(participantId).orElse(null);

        if (bracket == null) return null;
        if (participant == null) return null;

        List<Round> rounds = bracket.getRounds();

        if (rounds == null) return null;

        List<Round> foundRounds = rounds
                .stream()
                .filter(r -> Objects.equals(r.getId(), roundId))
                .toList();

        if (foundRounds.isEmpty()) return null;
        Round round = foundRounds.get(0);

        Round nextRound = null;
        for (int i=0; i<rounds.size(); i++) {
            if (Objects.equals(rounds.get(i).getId(), round.getId())) {
                if (i + 1 >= rounds.size()) {
                    List<Seed> seeds = new ArrayList<>();
                    for (int j = 0; j < rounds.get(i).getSeeds().size() / 2; j++) {
                        seeds.add(new Seed());
                    }
                    seedRepository.saveAll(seeds);
                    nextRound = new Round(bracket, seeds);
                    roundRepository.save(nextRound);
                    rounds.add(nextRound);
                } else {
                    nextRound = rounds.get(++i);
                }
                break;
            }
        }

        List<Seed> seeds = round.getSeeds();
        int nextRoundSeedIndex = 0;
        boolean isLowerSeed = false;
        for (int i=0; i<seeds.size(); i++) {
            if (seeds.get(i).getParticipants().contains(participant)) {
                double actualResult = i / 2.0;
                nextRoundSeedIndex = (int) Math.floor(actualResult);
                if (actualResult - nextRoundSeedIndex > 0) {
                    isLowerSeed = true;
                }
                break;
            }
        }

        List<Seed> nextRoundSeeds = nextRound.getSeeds();
        Seed nextRoundSeed = nextRoundSeeds.get(nextRoundSeedIndex);

        List<Participant> nextRoundSeedParticipants = nextRoundSeed.getParticipants();
        if (nextRoundSeedParticipants == null) {
            nextRoundSeedParticipants = new ArrayList<>();
            nextRoundSeed.setParticipants(nextRoundSeedParticipants);
        }

        if (nextRoundSeedParticipants.size() >= 1) {
            nextRoundSeedParticipants.add(isLowerSeed ? 1 : 0, participant);
        } else {
            nextRoundSeedParticipants.add(participant);
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

        int upperBracketSize = (int) Math.ceil(participants.size() / 2.0);
        int lowerBracketSize = participants.size() - upperBracketSize;

        Bracket bracket = new Bracket(BRACKET_TYPE.WINNERS);
        bracket.setAgeCategory(ageCategoryRepository.findByCategory(ageCategoryId));
        bracketRepository.save(bracket);

        Round firstRound = new Round(bracket, new ArrayList<>());
        roundRepository.save(firstRound);

        List<Seed> upperSeeds = createSeeds(bracket, firstRound.getId(), upperBracketSize, 0, participants);
        List<Seed> lowerSeeds = createSeeds(bracket, firstRound.getId(), lowerBracketSize, 1, participants);

        List<Seed> seeds = new ArrayList<>(upperSeeds);
        seeds.addAll(lowerSeeds);
        seedRepository.saveAll(seeds);

        firstRound.setSeeds(seeds);
        roundRepository.save(firstRound);

        bracket.setRounds(new ArrayList<>(List.of(firstRound)));
        for (Seed seed : firstRound.getSeeds()) {
            List<Participant> seedParticipants = seed.getParticipants();
            if (seedParticipants.size() == 1) {
                advanceToNextRound(seedParticipants.get(0).getId(), bracket.getId(), firstRound.getId());
            }
        }

        return bracketRepository.save(bracket);
    }

    private List<Seed> createSeeds(Bracket bracket, long roundId, int bracketSize,
                                   int startingIndex, List<Participant> participants) {
        int perfectParticipantSize = calculatePerfectParticipantSize(bracketSize);
        int numOfSeeds = perfectParticipantSize / 2;
        int numOfByes = perfectParticipantSize - bracketSize;

        List<Seed> seeds = new ArrayList<>(Collections.nCopies(numOfSeeds, null));
        calculateByes(seeds, numOfByes, startingIndex, participants);

        // TODO geri kalan boşluk nasıl doldurulcak?
        int currIndex = startingIndex == 0 ? 0 : 3;
        for (int i=0; i<seeds.size(); i++) {
            Seed seed = new Seed();
            if (seeds.get(i) == null) {
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
            seeds.set(seedIndex, new Seed(new ArrayList<>(List.of(byeParticipant))));

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

    private int calculatePerfectParticipantSize(int n) {
        int power = (int) Math.ceil(Math.log(n) / Math.log(2));
        return (int) Math.pow(2, power);
    }
}