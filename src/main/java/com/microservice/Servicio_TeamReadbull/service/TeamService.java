package com.microservice.Servicio_TeamReadbull.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;


import com.microservice.Servicio_TeamReadbull.dto.Request.TeamRequestDTO;
import com.microservice.Servicio_TeamReadbull.dto.Response.TeamResponseDTO;
import com.microservice.Servicio_TeamReadbull.exception.ResourceNotFoundException;
import com.microservice.Servicio_TeamReadbull.exception.UnauthorizedException;
import com.microservice.Servicio_TeamReadbull.mappers.TeamMapper;
import com.microservice.Servicio_TeamReadbull.model.Team;
import com.microservice.Servicio_TeamReadbull.repository.TeamRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Slf4j
@RequiredArgsConstructor
@Service
public class TeamService {

    private final TeamRepository teamRepository;
    private final TeamMapper teamMapper;
    private final WebClient webClient;    

    // El captainId viene del token JWT via header X-User-Id — no del body
    public TeamResponseDTO createTeam(TeamRequestDTO dto, Long captainId) {
        Team team = new Team();

        ArrayList<Long> initialPlayers = new ArrayList<>();
        initialPlayers.add(captainId);

        team.setName(dto.getName());
        team.setIdTournament(dto.getIdTournament());
        team.setIdCaptain(captainId);
        team.setColors(dto.getColors());
        team.setPhoto(dto.getPhoto());
        team.setPlayers(initialPlayers);
        team.setCurrentPlayers(initialPlayers.size());
        team.setTournamentStatus(Team.TournamentStatus.NONE);

        Team saved = teamRepository.save(team);
        log.info("Equipo creado con ID: {} y nombre: {} por capitán ID: {}", saved.getId(), saved.getName(), captainId);
        return teamMapper.toDto(saved);
    }

    public TeamResponseDTO getTeamById(Long id) {
        Team team = teamRepository.findById(id)
                .orElseThrow(() -> ResourceNotFoundException.notFound("Team", id));
        return teamMapper.toDto(team);
    }

    public List<TeamResponseDTO> getAllteams() {
        List<Team> teams = teamRepository.findAll();
        return teams.stream()
                .map(teamMapper::toDto)
                .toList();
    }

    // HU-02: Solo el capitán de ESE equipo puede actualizar el nombre
    // y solo si no está en torneo Activo o En Progreso
    public TeamResponseDTO updateTeamName(Long id, String newName, Long captainId) {
        Team team = teamRepository.findById(id)
                .orElseThrow(() -> ResourceNotFoundException.notFound("Team", id));

        if (!team.getIdCaptain().equals(captainId)) {
            throw UnauthorizedException.notCaptain(id);
        }

        if (team.isInActiveTournament()) {
            throw new IllegalStateException(
                    "No se puede actualizar el nombre del equipo mientras esté en un torneo Activo o En Progreso.");
        }

        team.setName(newName);
        Team updated = teamRepository.save(team);
        log.info("Nombre del equipo ID {} actualizado a: {} por capitán ID: {}", id, updated.getName(), captainId);
        return teamMapper.toDto(updated);
    }

    // Solo organizador o admin puede actualizar el estado del torneo
    public TeamResponseDTO updateTournamentStatus(Long teamId, Team.TournamentStatus status) {
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> ResourceNotFoundException.notFound("Team", teamId));
        team.setTournamentStatus(status);
        Team saved = teamRepository.save(team);
        log.info("Estado del torneo del equipo ID {} actualizado a: {}", teamId, status);
        return teamMapper.toDto(saved);
    }

    // Solo organizador o admin puede eliminar un equipo
    public void deleteTeam(Long id) {
        if (!teamRepository.existsById(id)) {
            throw ResourceNotFoundException.notFound("Team", id);
        }
        teamRepository.deleteById(id);
        log.info("Equipo eliminado con ID: {}", id);
    }

    // Solo el capitán de ESE equipo puede ver las solicitudes pendientes
    public List<Long> getPendingRequest(Long teamId, Long captainId) {
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> ResourceNotFoundException.notFound("Team", teamId));

        if (!team.getIdCaptain().equals(captainId)) {
            throw UnauthorizedException.notCaptain(teamId);
        }

        return team.getRequests();
    }

    // Solo el capitán de ESE equipo puede rechazar solicitudes
    public void rejectRequest(Long teamId, Long playerId, Long captainId, String authHeader) {
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> ResourceNotFoundException.notFound("Team", teamId));

        if (captainId == null || !team.getIdCaptain().equals(captainId)) {
            throw UnauthorizedException.notCaptain(teamId);
        }

        if (!team.getRequests().contains(playerId)) {
            throw new IllegalStateException("El jugador no tiene solicitud pendiente en este equipo.");
        }

        team.getRequests().remove(playerId);
        teamRepository.save(team);
        log.info("Solicitud del jugador ID {} rechazada en equipo ID {} por capitán ID {}", playerId, teamId, captainId);
    }

    public TeamResponseDTO acceptRequest(Long teamId, Long playerId, Long userId, String authHeader) {
    Team team = teamRepository.findById(teamId) .orElseThrow(() -> ResourceNotFoundException.notFound("Team", teamId));

    if (userId == null || !team.getIdCaptain().equals(userId)) {
        throw UnauthorizedException.notCaptain(teamId);
    }

    team.addPlayer(playerId);
    team.getRequests().remove(playerId);
    Team saved = teamRepository.save(team);

    List<Long> playerIds = new ArrayList<>(saved.getPlayers());
    
    TeamResponseDTO response = teamMapper.toDto(saved);

    if (!validateJerseys(playerIds, authHeader)) {
        response.setWarning("Hay jugadores con el mismo dorsal, revisen sus números.");
    }

    if (!validatePrograms(playerIds, authHeader)) {
        response.setWarning("El equipo no cumple con la mitad de jugadores de programas permitidos.");
    }

    return response;
}

    public void sendRequest(Long teamId, Long playerId) {
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> ResourceNotFoundException.notFound("Team", teamId));

        if (team.getPlayers().size() >= team.getMaxPlayers()) {
            throw new IllegalStateException("El equipo ya tiene el máximo de " + team.getMaxPlayers() + " jugadores.");
        }

        if (team.getPlayers().contains(playerId)) {
            throw new IllegalStateException("El jugador ya pertenece a este equipo.");
        }

        if (teamRepository.existsPlayerInAnyTeam(playerId)) {
            throw new IllegalStateException("El jugador ya pertenece a otro equipo.");
        }

        if (team.getRequests().contains(playerId)) {
            throw new IllegalStateException("El jugador ya tiene una solicitud pendiente en este equipo.");
        }

        team.getRequests().add(playerId);
        teamRepository.save(team);
        log.info("Jugador ID {} envió solicitud al equipo ID {}", playerId, teamId);
    }

    // Un jugador puede unirse a un equipo por código
    public void sendRequesBycode(String code, Long playerId) {
        Team team = teamRepository.findByCode(code)
                .orElseThrow(() -> ResourceNotFoundException.notFound("Team", "code: " + code));

        if (team.getPlayers().size() >= team.getMaxPlayers()) {
            throw new IllegalStateException("El equipo ya tiene el máximo de " + team.getMaxPlayers() + " jugadores.");
        }

        if (team.getPlayers().contains(playerId)) {
            throw new IllegalStateException("El jugador ya pertenece a este equipo.");
        }

        if (teamRepository.existsPlayerInAnyTeam(playerId)) {
            throw new IllegalStateException("El jugador ya pertenece a otro equipo.");
        }

        if (team.getRequests().contains(playerId)) {
            throw new IllegalStateException("El jugador ya tiene una solicitud pendiente en este equipo.");
        }

        team.getRequests().add(playerId);
        teamRepository.save(team);
        log.info("Jugador ID {} envió solicitud al equipo ID {} por código", playerId, team.getId());
    }

    private boolean validateJerseys(List<Long> playerIds, String authHeader) {
        try {
            Map<String, List<Long>> body = Map.of("playerIds", playerIds);
            return Boolean.TRUE.equals(
                webClient.post()
                    .uri("/api/users/validate-jerseys")
                    .header("Authorization", authHeader)
                    .bodyValue(body)
                    .retrieve()
                    .bodyToMono(Boolean.class)
                    .block()
            );
        } catch (Exception e) {
            log.warn("No se pudo validar dorsales: {}", e.getMessage());
            return true; 
        }
    }

    private boolean validatePrograms(List<Long> playerIds, String authHeader) {
        try {
            Map<String, List<Long>> body = Map.of("playerIds", playerIds);
            return Boolean.TRUE.equals(
                webClient.post()
                    .uri("/api/users/validate-programs")
                    .header("Authorization", authHeader)
                    .bodyValue(body)
                    .retrieve()
                    .bodyToMono(Boolean.class)
                    .block()
            );
        } catch (Exception e) {
            log.warn("No se pudo validar programas: {}", e.getMessage());
            return true; 
        }
    }
}