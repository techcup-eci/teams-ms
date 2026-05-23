package com.microservice.Servicio_TeamReadbull.controllers;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.microservice.Servicio_TeamReadbull.dto.Request.TeamRequestDTO;
import com.microservice.Servicio_TeamReadbull.dto.Request.UpdateNameRequestDTO;
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

    // Solo un capitán puede crear un equipo
    // El captainId se extrae del header X-User-Id que viene del gateway
    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<TeamResponseDTO> createTeam(
            @Valid @RequestBody TeamRequestDTO dto,
            @RequestHeader("X-User-Id") Long captainId) {
        TeamResponseDTO response = teamService.createTeam(dto, captainId);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // Cualquier usuario autenticado puede ver todos los equipos
    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<TeamResponseDTO>> getAllTeams() {
        List<TeamResponseDTO> teams = teamService.getAllteams();
        return ResponseEntity.ok(teams);
    }

    // Cualquier usuario autenticado puede ver un equipo por ID
    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<TeamResponseDTO> getTeamById(@PathVariable Long id) {
        TeamResponseDTO response = teamService.getTeamById(id);
        return ResponseEntity.ok(response);
    }

    // Solo el capitan puede actualizar datos de su equipo
    // El service valida que sea el capitan de ese equipo específico
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('CAPTAIN')")
    public ResponseEntity<TeamResponseDTO> updateTeam(
            @PathVariable Long id,
            @Valid @RequestBody TeamRequestDTO dto,
            @RequestHeader("X-User-Id") Long captainId) {
        TeamResponseDTO response = teamService.updateTeam(id, captainId, dto);
        return ResponseEntity.ok(response);
    }

    // Solo el organizador o admin puede actualizar el estado del torneo en el equipo
    // Este endpoint es llamado por el servicio de torneos cuando cambia el estado
    @PutMapping("/{id}/tournament-status")
    @PreAuthorize("hasRole('ORGANIZER') or hasRole('ADMIN')")
    public ResponseEntity<TeamResponseDTO> updateTournamentStatus(
            @PathVariable Long id,
            @RequestBody Team.TournamentStatus status) {
        TeamResponseDTO response = teamService.updateTournamentStatus(id, status);
        return ResponseEntity.ok(response);
    }

    // Solo el organizador o admin puede eliminar un equipo
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ORGANIZER') or hasRole('ADMIN')")
    public ResponseEntity<Void> deleteTeam(@PathVariable Long id) {
        teamService.deleteTeam(id);
        return ResponseEntity.noContent().build();
    }

    // Solo el capitán puede eliminar jugadores de su equipo
    @DeleteMapping("/{teamId}/players/{playerId}")
    @PreAuthorize("hasRole('CAPTAIN')")
    public ResponseEntity<Void> removePlayer(
            @PathVariable Long teamId,
            @PathVariable Long playerId,
            @RequestHeader("X-User-Id") Long captainId) {
        
        teamService.removePlayer(teamId, playerId, captainId);
        return ResponseEntity.noContent().build();
    }

    // Solo el capitán puede ver las solicitudes pendientes de su equipo
    @GetMapping("/{teamId}/solicitudes")
    @PreAuthorize("hasRole('CAPTAIN')")
    public ResponseEntity<List<Long>> getPendingRequest(
            @PathVariable Long teamId,
            @RequestHeader("X-User-Id") Long captainId) {
        List<Long> response = teamService.getPendingRequest(teamId, captainId);
        return ResponseEntity.ok(response);
    }

    // Solo el capitán puede aceptar solicitudes de su equipo
    @PostMapping("/{teamId}/solicitudes/{playerId}/accept")
    @PreAuthorize("hasRole('CAPTAIN')")
    public ResponseEntity<TeamResponseDTO> acceptRequest(
            @PathVariable Long teamId,
            @PathVariable Long playerId,
            @RequestHeader("X-User-Id") Long captainId) {
        TeamResponseDTO response = teamService.acceptRequest(teamId, playerId, captainId, null);
        return ResponseEntity.ok(response);
    }

    // Solo el capitán puede rechazar solicitudes de su equipo
    @PostMapping("/{teamId}/solicitudes/{playerId}/reject")
    @PreAuthorize("hasRole('CAPTAIN')")
    public ResponseEntity<Void> rejectRequest(
            @PathVariable Long teamId,
            @PathVariable Long playerId,
            @RequestHeader("X-User-Id") Long captainId) {
        teamService.rejectRequest(teamId, playerId, captainId, null);
        return ResponseEntity.noContent().build();
    }

    // Solo un jugador puede enviar solicitud de vinculación a un equipo
    // Un jugador solo puede tener 1 solicitud activa a la vez
    @PostMapping("/{teamId}/solicitudes")
    @PreAuthorize("hasRole('PLAYER')")
    public ResponseEntity<Void> sendRequest(
            @PathVariable Long teamId,
            @RequestHeader("X-User-Id") Long playerId) {
        teamService.sendRequest(teamId, playerId);
        return ResponseEntity.noContent().build();
    }

    // Solo un jugador puede unirse a un equipo por código
    @PostMapping("/join")
    @PreAuthorize("hasRole('PLAYER')")
    public ResponseEntity<Void> sendRequestByCode(
            @RequestParam String code,
            @RequestHeader("X-User-Id") Long playerId) {
        teamService.sendRequestBycode(code, playerId);
        return ResponseEntity.noContent().build();
    }

    // Un jugador puede salirse de su equipo (no el capitán)
    // Solo si el torneo no está activo o en progreso
    // No requiere rol específico — la validación está en el service
    @PostMapping("/{teamId}/leave")
    public ResponseEntity<Void> leaveTeam(
            @PathVariable Long teamId,
            @RequestHeader("X-User-Id") Long playerId) {
        teamService.leaveTeam(teamId, playerId);
        return ResponseEntity.noContent().build();
    }
}