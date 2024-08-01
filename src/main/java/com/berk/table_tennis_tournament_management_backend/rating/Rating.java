package com.berk.table_tennis_tournament_management_backend.rating;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Rating {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String participantName;
    private int rating;

    public Rating(String participantName, int rating) {
        this.participantName = participantName;
        this.rating = rating;
    }
}
