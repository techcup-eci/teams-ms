package com.microservice.Servicio_TeamReadbull.service;

import java.util.ArrayList;
import java.util.List;

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

@Slf4j
@RequiredArgsConstructor
@Service
public class TeamService {

    private final TeamRepository teamRepository;
    private final TeamMapper teamMapper;

    public TeamResponseDTO createTeam(TeamRequestDTO dto) {
        Team team = new Team();

        ArrayList<Long> initialPlayer = new ArrayList<>();
        initialPlayer.add(dto.getCaptainId());

        team.setName(dto.getName());
        team.setIdTournament(dto.getIdTournament());
        team.setIdCaptain(dto.getCaptainId());
        team.setColors(dto.getColors());
        team.setPhoto(dto.getPhoto());
        team.setPlayers(initialPlayer);
        team.setCurrentPlayers(team.getPlayers().size());
        team.setTournamentStatus(Team.TournamentStatus.NONE);

        Team saved = teamRepository.save(team);
        log.info("Equipo creado con ID: {} y nombre: {}", saved.getId(), saved.getName());
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

    public TeamResponseDTO updateTeamName(Long id, String newName) {
        Team team = teamRepository.findById(id)
                .orElseThrow(() -> ResourceNotFoundException.notFound("Team", id));

        if (team.isInActiveTournament()) {
            throw new IllegalStateException(
                    "No se puede actualizar el nombre del equipo mientras esté en un torneo Activo o En Progreso.");
        }

        team.setName(newName);
        Team updated = teamRepository.save(team);
        log.info("Nombre del equipo ID {} actualizado a: {}", id, updated.getName());
        return teamMapper.toDto(updated);
    }

    public TeamResponseDTO updateTournamentStatus(Long teamId, Team.TournamentStatus status) {
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> ResourceNotFoundException.notFound("Team", teamId));
        team.setTournamentStatus(status);
        Team saved = teamRepository.save(team);
        log.info("Estado del torneo del equipo ID {} actualizado a: {}", teamId, status);
        return teamMapper.toDto(saved);
    }

    public void deleteTeam(Long id) {
        if (!teamRepository.existsById(id)) {
            throw ResourceNotFoundException.notFound("Team", id);
        }
        teamRepository.deleteById(id);
        log.info("Equipo eliminado con ID: {}", id);
    }

    public TeamResponseDTO addPlayer(Long teamId, Long playerId) {
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> ResourceNotFoundException.notFound("Team", teamId));
        team.addPlayer(playerId);
        Team saved = teamRepository.save(team);
        log.info("Jugador ID {} agregado al equipo: {}", playerId, saved.getName());
        return teamMapper.toDto(saved);
    }

    public TeamResponseDTO removePlayer(Long teamId, Long playerId) {
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> ResourceNotFoundException.notFound("Team", teamId));

        if (team.isInActiveTournament()) {
            throw new IllegalStateException(
                    "No se puede eliminar un jugador mientras el equipo esté en un torneo Activo o En Progreso.");
        }

        team.removePlayer(playerId);
        Team saved = teamRepository.save(team);
        log.info("Jugador ID {} eliminado del equipo: {}", playerId, saved.getName());
        return teamMapper.toDto(saved);
    }

    public List<Long> getPendingRequest(Long teamId, Long userId) {
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> ResourceNotFoundException.notFound("Team", teamId));

        if (!team.getIdCaptain().equals(userId)) {
            throw UnauthorizedException.notCaptain(teamId);
        }

        return team.getRequests();
    }

    public void rejectRequest(Long teamId, Long playerId, Long userId, String authHeader) {
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> ResourceNotFoundException.notFound("Team", teamId));

        if (userId == null || !team.getIdCaptain().equals(userId)) {
            throw UnauthorizedException.notCaptain(teamId);
        }

        team.getRequests().remove(playerId);
        teamRepository.save(team);
        log.info("Solicitud del jugador ID {} rechazada en equipo ID {}", playerId, teamId);
    }

    public void acceptRequest(Long teamId, Long playerId, Long userId, String authHeader) {
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> ResourceNotFoundException.notFound("Team", teamId));

        if (userId == null || !team.getIdCaptain().equals(userId)) {
            throw UnauthorizedException.notCaptain(teamId);
        }

        team.addPlayer(playerId);
        team.getRequests().remove(playerId);
        teamRepository.save(team);
        log.info("Solicitud del jugador ID {} aceptada en equipo ID {}", playerId, teamId);
    }

    public void sendRequest(Long teamId, Long jugadorId) {
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> ResourceNotFoundException.notFound("Team", teamId));

        if (team.getPlayers().size() >= team.getMaxPlayers()) {
            throw new IllegalStateException("El equipo ya tiene el máximo de " + team.getMaxPlayers() + " jugadores.");
        }

        if (team.getPlayers().contains(jugadorId)) {
            throw new IllegalStateException("El jugador ya pertenece a este equipo.");
        }

        if (teamRepository.existsPlayerInAnyTeam(jugadorId)) {
            throw new IllegalStateException("El jugador ya pertenece a otro equipo.");
        }

        if (team.getRequests().contains(jugadorId)) {
            throw new IllegalStateException("El jugador ya tiene una solicitud pendiente en este equipo.");
        }

        team.getRequests().add(jugadorId);
        teamRepository.save(team);
        log.info("Jugador ID {} envió solicitud al equipo ID {}", jugadorId, teamId);
    }
}