package com.berk.table_tennis_tournament_management_backend.match;

import com.berk.table_tennis_tournament_management_backend.age_category.AGE_CATEGORY;
import com.berk.table_tennis_tournament_management_backend.age_category.AgeCategory;
import com.berk.table_tennis_tournament_management_backend.age_category.AgeCategoryRepository;
import com.berk.table_tennis_tournament_management_backend.group.Group;
import com.berk.table_tennis_tournament_management_backend.group.GroupRepository;
import com.berk.table_tennis_tournament_management_backend.group_table_time.GroupTableTime;
import com.berk.table_tennis_tournament_management_backend.group_table_time.GroupTableTimeRepository;
import com.berk.table_tennis_tournament_management_backend.participant.Participant;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.util.ArrayList;
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
            matches.add(matchRepository.findAllByGroup(group));
        }

        return matches;
    }

    public List<List<Match>> createMatches(int category, int age) {
        AGE_CATEGORY categoryEnum = AGE_CATEGORY.valueOf(category);
        AgeCategory ageCategory = ageCategoryRepository.findByAgeAndCategory(categoryEnum.ageList.get(age),
                categoryEnum);

        List<Group> groups = groupRepository.findAllByAgeCategory(ageCategory);
        List<List<Match>> matches = new ArrayList<>();

        for (Group group : groups) {
            GroupTableTime groupTableTime = groupTableTimeRepository.findByGroup(group);

            List<Participant> participants = group.getParticipants();

            if (participants.size() == 3) {
                LocalTime startTime = groupTableTime.getTableTime().getTime().getStartTime();
                List<Match> groupMatches = List.of(
                        new Match(group, participants.get(1), participants.get(2),
                                startTime, startTime.plusMinutes(20)),
                        new Match(group, participants.get(0), participants.get(2),
                                startTime.plusMinutes(20), startTime.plusMinutes(40)),
                        new Match(group, participants.get(0), participants.get(1),
                                startTime.plusMinutes(40), startTime.plusMinutes(60)
                        ));
                matchRepository.saveAll(groupMatches);
                matches.add(groupMatches);
            } else if (participants.size() == 4) {
                LocalTime startTime = groupTableTime.getTableTime().getTime().getStartTime();
                List<Match> groupMatches = List.of(
                        new Match(group, participants.get(1), participants.get(3),
                                startTime, startTime.plusMinutes(20)),
                        new Match(group, participants.get(0), participants.get(2),
                                startTime.plusMinutes(20), startTime.plusMinutes(40)),
                        new Match(group, participants.get(1), participants.get(2),
                                startTime.plusMinutes(40), startTime.plusMinutes(60)),
                        new Match(group, participants.get(0), participants.get(3),
                                startTime.plusMinutes(60), startTime.plusMinutes(80)),
                        new Match(group, participants.get(2), participants.get(3),
                                startTime.plusMinutes(80), startTime.plusMinutes(100)),
                        new Match(group, participants.get(0), participants.get(1),
                                startTime.plusMinutes(100), startTime.plusMinutes(120))
                        );
                matchRepository.saveAll(groupMatches);
                matches.add(groupMatches);
            }

            // 5 kişi için de düşün
        }

        return matches;
    }
}
