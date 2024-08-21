package com.berk.table_tennis_tournament_management_backend.match;

import com.berk.table_tennis_tournament_management_backend.age_category.AGE_CATEGORY;
import com.berk.table_tennis_tournament_management_backend.age_category.AgeCategory;
import com.berk.table_tennis_tournament_management_backend.age_category.AgeCategoryRepository;
import com.berk.table_tennis_tournament_management_backend.group.Group;
import com.berk.table_tennis_tournament_management_backend.group.GroupRepository;
import com.berk.table_tennis_tournament_management_backend.group_table_time.GroupTableTime;
import com.berk.table_tennis_tournament_management_backend.group_table_time.GroupTableTimeRepository;
import com.berk.table_tennis_tournament_management_backend.participant.Participant;
import com.berk.table_tennis_tournament_management_backend.participant.ParticipantComparator;
import com.berk.table_tennis_tournament_management_backend.table.Table;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
@AllArgsConstructor
public class MatchService {

    private final GroupRepository groupRepository;
    private final MatchRepository matchRepository;
    private final GroupTableTimeRepository groupTableTimeRepository;
    private final AgeCategoryRepository ageCategoryRepository;

    public List<List<Match>> getMatches(int category, int age) {
        AGE_CATEGORY categoryEnum = AGE_CATEGORY.valueOf(category);
        AgeCategory ageCategory = ageCategoryRepository.findByAgeAndCategory(categoryEnum.ageList.get(age),
                categoryEnum);

        List<Group> groups = groupRepository.findAllByAgeCategory(ageCategory);

        List<List<Match>> matches = new ArrayList<>();
        for (Group group : groups) {
            List<Match> matchList = matchRepository.findAllByGroup(group);

            matchList.sort(Comparator.comparing(Match::getStartTime));

            matches.add(matchList);
        }

        return matches;
    }

    public List<Match> getGroupMatches(Group group) {
        return matchRepository.findAllByGroup(group);
    }

    public List<List<Match>> createMatches(int category, int age) {
        AGE_CATEGORY categoryEnum = AGE_CATEGORY.valueOf(category);
        AgeCategory ageCategory = ageCategoryRepository.findByAgeAndCategory(categoryEnum.ageList.get(age),
                categoryEnum);

        List<Group> groups = groupRepository.findAllByAgeCategory(ageCategory);
        List<List<Match>> matches = new ArrayList<>();

        for (Group group : groups) {
            GroupTableTime groupTableTime = groupTableTimeRepository.findByGroup(group);
            Table table = groupTableTime.getTableTime().getTable();

            List<Participant> participants = group.getParticipants();
            participants.sort(new ParticipantComparator());

            if (participants.size() == 3) {
                LocalTime startTime = groupTableTime.getTableTime().getTime().getStartTime();
                List<Match> groupMatches = List.of(
                        new Match(group, table,  participants.get(1), participants.get(2),
                                startTime, startTime.plusMinutes(20)),
                        new Match(group, table, participants.get(0), participants.get(2),
                                startTime.plusMinutes(20), startTime.plusMinutes(40)),
                        new Match(group, table, participants.get(0), participants.get(1),
                                startTime.plusMinutes(40), startTime.plusMinutes(60)
                        ));
                matchRepository.saveAll(groupMatches);
                matches.add(groupMatches);
            } else if (participants.size() == 4) {
                LocalTime startTime = groupTableTime.getTableTime().getTime().getStartTime();
                List<Match> groupMatches = List.of(
                        new Match(group, table, participants.get(1), participants.get(3),
                                startTime, startTime.plusMinutes(20)),
                        new Match(group, table, participants.get(0), participants.get(2),
                                startTime.plusMinutes(20), startTime.plusMinutes(40)),
                        new Match(group, table, participants.get(1), participants.get(2),
                                startTime.plusMinutes(40), startTime.plusMinutes(60)),
                        new Match(group, table, participants.get(0), participants.get(3),
                                startTime.plusMinutes(60), startTime.plusMinutes(80)),
                        new Match(group, table, participants.get(2), participants.get(3),
                                startTime.plusMinutes(80), startTime.plusMinutes(100)),
                        new Match(group, table, participants.get(0), participants.get(1),
                                startTime.plusMinutes(100), startTime.plusMinutes(120))
                        );
                matchRepository.saveAll(groupMatches);
                matches.add(groupMatches);
            }

            // FIXME: 5 kişi için de düşün
        }

        return matches;
    }

    public void saveScores(Match match)  {
        matchRepository.save(match);
    }

    public Match getMatchBetweenP1AndP2(Participant p1, Participant p2) {
        Match match = matchRepository.findByP1AndP2(p1, p2);
        if (match == null) {
            match = matchRepository.findByP1AndP2(p2, p1);
        }

        return match;
    }

    public int calculateScore(Participant participant) {
        int numOfWins = calculateNumOfWins(participant);
        int numOfLoses = calculateNumOfLoses(participant);

        return numOfWins * 2 + numOfLoses;
    }

    public int calculateScore(int numOfWins, int numOfLoses) {
        return numOfWins * 2 + numOfLoses;
    }

    public int calculateNumOfWins(Participant participant) {
        List<Match> allMatches = matchRepository.findAll();
        int numOfWins = 0;
        for (Match match : allMatches) {
            if ((match.getP1().getId().equals(participant.getId()) && match.getP1Score() == 3) ||
                    (match.getP2().getId().equals(participant.getId()) && match.getP2Score() == 3)) {
                numOfWins++;
            }
        }

        return numOfWins;
    }

    public int calculateNumOfLoses(Participant participant) {
        List<Match> allMatches = matchRepository.findAll();
        int numOfLoses = 0;
        for (Match match : allMatches) {
            if ((match.getP1().getId().equals(participant.getId()) && match.getP2Score() == 3) ||
                    (match.getP2().getId().equals(participant.getId()) && match.getP1Score() == 3)) {
                numOfLoses++;
            }
        }

        return numOfLoses;
    }
}
