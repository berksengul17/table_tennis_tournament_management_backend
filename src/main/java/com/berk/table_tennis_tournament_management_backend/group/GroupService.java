package com.berk.table_tennis_tournament_management_backend.group;

import com.berk.table_tennis_tournament_management_backend.age_category.AGE_CATEGORY;
import com.berk.table_tennis_tournament_management_backend.age_category.AgeCategory;
import com.berk.table_tennis_tournament_management_backend.age_category.AgeCategoryRepository;
import com.berk.table_tennis_tournament_management_backend.group_table_time.GroupTableTime;
import com.berk.table_tennis_tournament_management_backend.group_table_time.GroupTableTimeRepository;
import com.berk.table_tennis_tournament_management_backend.match.Match;
import com.berk.table_tennis_tournament_management_backend.match.MatchRepository;
import com.berk.table_tennis_tournament_management_backend.participant.Participant;
import com.berk.table_tennis_tournament_management_backend.participant.ParticipantComparator;
import com.berk.table_tennis_tournament_management_backend.participant.ParticipantRepository;
import com.berk.table_tennis_tournament_management_backend.participant_age_category.ParticipantAgeCategory;
import com.berk.table_tennis_tournament_management_backend.participant_age_category.ParticipantAgeCategoryRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class GroupService {

    private final AgeCategoryRepository ageCategoryRepository;
    private final ParticipantRepository participantRepository;
    private final ParticipantAgeCategoryRepository participantAgeCategoryRepository;
    private final GroupTableTimeRepository groupTableTimeRepository;
    private final MatchRepository matchRepository;
    private final GroupRepository groupRepository;
    private final int NUM_OF_TABLES = 16;


    @Transactional
    public List<Group> createGroupsForAgeCategory(int category, int age, boolean refresh) {
        List<Group> groups = new ArrayList<>();
        AGE_CATEGORY categoryEnum = AGE_CATEGORY.valueOf(category);
        AgeCategory ageCategory = ageCategoryRepository.findByAgeAndCategory(categoryEnum.ageList.get(age),
                                                                            categoryEnum);
        if (ageCategory == null) {
            return groups;
        }

        List<ParticipantAgeCategory> participantAgeCategoryList =
                participantAgeCategoryRepository.findAllByAgeCategory(ageCategory);

        List<Participant> participants = new ArrayList<>();
        participantAgeCategoryList.forEach(participantAgeCategory -> {
            participants.add(participantAgeCategory.getParticipant());
        });

        if (refresh) {
            List<Group> savedGroups =
                    groupRepository.findByAgeCategory_CategoryAndAgeCategory_Age(categoryEnum,
                    categoryEnum.ageList.get(age));

            for (Participant participant : participants) {
                participant.setGroup(null);
                participantRepository.save(participant);
            }

            for (Group group : savedGroups) {
                GroupTableTime foundGroupTableTime = groupTableTimeRepository.findByGroup(group);
                List<Match> foundMatches = matchRepository.findAllByGroup(group);

                if (foundGroupTableTime != null)
                    groupTableTimeRepository.deleteById(foundGroupTableTime.getId());
                if (!foundMatches.isEmpty())
                    matchRepository.deleteAllById(foundMatches.stream().map(Match::getId).toList());
            }

            groupRepository.deleteAllById(savedGroups.stream().map(Group::getId).toList());
        }

        participants.sort(new ParticipantComparator());

        int numOfParticipants = participants.size();

        int numOfGroups = (numOfParticipants / 4) + (numOfParticipants % 4 == 0 ? 0 : 1);
        for (int i = 0; i < numOfGroups; i++) {
            groups.add(new Group(ageCategory, new ArrayList<>()));
        }

        int rowCount = 0;
        boolean increaseRow = false;
        for (int i = 0; i < numOfParticipants; i++) {
             int groupIndex = i % numOfGroups;
             if (groupIndex == numOfGroups - 1) {
                 increaseRow = true;
             } else increaseRow = false;

             if (rowCount % 2 != 0) {
                 groupIndex = numOfGroups - 1 - groupIndex;
             }
             Group group = groups.get(groupIndex);
             group.getParticipants().add(participants.get(i));
             participants.get(i).setGroup(group);

             if (increaseRow) {rowCount ++;}
        }

        for (Group group : groups) {
            List<Participant> groupParticipants = group.getParticipants();
            for (int i = 0; i < groupParticipants.size(); i++) {
                groupParticipants.get(i).setGroupRanking(i + 1); // Assign ranking starting from 1
            }
        }

        return groupRepository.saveAll(groups);
    }

    public List<Group> loadGroupsForAgeCategory(int category, int age) {
        AGE_CATEGORY categoryEnum = AGE_CATEGORY.valueOf(category);
        List<Group> groups = groupRepository.findByAgeCategory_CategoryAndAgeCategory_Age(
                categoryEnum, categoryEnum.ageList.get(age));
        sortGroupParticipants(groups);
        return groups;
    }

    public List<Group> loadAllGroups() {
        List<Group> foundGroups = groupRepository.findAll();
        sortGroupParticipants(foundGroups);
        return foundGroups;
    }

    @Transactional
    public List<Group> saveGroups(List<Group> groups) {
        for (Group group : groups) {
            List<Participant> participants = group.getParticipants();
            // delete related group table time
            GroupTableTime groupTableTime = groupTableTimeRepository.findByGroup(group);
            // reset related table time as available
            groupTableTime.getTableTime().setAvailable(true);
            groupTableTimeRepository.deleteById(
                    groupTableTime.getId());
            // delete related matches
            matchRepository.deleteAllById(
                    matchRepository.findAllByGroup(group)
                            .stream().map(Match::getId).toList());
            for (int i=0; i<participants.size(); i++) {
                Participant participant = participants.get(i);
                participant.setGroupRanking(i + 1);
                participant.setGroup(group);
                participantRepository.save(participant);  // Save the updated participant
            }
        }

        List<Group> savedGroups = groupRepository.saveAll(groups);
        sortGroupParticipants(savedGroups);

        return savedGroups;
    }

    private void sortGroupParticipants(List<Group> groups) {
        for (Group group: groups) {
            if (group.getId() == 365) continue;
            group.getParticipants().sort(new ParticipantComparator());
        }
    }
}
