package com.microservice.Servicio_TeamReadbull.dto.Response;

import java.util.List;

public class TeamResponseDTO {

    private Long id;
    private String name;
    private Long idTournament;
    private Long idCaptain;
    private List<Long> idPlayers;
    private int currentPlayers;
    private int maxPlayers;
    private int minPlayers;

    public TeamResponseDTO() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public Long getIdTournament() { return idTournament; }
    public void setIdTournament(Long idTournament) { this.idTournament = idTournament; }

    public Long getIdCaptain() { return idCaptain; }
    public void setIdCaptain(Long idCaptain) { this.idCaptain = idCaptain; }

    public List<Long> getIdPlayers() { return idPlayers; }
    public void setIdPlayers(List<Long> idPlayers) { this.idPlayers = idPlayers; }

    public int getCurrentPlayers() { return currentPlayers; }
    public void setCurrentPlayers(int currentPlayers) { this.currentPlayers = currentPlayers; }

    public int getMaxPlayers() { return maxPlayers; }
    public void setMaxPlayers(int maxPlayers) { this.maxPlayers = maxPlayers; }

    public int getMinPlayers() { return minPlayers; }
    public void setMinPlayers(int minPlayers) { this.minPlayers = minPlayers; }
}