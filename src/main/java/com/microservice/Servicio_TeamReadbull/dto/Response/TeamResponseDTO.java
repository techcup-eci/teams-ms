package com.microservice.Servicio_TeamReadbull.dto.Response;

import java.util.List;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TeamResponseDTO {

    private Long id;

    private String name;

    private Long idTournament;

    private Long idCaptain;

    private List<Long> idPlayers;

    private int currentPlayers;

    private int maxPlayers;

    private int minPlayers;
}
