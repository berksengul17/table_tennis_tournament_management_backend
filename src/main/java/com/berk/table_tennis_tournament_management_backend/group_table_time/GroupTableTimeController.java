package com.berk.table_tennis_tournament_management_backend.group_table_time;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/group-table-time")
@AllArgsConstructor
public class GroupTableTimeController {

    private final GroupTableTimeService groupTableTimeService;

    @GetMapping
    public List<GroupTableTime> assignGroupsToTableAndTime() {
        return groupTableTimeService.assignGroupsToTableAndTime();
    }

}
