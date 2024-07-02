package com.berk.table_tennis_tournament_management_backend.group;

import com.berk.table_tennis_tournament_management_backend.participant.Participant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/group")
public class GroupController {

    private final GroupService groupService;

    public GroupController(GroupService groupService) {
        this.groupService = groupService;
    }

    @PostMapping("/create/{ageCategory}")
    public List<Group> createGroups(@PathVariable int ageCategory) {
        return groupService.createGroupsForAgeCategory(ageCategory);
    }

    @GetMapping("/load/{ageCategory}")
    public List<Group> loadGroupsForAgeCategory(@PathVariable int ageCategory) {
        return groupService.loadGroupsForAgeCategory(ageCategory);
    }

    @GetMapping("/load-all")
    public List<Group> loadAllGroups() {
        return groupService.loadAllGroups();
    }
}
