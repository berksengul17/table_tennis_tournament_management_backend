package com.berk.table_tennis_tournament_management_backend.group_table_time;

import com.berk.table_tennis_tournament_management_backend.age_category.AgeCategory;
import com.berk.table_tennis_tournament_management_backend.age_category.AgeCategoryCount;
import com.berk.table_tennis_tournament_management_backend.group.Group;
import com.berk.table_tennis_tournament_management_backend.group.GroupRepository;
import com.berk.table_tennis_tournament_management_backend.table_time.TableTime;
import com.berk.table_tennis_tournament_management_backend.table_time.TableTimeRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class GroupTableTimeService {

    private final GroupTableTimeRepository groupTableTimeRepository;
    private final GroupRepository groupRepository;
    private final TableTimeRepository tableTimeRepository;

    public List<GroupTableTime> assignGroupsToTableAndTime() {
        List<GroupTableTime> groupTableTimeList = groupTableTimeRepository.findAll();
        List<AgeCategoryCount> ageCategoryCounts = groupRepository.getAgeCategoryCounts();

        if (!groupTableTimeList.isEmpty()) {
            return groupTableTimeList;
        }

        for (AgeCategoryCount ageCategoryCount : ageCategoryCounts) {
            List<Group> groups = groupRepository.findAllByAgeCategory(ageCategoryCount.getAgeCategory());
            List<TableTime> tableTimeList = tableTimeRepository.findAll();
            for (Group group : groups) {
                for (TableTime tableTime : tableTimeList) {
                    if (tableTime.isAvailable()) {
                        GroupTableTime groupTableTime = new GroupTableTime();
                        groupTableTime.setGroup(group);
                        groupTableTime.setTableTime(tableTime);
                        tableTime.setAvailable(false);
                        groupTableTimeList.add(groupTableTime);
                        groupTableTimeRepository.save(groupTableTime);
                        break;
                    }
                }
            }
        }

        return groupTableTimeList;
    }

    @Transactional
    public List<GroupTableTime> saveGroupTableTimes(List<GroupTableTime> groupTableTimeList) {
        if (groupTableTimeList.isEmpty()) {
            return groupTableTimeList;
        }

        List<GroupTableTime> updatedGroupTableTimeList = new ArrayList<>();

        for (GroupTableTime groupTableTime : groupTableTimeList) {
            GroupTableTime found = groupTableTimeRepository
                    .findById(groupTableTime.getId())
                    .orElse(null);

            if (found == null) continue;
            found.setTableTime(groupTableTime.getTableTime());
            updatedGroupTableTimeList.add(found);
        }

        return groupTableTimeRepository.saveAll(updatedGroupTableTimeList);
    }

}
