package com.berk.table_tennis_tournament_management_backend.tournament_img;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "tournament_img")
public class TournamentImg {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "tournament_name", length = 255)
    private String tournamentName;

    @Lob
    @Column(name = "data")
    private byte[] data;
}
