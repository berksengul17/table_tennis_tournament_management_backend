package com.berk.table_tennis_tournament_management_backend.group;

import com.berk.table_tennis_tournament_management_backend.age_category.AGE;
import com.berk.table_tennis_tournament_management_backend.age_category.AGE_CATEGORY;
import com.berk.table_tennis_tournament_management_backend.age_category.AgeCategory;
import com.berk.table_tennis_tournament_management_backend.age_category.AgeCategoryRepository;
import com.berk.table_tennis_tournament_management_backend.participant.Participant;
import com.berk.table_tennis_tournament_management_backend.participant.ParticipantComparator;
import com.berk.table_tennis_tournament_management_backend.participant.ParticipantRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class GroupService {

    private final AgeCategoryRepository ageCategoryRepository;
    private final ParticipantRepository participantRepository;
    private final GroupRepository groupRepository;

    public GroupService(AgeCategoryRepository ageCategoryRepository, ParticipantRepository participantRepository, GroupRepository groupRepository) {
        this.ageCategoryRepository = ageCategoryRepository;
        this.participantRepository = participantRepository;
        this.groupRepository = groupRepository;
    }

    @Transactional
    public List<Group> createGroupsForAgeCategory(int category, int age) {
        List<Group> groups = new ArrayList<>();
        AgeCategory ageCategory = ageCategoryRepository.findByAgeAndCategory(AGE.valueOf(category),
                                                                            AGE_CATEGORY.valueOf(age));
        if (ageCategory == null) {
            return groups;
        }

        List<Participant> participants = ageCategory.getParticipants();
        participants.sort(new ParticipantComparator());

        int numOfParticipants = participants.size();
        int numOfGroups = numOfParticipants / 4;

        // Ensure there is at least one group
        if (numOfGroups == 0) {
            numOfGroups = 1;
        }

        // Initialize the groups
        for (int i = 0; i < numOfGroups; i++) {
            groups.add(new Group(ageCategory, new ArrayList<>()));
        }

        // Distribute participants in a round-robin fashion
        for (int i = 0; i < numOfParticipants; i++) {
            int groupIndex = i % numOfGroups;
            Participant participant = participants.get(i);
            participant.setGroupRanking(groups.get(groupIndex).getParticipants().size() + 1);
            participant.setGroup(groups.get(groupIndex));
            participantRepository.save(participant);
            groups.get(groupIndex).getParticipants().add(participants.get(i));
        }

        // Save the groups to the database
        List<Group> savedGroups = groupRepository.saveAll(groups);
        sortGroupParticipants(savedGroups);
        return savedGroups;
    }

    public List<Group> loadGroupsForAgeCategory(int category, int age) {
        return groupRepository.findByAgeCategory_CategoryAndAgeCategory_Age(
                AGE_CATEGORY.valueOf(category),
                AGE.valueOf(age));
    }

    public List<Group> loadAllGroups() {
        List<Group> foundGroups = groupRepository.findAll();
        sortGroupParticipants(foundGroups);
        return foundGroups;
    }

    @Transactional
    public List<Group> saveGroups(List<Group> groups) {
        groupRepository.deleteAll();
        return groupRepository.saveAll(groups);
    }

    private void sortGroupParticipants(List<Group> groups) {
        for (Group group: groups) {
            group.getParticipants().sort(new ParticipantComparator());
        }
    }
}
