package com.microservice.Servicio_TeamReadbull.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.microservice.Servicio_TeamReadbull.dto.Request.TeamRequestDTO;
import com.microservice.Servicio_TeamReadbull.dto.Response.TeamResponseDTO;
import com.microservice.Servicio_TeamReadbull.model.Team;

@Mapper(componentModel = "spring")
public interface TeamMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "idCaptain", ignore = true)
    @Mapping(target = "players", ignore = true)
    @Mapping(target = "currentPlayers", ignore = true)
    @Mapping(target = "code", ignore = true)
    @Mapping(target = "tournamentStatus", ignore = true)
    Team toEntity(TeamRequestDTO dto);

    @Mapping(target = "captainId", source = "idCaptain")
    @Mapping(target = "maxPlayers", constant = "12")
    @Mapping(target = "minPlayers", constant = "7")
    TeamResponseDTO toDto(Team team);
}
