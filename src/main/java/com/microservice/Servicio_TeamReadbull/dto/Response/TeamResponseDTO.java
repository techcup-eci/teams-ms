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
    private Long idCaptain;
    private Long captainId;
    private List<Long> idPlayers;
    private List<Long> players;
    private Integer currentPlayers;
    private int maxPlayers;
    private int minPlayers;
    private String colors;
    private String photo;
    private Team.TournamentStatus tournamentStatus;
}