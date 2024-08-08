package com.berk.table_tennis_tournament_management_backend.log;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Log {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String method;
    @Column(columnDefinition="text")
    private String uri;
    @Column(columnDefinition="text")
    private String requestBody;
    @Column(columnDefinition="text")
    private String responseBody;
    private int responseStatus;
    private LocalDateTime timestamp;

}
