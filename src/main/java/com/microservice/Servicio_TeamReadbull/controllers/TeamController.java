package com.microservice.Servicio_TeamReadbull.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

import com.microservice.Servicio_TeamReadbull.dto.Request.TeamRequestDTO;
import com.microservice.Servicio_TeamReadbull.model.Team;
import com.microservice.Servicio_TeamReadbull.service.TeamService;

@RestController
@RequestMapping("/equipos")
public class TeamController {

    @Autowired
    private TeamService teamService;
    
    @PostMapping
    public ResponseEntity<?> createTeam(@Valid @RequestBody TeamRequestDTO dto) {
        try {
            // Llamamos al servicio que valida las carreras y dorsales
            Team createdTeam = teamService.createTeam(dto);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdTeam);
        } catch (IllegalArgumentException e) {
            // Si alguna regla falla, devolvemos un 400 Bad Request con el mensaje de error
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error interno al crear el equipo");
        }
    }


    @GetMapping("/{id}")
    public ResponseEntity<?> getTeamById(@PathVariable Long id) {
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTeam(@PathVariable Long id) {
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateTeam(@PathVariable Long id, @Valid @RequestBody TeamRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
    }

    @PostMapping("/{teamId}/players/{playerId}")
    public ResponseEntity<?> addPlayer(@PathVariable Long teamId, @PathVariable Long playerId) {
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
    }

    @DeleteMapping("/{teamId}/players/{playerId}")
    public ResponseEntity<?> removePlayer(@PathVariable Long teamId, @PathVariable Long playerId) {
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
    }
}