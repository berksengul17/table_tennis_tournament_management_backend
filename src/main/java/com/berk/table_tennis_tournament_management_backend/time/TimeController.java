package com.berk.table_tennis_tournament_management_backend.time;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/time")
@AllArgsConstructor
public class TimeController {

    private final TimeRepository timeRepository;

    @GetMapping
    public List<Time> getAllTimes() {
        return timeRepository.findAll();
    }
}
