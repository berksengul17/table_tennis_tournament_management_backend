package com.berk.table_tennis_tournament_management_backend.table_time;

import com.berk.table_tennis_tournament_management_backend.table.Table;
import com.berk.table_tennis_tournament_management_backend.time.Time;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class TableTime {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "table_id")
    private Table table;
    @ManyToOne
    @JoinColumn(name = "time_id")
    private Time time;
    private boolean isAvailable;

    public TableTime(Table table, Time time, boolean isAvailable) {
        this.table = table;
        this.time = time;
        this.isAvailable = isAvailable;
    }
}
