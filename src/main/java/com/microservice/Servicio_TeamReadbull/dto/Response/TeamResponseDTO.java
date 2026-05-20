package com.microservice.Servicio_TeamReadbull.dto.Response;

import java.util.List;

import com.microservice.Servicio_TeamReadbull.model.Team;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TeamResponseDTO {

    private Long id;
    private String name;
    private Long idTournament;
    private Long captainId;
    private List<Long> players;
    private int currentPlayers;
    private int maxPlayers;
    private int minPlayers;
    private String colors;
    private String photo;
    private String code;
    private Team.TournamentStatus tournamentStatus;
    private String warning;
}