package com.microservice.Servicio_TeamReadbull.service;

import java.util.ArrayList;

import com.microservice.Servicio_TeamReadbull.dto.Request.TeamRequestDTO;
import com.microservice.Servicio_TeamReadbull.dto.Response.TeamResponseDTO;
import com.microservice.Servicio_TeamReadbull.exception.ResourceNotFoundException;
import com.microservice.Servicio_TeamReadbull.mappers.TeamMapper;
import com.microservice.Servicio_TeamReadbull.model.Team;
import com.microservice.Servicio_TeamReadbull.repository.TeamRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;

@Slf4j //libreria para logs
@RequiredArgsConstructor
@Service
public class TeamService {

    private final TeamRepository teamRepository;

    private final TeamMapper teamMapper;


    public TeamResponseDTO createTeam(TeamRequestDTO dto) {
        Team team = new Team();
        team.setName(dto.getName());
        team.setIdTournament(dto.getIdTournament());
        team.setIdCaptain(dto.getIdCaptain());
        team.setIdPlayers(dto.getIdPlayers() != null ? dto.getIdPlayers() : new ArrayList<>());
        team.setCurrentPlayers(team.getIdPlayers().size());

        Team saved = teamRepository.save(team);
        log.info("Equipo creado con ID: {} y nombre: {}", saved.getId(), saved.getName());
        return teamMapper.toDto(saved);
    }

    public void deleteTeam(Long id){
        if (!teamRepository.existsById(id)) {
            throw ResourceNotFoundException.notFound("Team", id);
        }
        teamRepository.deleteById(id);
        log.info("Equipo eliminado con ID: {} y nombre: {}", id, teamRepository.findById(id).map(Team::getName));
    }

    public TeamResponseDTO updateTeam(Long id, TeamRequestDTO dto){
        if (!teamRepository.existsById(id)) {
            throw ResourceNotFoundException.notFound("Team", id);
        }
        Team team = teamMapper.toEntity(dto);
        team.setName(dto.getName());
        team.setIdTournament(dto.getIdTournament());
        team.setIdCaptain(dto.getIdCaptain());
        team.setIdPlayers(dto.getIdPlayers() != null ? dto.getIdPlayers() : new ArrayList<>());
        team.setCurrentPlayers(team.getIdPlayers().size());
        Team updated = teamRepository.save(team);
        log.info("Equipo actualizado con ID: {} y nombre: {}", id, updated.getName());
        return teamMapper.toDto(updated);
    }

    public TeamResponseDTO getTeamById(Long id) {
        Team team = teamRepository.findById(id)
            .orElseThrow(() -> ResourceNotFoundException.notFound("Team", id));
        return teamMapper.toDto(team);
    }

    public TeamResponseDTO addPlayer(Long teamId, Long playerId) {
        Team team = teamRepository.findById(teamId)
            .orElseThrow(() -> ResourceNotFoundException.notFound("Team", teamId));
        team.addPlayer(playerId);
        Team saved = teamRepository.save(team);
        log.info("Jugador  con ID: {} agregado al equipo: {}", playerId, saved.getName());
        return teamMapper.toDto(saved);
    }

    public TeamResponseDTO removePlayer(Long teamId, Long playerId) {
        Team team = teamRepository.findById(teamId)
            .orElseThrow(() -> ResourceNotFoundException.notFound("Team", teamId));
        team.removePlayer(playerId);
        Team saved = teamRepository.save(team);
        log.info("Jugador  con ID: {} eliminado del equipo: {}", playerId, saved.getName());
        return teamMapper.toDto(saved);
    }


}
