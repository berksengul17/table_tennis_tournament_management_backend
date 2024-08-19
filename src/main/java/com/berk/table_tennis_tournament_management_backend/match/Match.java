package com.berk.table_tennis_tournament_management_backend.match;

import com.berk.table_tennis_tournament_management_backend.group.Group;
import com.berk.table_tennis_tournament_management_backend.participant.Participant;
import com.berk.table_tennis_tournament_management_backend.table.Table;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Match {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "group_id")
    private Group group;
    @ManyToOne
    @JoinColumn(name = "table_id")
    private Table table;
    @ManyToOne
    @JoinColumn(name = "p1_id")
    private Participant p1;
    @ManyToOne
    @JoinColumn(name = "p2_id")
    private Participant p2;
    @Column(name = "p1_score")
    private int p1Score;
    @Column(name = "p2_score")
    private int p2Score;
    private LocalTime startTime;
    private LocalTime endTime;

    public Match(Group group, Table table, Participant p1, Participant p2,
                 LocalTime startTime, LocalTime endTime) {
        this.group = group;
        this.table = table;
        this.p1 = p1;
        this.p2 = p2;
        this.startTime = startTime;
        this.endTime = endTime;
    }
}
