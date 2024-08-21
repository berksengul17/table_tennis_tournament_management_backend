package com.berk.table_tennis_tournament_management_backend.table_time;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/table-time")
@AllArgsConstructor
public class TableTimeController {

    private final TableTimeRepository tableTimeRepository;

    @GetMapping
    public TableTime getTableTime(@RequestParam int tableId,
                                  @RequestParam int timeId) {
        return tableTimeRepository.findByTable_IdAndTime_Id(tableId, timeId);
    }
}
