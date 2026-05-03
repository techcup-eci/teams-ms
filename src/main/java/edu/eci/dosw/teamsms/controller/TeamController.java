package edu.eci.dosw.teamsms.controller;

import edu.eci.dosw.teamsms.dto.TeamRequestDTO;
import edu.eci.dosw.teamsms.entity.Team;
import edu.eci.dosw.teamsms.service.TeamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/equipos")
public class TeamController {

    @Autowired
    private TeamService teamService;

    @PostMapping
    public ResponseEntity<?> createTeam(@RequestBody TeamRequestDTO request) {
        try {
            Team createdTeam = teamService.createTeam(request);
            return new ResponseEntity<>(createdTeam, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
}