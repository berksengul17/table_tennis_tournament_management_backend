package com.berk.table_tennis_tournament_management_backend.group;

import com.berk.table_tennis_tournament_management_backend.age_category.AGE;
import com.berk.table_tennis_tournament_management_backend.age_category.AGE_CATEGORY;
import com.berk.table_tennis_tournament_management_backend.age_category.AgeCategory;
import com.berk.table_tennis_tournament_management_backend.age_category.AgeCategoryRepository;
import com.berk.table_tennis_tournament_management_backend.participant.Participant;
import com.berk.table_tennis_tournament_management_backend.participant.ParticipantComparator;
import com.berk.table_tennis_tournament_management_backend.participant.ParticipantRepository;
import com.berk.table_tennis_tournament_management_backend.participant_age_category.ParticipantAgeCategory;
import com.berk.table_tennis_tournament_management_backend.participant_age_category.ParticipantAgeCategoryRepository;
import jakarta.servlet.http.Part;
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
            for (Participant participant : group.getParticipants()) {
                participant.setGroup(group);
                participantRepository.save(participant);  // Save the updated participant
            }
        }

        List<Group> savedGroups = groupRepository.saveAll(groups);
        sortGroupParticipants(savedGroups);

        return savedGroups;
    }

    public void calculateStartTimesAndTableNamesOfGroups() {

    }

    public AgeCategory findTheBiggestAgeCategory() {
        AgeCategory theBiggestAgeCategory = null;
        int maxNumOfGroups = 0;
        for (AGE_CATEGORY category : AGE_CATEGORY.values()) {
            for (AGE age : category.ageList) {
                List<Group> groups = groupRepository.findByAgeCategory_CategoryAndAgeCategory_Age(category, age);
                if (groups.size() > maxNumOfGroups) {
                    maxNumOfGroups = groups.size();
                    theBiggestAgeCategory = ageCategoryRepository.findByAgeAndCategory(age, category);
                }
            }
        }

        return theBiggestAgeCategory;
    }

    private void sortGroupParticipants(List<Group> groups) {
        for (Group group: groups) {
            group.getParticipants().sort(new ParticipantComparator());
        }
    }
}
