package com.microservice.Servicio_TeamReadbull.controllers;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.microservice.Servicio_TeamReadbull.dto.Request.TeamRequestDTO;
import com.microservice.Servicio_TeamReadbull.dto.Response.TeamResponseDTO;
import com.microservice.Servicio_TeamReadbull.model.Team;
import com.microservice.Servicio_TeamReadbull.service.TeamService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/teams")
public class TeamController {

    private final TeamService teamService;

    public TeamController(TeamService teamService) {
        this.teamService = teamService;
    }

    @PostMapping
    public ResponseEntity<TeamResponseDTO> createTeam(@Valid @RequestBody TeamRequestDTO dto) {
        TeamResponseDTO response = teamService.createTeam(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TeamResponseDTO> getTeamById(@PathVariable Long id) {
        TeamResponseDTO response = teamService.getTeamById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<TeamResponseDTO>> getAllTeams() {
        List<TeamResponseDTO> teams = teamService.getAllteams();
        return ResponseEntity.ok(teams);
    }

    
    @PutMapping("/{id}")
    public ResponseEntity<TeamResponseDTO> updateTeam(@PathVariable Long id, @Valid @RequestBody TeamRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
    }

    @PutMapping("/{id}/tournament-status")
    public ResponseEntity<TeamResponseDTO> updateTournamentStatus(
            @PathVariable Long id,
            @RequestBody Team.TournamentStatus status) {
        TeamResponseDTO response = teamService.updateTournamentStatus(id, status);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTeam(@PathVariable Long id) {
        teamService.deleteTeam(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{teamId}/players/{playerId}")
    public ResponseEntity<TeamResponseDTO> addPlayer(
            @PathVariable Long teamId,
            @PathVariable Long playerId) {
        TeamResponseDTO response = teamService.addPlayer(teamId, playerId);
        return ResponseEntity.ok(response);
    }

    //eliminar jugador del equipo
    @DeleteMapping("/{teamId}/players/{playerId}")
    public ResponseEntity<TeamResponseDTO> removePlayer(
            @PathVariable Long teamId,
            @PathVariable Long playerId) {
        TeamResponseDTO response = teamService.removePlayer(teamId, playerId);
        return ResponseEntity.ok(response);
    }

    //obtener solicitudes pendientes del equipo
    @GetMapping("/{teamId}/solicitudes")
    public ResponseEntity<List<Long>> getPendingRequest(
            @PathVariable Long teamId,
            @RequestHeader("X-User-Id") Long userId) {
        List<Long> response = teamService.getPendingRequest(teamId, userId);
        return ResponseEntity.ok(response);
    }

    //rechazar solicitud 
    @PostMapping("/{teamId}/solicitudes/{playerId}/reject")
    public ResponseEntity<Void> rejectRequest(
            @RequestHeader("X-User-Id") Long userId,
            @PathVariable Long playerId,
            @PathVariable Long teamId,
            @RequestHeader("Authorization") String authHeader) {
        teamService.rejectRequest(teamId, playerId, userId, authHeader);
        return ResponseEntity.noContent().build();
    }

    //aceptar solicitud
    @PostMapping("/{teamId}/solicitudes/{playerId}/accept")
    public ResponseEntity<Void> acceptRequest(
            @RequestHeader("X-User-Id") Long userId,
            @PathVariable Long playerId,
            @PathVariable Long teamId,
            @RequestHeader("Authorization") String authHeader) {
        teamService.acceptRequest(teamId, playerId, userId, authHeader);
        return ResponseEntity.noContent().build();
    }

    //enviar solicitud al equipo
    @PostMapping("/{teamId}/solicitudes")
    public ResponseEntity<Void> sendRequest(
            @PathVariable Long teamId,
            @RequestHeader("X-User-Id") Long jugadorId) {
        teamService.sendRequest(teamId, jugadorId);
        return ResponseEntity.noContent().build();
    }
}