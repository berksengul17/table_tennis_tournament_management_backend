package com.berk.table_tennis_tournament_management_backend.participant;

import java.util.Comparator;

public class ParticipantComparator implements Comparator<Participant> {
    @Override
    public int compare(Participant o1, Participant o2) {
        return o2.getRating() - o1.getRating();
    }
}
