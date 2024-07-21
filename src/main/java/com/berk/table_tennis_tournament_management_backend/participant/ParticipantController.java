package com.berk.table_tennis_tournament_management_backend.participant;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/participant")
public class ParticipantController {

    private final ParticipantService participantService;

    public ParticipantController(ParticipantService participantService) {
        this.participantService = participantService;
    }

    @PostMapping("/register")
    public Participant register(@RequestBody ParticipantDTO newParticipant) {
        return participantService.register(newParticipant);
    }

    @GetMapping("/get-all-participants")
    public List<Participant> getParticipants() {
        return participantService.getParticipants();
    }

//    @GetMapping("/distribute")
//    public Map<Integer, List<List<Participant>>> distributeParticipantsToTables(@RequestParam int totalTables) {
//        Map<Integer, List<Participant>> categorizedParticipants = participantService.categorizeParticipants();
//        return participantService.distributeParticipantsToTables(categorizedParticipants, totalTables);
//    }
}