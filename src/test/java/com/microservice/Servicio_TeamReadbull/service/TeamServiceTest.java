package com.microservice.Servicio_TeamReadbull.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Optional;

import com.microservice.Servicio_TeamReadbull.dto.Response.TeamResponseDTO;
import com.microservice.Servicio_TeamReadbull.exception.ResourceNotFoundException;
import com.microservice.Servicio_TeamReadbull.mappers.TeamMapper;
import com.microservice.Servicio_TeamReadbull.model.Team;
import com.microservice.Servicio_TeamReadbull.repository.TeamRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class TeamServiceTest {

    @Mock
    private TeamRepository teamRepository;

    @Mock
    private TeamMapper teamMapper;

    @InjectMocks
    private TeamService teamService;

    private Team team;

    @BeforeEach
    void setUp() {
        team = new Team();
        team.setId(1L);
        team.setName("Redbull FC");
        team.setIdCaptain(10L);
        team.setTournamentStatus(Team.TournamentStatus.NONE);
    }

    // HU-02: Actualizar nombre exitosamente cuando no hay torneo activo
    @Test
    void updateTeamName_whenNoActiveTournament_shouldUpdateName() {
        team.setTournamentStatus(Team.TournamentStatus.NONE);

        when(teamRepository.findById(1L)).thenReturn(Optional.of(team));
        when(teamRepository.save(any(Team.class))).thenReturn(team);
        when(teamMapper.toDto(team)).thenReturn(new TeamResponseDTO());

        TeamResponseDTO result = teamService.updateTeamName(1L, "Nuevo Nombre");

        assertNotNull(result);
        assertEquals("Nuevo Nombre", team.getName());
        verify(teamRepository).save(team);
    }

    // HU-02: Bloquear actualización cuando torneo está ACTIVE
    @Test
    void updateTeamName_whenTournamentActive_shouldThrowException() {
        team.setTournamentStatus(Team.TournamentStatus.ACTIVE);

        when(teamRepository.findById(1L)).thenReturn(Optional.of(team));

        IllegalStateException ex = assertThrows(IllegalStateException.class, () -> {
            teamService.updateTeamName(1L, "Nuevo Nombre");
        });

        assertTrue(ex.getMessage().contains("Activo o En Progreso"));
        verify(teamRepository, never()).save(any());
    }

    // HU-02: Bloquear actualización cuando torneo está IN_PROGRESS
    @Test
    void updateTeamName_whenTournamentInProgress_shouldThrowException() {
        team.setTournamentStatus(Team.TournamentStatus.IN_PROGRESS);

        when(teamRepository.findById(1L)).thenReturn(Optional.of(team));

        IllegalStateException ex = assertThrows(IllegalStateException.class, () -> {
            teamService.updateTeamName(1L, "Nuevo Nombre");
        });

        assertTrue(ex.getMessage().contains("Activo o En Progreso"));
        verify(teamRepository, never()).save(any());
    }

    // HU-02: Equipo no encontrado
    @Test
    void updateTeamName_whenTeamNotFound_shouldThrowException() {
        when(teamRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            teamService.updateTeamName(99L, "Nuevo Nombre");
        });
    }

    // HU-02: Permitir actualización cuando torneo está FINISHED
    @Test
    void updateTeamName_whenTournamentFinished_shouldUpdateName() {
        team.setTournamentStatus(Team.TournamentStatus.FINISHED);

        when(teamRepository.findById(1L)).thenReturn(Optional.of(team));
        when(teamRepository.save(any(Team.class))).thenReturn(team);
        when(teamMapper.toDto(team)).thenReturn(new TeamResponseDTO());

        TeamResponseDTO result = teamService.updateTeamName(1L, "Nuevo Nombre");

        assertNotNull(result);
        verify(teamRepository).save(team);
    }
}