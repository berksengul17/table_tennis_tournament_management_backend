package com.berk.table_tennis_tournament_management_backend.group;

import com.berk.table_tennis_tournament_management_backend.age_category.AgeCategory;
import com.berk.table_tennis_tournament_management_backend.participant.Participant;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "groups")
public class Group {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private AgeCategory ageCategory;
    // TODO: burda cascade i çıkarınca niye düzeldi araştır
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "group")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private List<Participant> participants;
//    private String startTime;
//    private String tableName;

    public Group(AgeCategory ageCategory, List<Participant> participants) {
        this.ageCategory = ageCategory;
        this.participants = participants;
    }
}
