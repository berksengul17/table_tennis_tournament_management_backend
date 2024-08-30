package com.berk.table_tennis_tournament_management_backend.group_table_time;

import com.berk.table_tennis_tournament_management_backend.group.Group;
import com.berk.table_tennis_tournament_management_backend.table_time.TableTime;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class GroupTableTime {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @OneToOne
    private Group group;
    @ManyToOne
    @JoinColumn(name = "table_time_id")
    private TableTime tableTime;
}
