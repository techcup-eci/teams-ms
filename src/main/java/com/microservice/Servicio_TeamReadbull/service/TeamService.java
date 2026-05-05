package com.microservice.Servicio_TeamReadbull.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.microservice.Servicio_TeamReadbull.dto.Request.TeamRequestDTO;
import com.microservice.Servicio_TeamReadbull.dto.Response.TeamResponseDTO;
import com.microservice.Servicio_TeamReadbull.model.Team;
import com.microservice.Servicio_TeamReadbull.repository.TeamRepository;

@Service
public class TeamService {

    @Autowired
    private TeamRepository teamRepository;

    public TeamResponseDTO createTeam(TeamRequestDTO dto) {
        Team team = new Team();
        team.setName(dto.getName());
        team.setColors(dto.getColors());
        team.setIdCaptain(dto.getCaptainId());
        team.setPhoto(dto.getPhoto());
        
        if (dto.getPlayers() != null) {
            team.setPlayers(dto.getPlayers());
            team.setCurrentPlayers(dto.getPlayers().size());
        }

        teamRepository.save(team);
        
        // Retornamos null para evitar el error del constructor
        return null; 
    }

    public TeamResponseDTO updateTeam(Long id, TeamRequestDTO dto) {
        return null;
    }

    public void deleteTeam(Long id) {
        teamRepository.deleteById(id);
    }

    public TeamResponseDTO addPlayer(Long teamId, Long playerId) {
        return null;
    }

    public TeamResponseDTO removePlayer(Long teamId, Long playerId) {
        return null;
    }
}