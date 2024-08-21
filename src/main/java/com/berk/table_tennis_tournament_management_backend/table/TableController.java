package com.berk.table_tennis_tournament_management_backend.table;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/table")
@AllArgsConstructor
public class TableController {

    private final TableRepository tableRepository;

    @GetMapping
    public List<Table> getAllTables() {
        return tableRepository.findAll();
    }
}
