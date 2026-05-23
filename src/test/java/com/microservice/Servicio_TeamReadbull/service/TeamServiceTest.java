package com.microservice.Servicio_TeamReadbull.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Optional;

import com.microservice.Servicio_TeamReadbull.dto.Request.TeamRequestDTO;
import com.microservice.Servicio_TeamReadbull.dto.Response.TeamResponseDTO;
import com.microservice.Servicio_TeamReadbull.exception.ResourceNotFoundException;
import com.microservice.Servicio_TeamReadbull.exception.UnauthorizedException;
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

    @Mock
    private org.springframework.web.reactive.function.client.WebClient webClient;

    @InjectMocks
    private TeamService teamService;

    private Team team;
    private TeamRequestDTO dto;
    private final Long CAPTAIN_ID = 10L;

    @BeforeEach
    void setUp() {
        team = new Team();
        team.setId(1L);
        team.setName("Redbull FC");
        team.setIdCaptain(CAPTAIN_ID);
        team.setTournamentStatus(Team.TournamentStatus.NONE);

        dto = TeamRequestDTO.builder()
                .name("Nuevo Nombre")
                .idTournament("1")
                .colors("Rojo")
                .photo("foto.png")
                .build();
    }

    @Test
    void updateTeamName_whenNoActiveTournament_shouldUpdateName() {
        when(teamRepository.findById(1L)).thenReturn(Optional.of(team));
        when(teamRepository.save(any(Team.class))).thenReturn(team);
        when(teamMapper.toDto(team)).thenReturn(new TeamResponseDTO());

        TeamResponseDTO result = teamService.updateTeam(1L, CAPTAIN_ID, dto);

        assertNotNull(result);
        assertEquals("Nuevo Nombre", team.getName());
        verify(teamRepository).save(team);
    }

    @Test
    void updateTeamName_whenTournamentActive_shouldThrowException() {
        team.setTournamentStatus(Team.TournamentStatus.ACTIVE);
        when(teamRepository.findById(1L)).thenReturn(Optional.of(team));

        IllegalStateException ex = assertThrows(IllegalStateException.class, () ->
                teamService.updateTeam(1L, CAPTAIN_ID, dto));

        assertTrue(ex.getMessage().contains("Activo o En Progreso"));
        verify(teamRepository, never()).save(any());
    }

    @Test
    void updateTeamName_whenTournamentInProgress_shouldThrowException() {
        team.setTournamentStatus(Team.TournamentStatus.IN_PROGRESS);
        when(teamRepository.findById(1L)).thenReturn(Optional.of(team));

        IllegalStateException ex = assertThrows(IllegalStateException.class, () ->
                teamService.updateTeam(1L, CAPTAIN_ID, dto));

        assertTrue(ex.getMessage().contains("Activo o En Progreso"));
        verify(teamRepository, never()).save(any());
    }

    @Test
    void updateTeamName_whenTeamNotFound_shouldThrowException() {
        when(teamRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () ->
                teamService.updateTeam(99L, CAPTAIN_ID, dto));
    }

    @Test
    void updateTeamName_whenTournamentFinished_shouldUpdateName() {
        team.setTournamentStatus(Team.TournamentStatus.FINISHED);
        when(teamRepository.findById(1L)).thenReturn(Optional.of(team));
        when(teamRepository.save(any(Team.class))).thenReturn(team);
        when(teamMapper.toDto(team)).thenReturn(new TeamResponseDTO());

        TeamResponseDTO result = teamService.updateTeam(1L, CAPTAIN_ID, dto);

        assertNotNull(result);
        verify(teamRepository).save(team);
    }

    @Test
    void updateTeamName_whenNotCaptain_shouldThrowUnauthorized() {
        when(teamRepository.findById(1L)).thenReturn(Optional.of(team));

        assertThrows(UnauthorizedException.class, () ->
                teamService.updateTeam(1L, 99L, dto));

        verify(teamRepository, never()).save(any());
    }
}