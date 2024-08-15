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

    @PostMapping("/create/{category}/{age}")
    public List<Group> createGroups(@PathVariable int category,
                                    @PathVariable int age,
                                    @RequestParam boolean refresh) {
        return groupService.createGroupsForAgeCategory(category, age, refresh);
    }

    @GetMapping("/load/{category}/{age}")
    public List<Group> loadGroupsForAgeCategory(@PathVariable int category, @PathVariable int age) {
        return groupService.loadGroupsForAgeCategory(category, age);
    }

    @GetMapping("/load-all")
    public List<Group> loadAllGroups() {
        return groupService.loadAllGroups();
    }

    @PostMapping("/save")
    public List<Group> saveGroups(@RequestBody List<Group> groups) {
        return groupService.saveGroups(groups);
    }
}
