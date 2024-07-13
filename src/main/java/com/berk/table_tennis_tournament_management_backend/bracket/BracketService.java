package com.berk.table_tennis_tournament_management_backend.bracket;

import com.berk.table_tennis_tournament_management_backend.age_category.AgeCategoryRepository;
import com.berk.table_tennis_tournament_management_backend.participant.Participant;
import com.berk.table_tennis_tournament_management_backend.participant.ParticipantRepository;
import jakarta.servlet.http.Part;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

@Service
@AllArgsConstructor
public class BracketService {

    private final BracketRepository bracketRepository;
    private final AgeCategoryRepository ageCategoryRepository;
    private final ParticipantRepository participantRepository;

    public Participant[] createWinnersBracket(int ageCategoryId) {
        List<Participant> participants = participantRepository.findAllByAgeCategory_Category(ageCategoryId)
                .stream()
                .filter(participant -> participant.getGroupRanking() == 1
                        || participant.getGroupRanking() == 2)
                .sorted(Comparator.comparingLong(p -> p.getGroup().getId()))
                .sorted(Comparator.comparingInt(Participant::getGroupRanking))
                .toList();

        int upperBracketSize = (int) Math.ceil(participants.size() / 2.0);
        int lowerBracketSize = participants.size() - upperBracketSize;
        Participant[] upperBracket = new Participant[upperBracketSize];
        Participant[] lowerBracket = new Participant[lowerBracketSize];

        int currentIndex = 2;
        upperBracket[0] = participants.get(0);
        for (int i=upperBracketSize-1; i>0; i--) {
            upperBracket[i] = participants.get(currentIndex);
            currentIndex += 2;
        }

        currentIndex = 5;
        lowerBracket[0] = participants.get(3);
        lowerBracket[lowerBracketSize-1] = participants.get(1);
        for (int i=lowerBracketSize-2; i>0; i--) {
            lowerBracket[i] = participants.get(currentIndex);
            currentIndex += 2;
        }

        return ArrayUtils.addAll(upperBracket, lowerBracket);
    }
}
