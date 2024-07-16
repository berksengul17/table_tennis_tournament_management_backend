package com.berk.table_tennis_tournament_management_backend.bracket;

import com.berk.table_tennis_tournament_management_backend.age_category.AgeCategoryRepository;
import com.berk.table_tennis_tournament_management_backend.participant.Participant;
import com.berk.table_tennis_tournament_management_backend.participant.ParticipantRepository;
import com.berk.table_tennis_tournament_management_backend.round.Round;
import com.berk.table_tennis_tournament_management_backend.round.RoundRepository;
import com.berk.table_tennis_tournament_management_backend.seed.Seed;
import com.berk.table_tennis_tournament_management_backend.seed.SeedRepository;
import jakarta.servlet.http.Part;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.stereotype.Service;

import java.lang.reflect.Array;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@AllArgsConstructor
public class BracketService {

    private final BracketRepository bracketRepository;
    private final AgeCategoryRepository ageCategoryRepository;
    private final ParticipantRepository participantRepository;
    private final RoundRepository roundRepository;
    private final SeedRepository seedRepository;

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

        List<Seed> upperSeeds = createSeeds(upperBracketSize, 0, participants);
        List<Seed> lowerSeeds = createSeeds(lowerBracketSize, 1, participants);

        List<Seed> seeds = new ArrayList<>(upperSeeds);
        seeds.addAll(lowerSeeds);
        seedRepository.saveAll(seeds);

        Bracket bracket = new Bracket();
        bracketRepository.save(bracket);

        Round firstRound = new Round(bracket, seeds);
        roundRepository.save(firstRound);

        bracket.setRounds(new ArrayList<>(List.of(firstRound)));

        return bracketRepository.save(bracket);
    }

    private List<Seed> createSeeds(int bracketSize, int startingIndex, List<Participant> participants) {
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
                        currIndex = size - 5;
                    } else {
                        seed.getParticipants().add(participants.get(size - 1));
                        currIndex = size - 4;
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
            }
        }


        return seeds;
    }

    private void calculateByes(List<Seed> seeds, int numOfByes, int startingIndex,
                            List<Participant> participants) {
        int seedIndex = 0;
        for (int i=0; i<numOfByes; i++) {
            Participant byeParticipant = participants.get(startingIndex);
            seeds.add(seedIndex, new Seed(new ArrayList<>(List.of(byeParticipant))));

            if (startingIndex == 0) {
                seedIndex = seeds.size() - 1;
            } else {
                seedIndex--;
            }

            startingIndex += 2;
        }
    }

    private int calculatePerfectParticipantSize(int n) {
        int power = (int) Math.ceil(Math.log(n) / Math.log(2));
        return (int) Math.pow(2, power);
    }
}
