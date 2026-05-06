package com.microservice.Servicio_TeamReadbull.dto.Request;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TeamRequestDTO {

    private Long id;

    private String name;

    private Long idTournament;

    private Long idCaptain;

    private List<Long> idPlayers;

    private int currentPlayers;

    private String colors;

    private Long captainId;

    private String photo;

    private List<Long> players;
}
