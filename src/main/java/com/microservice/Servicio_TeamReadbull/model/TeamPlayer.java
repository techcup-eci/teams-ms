package com.microservice.Servicio_TeamReadbull.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "team_players")
@Data
public class TeamPlayer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false, unique = true) 
    private Long userId;

    @Column(nullable = false)
    private Integer dorsal;

    @Column(name = "academic_program", nullable = false)
    private String academicProgram;

    @ManyToOne
    @JoinColumn(name = "team_id")
    private Team team;
}