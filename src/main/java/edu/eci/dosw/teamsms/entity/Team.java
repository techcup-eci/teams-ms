package edu.eci.dosw.teamsms.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.util.List;

@Entity
@Table(name = "teams")
@Data
public class Team {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(nullable = false)
    private String colors;

    @Column(nullable = false)
    private String state = "DRAFT"; 

    @Column(name = "captain_id", nullable = false)
    private Long captainId;

    @OneToMany(mappedBy = "team", cascade = CascadeType.ALL)
    private List<TeamPlayer> players;
}