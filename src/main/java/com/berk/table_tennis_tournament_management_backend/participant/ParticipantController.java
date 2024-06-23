package com.berk.table_tennis_tournament_management_backend.participant;

import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/player")
public class ParticipantController {

    private final ParticipantService participantService;

    public ParticipantController(ParticipantService participantService) {
        this.participantService = participantService;
    }

    @PostMapping("/register")
    public Participant register(@RequestBody Participant newParticipant) {
        return participantService.register(newParticipant);
    }

    @GetMapping("/participants")
    public List<Participant> getParticipants() {
        return participantService.getParticipants();
    }

    @PostMapping("/participants/categorize")
    public Map<Integer, List<Participant>> categorizeParticipants() {
        return participantService.categorizeParticipants();
    }

    @GetMapping("/participants/categorized")
    public Map<Integer, List<Participant>> getCategorizedParticipants() {
        return participantService.loadCategorizedParticipants();
    }
}