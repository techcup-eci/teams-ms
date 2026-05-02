package com.microservice.Servicio_TeamReadbull.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.microservice.Servicio_TeamReadbull.dto.Request.TeamRequestDTO;
import com.microservice.Servicio_TeamReadbull.dto.Response.TeamResponseDTO;
import com.microservice.Servicio_TeamReadbull.model.Team;

@Mapper(componentModel = "spring")
public interface TeamMapper {

    @Mapping(target = "id", ignore = true)
    Team toEntity(TeamRequestDTO dto);

    TeamResponseDTO toDto(Team team);
}
