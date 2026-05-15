package com.microservice.Servicio_TeamReadbull.service;

import com.microservice.Servicio_TeamReadbull.dto.Request.TeamRequestDTO;
import com.microservice.Servicio_TeamReadbull.dto.Response.TeamResponseDTO;
import com.microservice.Servicio_TeamReadbull.exception.ResourceNotFoundException;
import com.microservice.Servicio_TeamReadbull.exception.UnauthorizedException;
import com.microservice.Servicio_TeamReadbull.mappers.TeamMapper;
import com.microservice.Servicio_TeamReadbull.model.Team;
import com.microservice.Servicio_TeamReadbull.repository.TeamRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TeamServiceFullTest {

    @Mock
    private TeamRepository teamRepository;

    @Mock
    private TeamMapper teamMapper;

    @InjectMocks
    private TeamService teamService;

    private Team team;
    private TeamRequestDTO dto;
    private TeamResponseDTO responseDTO;
    private final Long CAPTAIN_ID = 10L;

    @BeforeEach
    void setUp() {
        team = new Team();
        team.setId(1L);
        team.setName("Redbull FC");
        team.setIdCaptain(CAPTAIN_ID);
        team.setTournamentStatus(Team.TournamentStatus.NONE);
        team.setColors("Rojo y Azul");
        team.setPhoto("foto.png");
        team.setPlayers(new ArrayList<>(List.of(CAPTAIN_ID)));
        team.setCurrentPlayers(1);
        team.setRequests(new ArrayList<>());

        dto = TeamRequestDTO.builder()
                .name("Nuevo Equipo")
                .idTournament(1L)
                .colors("Rojo")
                .photo("foto.png")
                .build();

        responseDTO = TeamResponseDTO.builder()
                .id(1L)
                .name("Redbull FC")
                .idCaptain(CAPTAIN_ID)
                .build();
    }

    // =========================================================
    // createTeam
    // =========================================================
    @Nested
    @DisplayName("createTeam")
    class CreateTeam {

        @Test
        @DisplayName("Crea equipo correctamente y retorna DTO")
        void createTeam_success() {
            when(teamRepository.save(any(Team.class))).thenReturn(team);
            when(teamMapper.toDto(any(Team.class))).thenReturn(responseDTO);

            TeamResponseDTO result = teamService.createTeam(dto, CAPTAIN_ID);

            assertNotNull(result);
            verify(teamRepository).save(any(Team.class));
        }

        @Test
        @DisplayName("El equipo se guarda con el capitán en la lista de jugadores")
        void createTeam_captainAddedToPlayers() {
            when(teamRepository.save(any(Team.class))).thenAnswer(inv -> {
                Team t = inv.getArgument(0);
                assertThat(t.getPlayers()).contains(CAPTAIN_ID);
                assertEquals(CAPTAIN_ID, t.getIdCaptain());
                return t;
            });
            when(teamMapper.toDto(any(Team.class))).thenReturn(responseDTO);

            teamService.createTeam(dto, CAPTAIN_ID);

            verify(teamRepository).save(any(Team.class));
        }

        @Test
        @DisplayName("Status inicial es NONE")
        void createTeam_initialStatusIsNone() {
            when(teamRepository.save(any(Team.class))).thenAnswer(inv -> {
                Team t = inv.getArgument(0);
                assertEquals(Team.TournamentStatus.NONE, t.getTournamentStatus());
                return t;
            });
            when(teamMapper.toDto(any(Team.class))).thenReturn(responseDTO);

            teamService.createTeam(dto, CAPTAIN_ID);
        }
    }

    // =========================================================
    // getTeamById
    // =========================================================
    @Nested
    @DisplayName("getTeamById")
    class GetTeamById {

        @Test
        @DisplayName("Retorna DTO cuando el equipo existe")
        void getTeamById_found() {
            when(teamRepository.findById(1L)).thenReturn(Optional.of(team));
            when(teamMapper.toDto(team)).thenReturn(responseDTO);

            TeamResponseDTO result = teamService.getTeamById(1L);

            assertNotNull(result);
            assertEquals(1L, result.getId());
        }

        @Test
        @DisplayName("Lanza ResourceNotFoundException cuando no existe")
        void getTeamById_notFound() {
            when(teamRepository.findById(99L)).thenReturn(Optional.empty());

            assertThrows(ResourceNotFoundException.class,
                    () -> teamService.getTeamById(99L));
        }
    }

    // =========================================================
    // getAllTeams
    // =========================================================
    @Nested
    @DisplayName("getAllTeams")
    class GetAllTeams {

        @Test
        @DisplayName("Retorna lista de DTOs")
        void getAllTeams_returnsList() {
            Team team2 = new Team();
            team2.setId(2L);
            team2.setPlayers(new ArrayList<>());

            when(teamRepository.findAll()).thenReturn(Arrays.asList(team, team2));
            when(teamMapper.toDto(any(Team.class))).thenReturn(responseDTO);

            List<TeamResponseDTO> result = teamService.getAllteams();

            assertNotNull(result);
            assertEquals(2, result.size());
        }

        @Test
        @DisplayName("Retorna lista vacía si no hay equipos")
        void getAllTeams_emptyList() {
            when(teamRepository.findAll()).thenReturn(List.of());

            List<TeamResponseDTO> result = teamService.getAllteams();

            assertNotNull(result);
            assertTrue(result.isEmpty());
        }
    }

    // =========================================================
    // updateTeamName (HU-02)
    // =========================================================
    @Nested
    @DisplayName("updateTeamName")
    class UpdateTeamName {

        @Test
        @DisplayName("Capitán actualiza nombre sin torneo activo")
        void updateTeamName_success() {
            when(teamRepository.findById(1L)).thenReturn(Optional.of(team));
            when(teamRepository.save(any(Team.class))).thenReturn(team);
            when(teamMapper.toDto(team)).thenReturn(responseDTO);

            TeamResponseDTO result = teamService.updateTeamName(1L, "Nuevo Nombre", CAPTAIN_ID);

            assertNotNull(result);
            assertEquals("Nuevo Nombre", team.getName());
        }

        @Test
        @DisplayName("Lanza IllegalStateException cuando torneo está ACTIVE")
        void updateTeamName_tournamentActive() {
            team.setTournamentStatus(Team.TournamentStatus.ACTIVE);
            when(teamRepository.findById(1L)).thenReturn(Optional.of(team));

            assertThrows(IllegalStateException.class,
                    () -> teamService.updateTeamName(1L, "Nuevo Nombre", CAPTAIN_ID));
        }

        @Test
        @DisplayName("Lanza UnauthorizedException si no es el capitán")
        void updateTeamName_notCaptain() {
            when(teamRepository.findById(1L)).thenReturn(Optional.of(team));

            assertThrows(UnauthorizedException.class,
                    () -> teamService.updateTeamName(1L, "Nuevo Nombre", 99L));
        }

        @Test
        @DisplayName("Lanza ResourceNotFoundException si equipo no existe")
        void updateTeamName_notFound() {
            when(teamRepository.findById(99L)).thenReturn(Optional.empty());

            assertThrows(ResourceNotFoundException.class,
                    () -> teamService.updateTeamName(99L, "Nuevo Nombre", CAPTAIN_ID));
        }
    }

    // =========================================================
    // updateTournamentStatus
    // =========================================================
    @Nested
    @DisplayName("updateTournamentStatus")
    class UpdateTournamentStatus {

        @Test
        @DisplayName("Actualiza el estado del torneo correctamente")
        void updateTournamentStatus_success() {
            when(teamRepository.findById(1L)).thenReturn(Optional.of(team));
            when(teamRepository.save(any(Team.class))).thenReturn(team);
            when(teamMapper.toDto(team)).thenReturn(responseDTO);

            TeamResponseDTO result = teamService.updateTournamentStatus(1L, Team.TournamentStatus.ACTIVE);

            assertNotNull(result);
            assertEquals(Team.TournamentStatus.ACTIVE, team.getTournamentStatus());
        }

        @Test
        @DisplayName("Lanza ResourceNotFoundException cuando equipo no existe")
        void updateTournamentStatus_notFound() {
            when(teamRepository.findById(99L)).thenReturn(Optional.empty());

            assertThrows(ResourceNotFoundException.class,
                    () -> teamService.updateTournamentStatus(99L, Team.TournamentStatus.ACTIVE));
        }
    }

    // =========================================================
    // deleteTeam
    // =========================================================
    @Nested
    @DisplayName("deleteTeam")
    class DeleteTeam {

        @Test
        @DisplayName("Elimina equipo existente correctamente")
        void deleteTeam_success() {
            when(teamRepository.existsById(1L)).thenReturn(true);
            doNothing().when(teamRepository).deleteById(1L);

            assertDoesNotThrow(() -> teamService.deleteTeam(1L));
            verify(teamRepository).deleteById(1L);
        }

        @Test
        @DisplayName("Lanza ResourceNotFoundException si equipo no existe")
        void deleteTeam_notFound() {
            when(teamRepository.existsById(99L)).thenReturn(false);

            assertThrows(ResourceNotFoundException.class,
                    () -> teamService.deleteTeam(99L));
        }
    }

    // =========================================================
    // getPendingRequest
    // =========================================================
    @Nested
    @DisplayName("getPendingRequest")
    class GetPendingRequest {

        @Test
        @DisplayName("Capitán puede ver solicitudes pendientes")
        void getPendingRequest_captainCanSee() {
            team.setRequests(new ArrayList<>(List.of(30L, 40L)));
            when(teamRepository.findById(1L)).thenReturn(Optional.of(team));

            List<Long> result = teamService.getPendingRequest(1L, CAPTAIN_ID);

            assertThat(result).containsExactly(30L, 40L);
        }

        @Test
        @DisplayName("No capitán lanza UnauthorizedException")
        void getPendingRequest_notCaptain() {
            when(teamRepository.findById(1L)).thenReturn(Optional.of(team));

            assertThrows(UnauthorizedException.class,
                    () -> teamService.getPendingRequest(1L, 99L));
        }

        @Test
        @DisplayName("Lanza ResourceNotFoundException si equipo no existe")
        void getPendingRequest_teamNotFound() {
            when(teamRepository.findById(99L)).thenReturn(Optional.empty());

            assertThrows(ResourceNotFoundException.class,
                    () -> teamService.getPendingRequest(99L, CAPTAIN_ID));
        }
    }

    // =========================================================
    // rejectRequest
    // =========================================================
    @Nested
    @DisplayName("rejectRequest")
    class RejectRequest {

        @Test
        @DisplayName("Capitán rechaza solicitud correctamente")
        void rejectRequest_success() {
            team.setRequests(new ArrayList<>(List.of(30L)));
            when(teamRepository.findById(1L)).thenReturn(Optional.of(team));
            when(teamRepository.save(any(Team.class))).thenReturn(team);

            assertDoesNotThrow(() -> teamService.rejectRequest(1L, 30L, CAPTAIN_ID, null));
            assertThat(team.getRequests()).doesNotContain(30L);
        }

        @Test
        @DisplayName("Lanza UnauthorizedException si no es el capitán")
        void rejectRequest_notCaptain() {
            when(teamRepository.findById(1L)).thenReturn(Optional.of(team));

            assertThrows(UnauthorizedException.class,
                    () -> teamService.rejectRequest(1L, 30L, 99L, null));
        }

        @Test
        @DisplayName("Lanza IllegalStateException si no hay solicitud pendiente")
        void rejectRequest_noPendingRequest() {
            team.setRequests(new ArrayList<>());
            when(teamRepository.findById(1L)).thenReturn(Optional.of(team));

            assertThrows(IllegalStateException.class,
                    () -> teamService.rejectRequest(1L, 30L, CAPTAIN_ID, null));
        }
    }

    // =========================================================
    // acceptRequest
    // =========================================================
    @Nested
    @DisplayName("acceptRequest")
    class AcceptRequest {

        @Test
        @DisplayName("Capitán acepta solicitud y jugador se agrega al equipo")
        void acceptRequest_success() {
            team.setRequests(new ArrayList<>(List.of(30L)));
            when(teamRepository.findById(1L)).thenReturn(Optional.of(team));
            when(teamRepository.save(any(Team.class))).thenReturn(team);

            assertDoesNotThrow(() -> teamService.acceptRequest(1L, 30L, CAPTAIN_ID, null));
            assertThat(team.getPlayers()).contains(30L);
            assertThat(team.getRequests()).doesNotContain(30L);
        }

        @Test
        @DisplayName("Lanza UnauthorizedException si no es el capitán")
        void acceptRequest_notCaptain() {
            when(teamRepository.findById(1L)).thenReturn(Optional.of(team));

            assertThrows(UnauthorizedException.class,
                    () -> teamService.acceptRequest(1L, 30L, 99L, null));
        }

        @Test
        @DisplayName("Lanza IllegalStateException si no hay solicitud pendiente")
        void acceptRequest_noPendingRequest() {
            team.setRequests(new ArrayList<>());
            when(teamRepository.findById(1L)).thenReturn(Optional.of(team));

            assertThrows(IllegalStateException.class,
                    () -> teamService.acceptRequest(1L, 30L, CAPTAIN_ID, null));
        }
    }

    // =========================================================
    // sendRequest
    // =========================================================
    @Nested
    @DisplayName("sendRequest")
    class SendRequest {

        @Test
        @DisplayName("Jugador envía solicitud correctamente")
        void sendRequest_success() {
            when(teamRepository.findById(1L)).thenReturn(Optional.of(team));
            when(teamRepository.existsPlayerInAnyTeam(50L)).thenReturn(false);
            when(teamRepository.save(any(Team.class))).thenReturn(team);

            assertDoesNotThrow(() -> teamService.sendRequest(1L, 50L));
            assertThat(team.getRequests()).contains(50L);
        }

        @Test
        @DisplayName("Lanza IllegalStateException si equipo lleno")
        void sendRequest_teamFull() {
            List<Long> fullPlayers = new ArrayList<>();
            for (long i = 1; i <= 12; i++) fullPlayers.add(i);
            team.setPlayers(fullPlayers);
            when(teamRepository.findById(1L)).thenReturn(Optional.of(team));

            assertThrows(IllegalStateException.class,
                    () -> teamService.sendRequest(1L, 50L));
        }

        @Test
        @DisplayName("Lanza IllegalStateException si jugador ya pertenece al equipo")
        void sendRequest_playerAlreadyInTeam() {
            team.setPlayers(new ArrayList<>(List.of(CAPTAIN_ID, 20L)));
            when(teamRepository.findById(1L)).thenReturn(Optional.of(team));

            assertThrows(IllegalStateException.class,
                    () -> teamService.sendRequest(1L, 20L));
        }

        @Test
        @DisplayName("Lanza IllegalStateException si jugador pertenece a otro equipo")
        void sendRequest_playerInAnotherTeam() {
            when(teamRepository.findById(1L)).thenReturn(Optional.of(team));
            when(teamRepository.existsPlayerInAnyTeam(50L)).thenReturn(true);

            assertThrows(IllegalStateException.class,
                    () -> teamService.sendRequest(1L, 50L));
        }

        @Test
        @DisplayName("Lanza IllegalStateException si jugador ya tiene solicitud pendiente")
        void sendRequest_duplicateRequest() {
            team.setRequests(new ArrayList<>(List.of(50L)));
            when(teamRepository.findById(1L)).thenReturn(Optional.of(team));
            when(teamRepository.existsPlayerInAnyTeam(50L)).thenReturn(false);

            assertThrows(IllegalStateException.class,
                    () -> teamService.sendRequest(1L, 50L));
        }
    }

    // =========================================================
    // sendRequesBycode
    // =========================================================
    @Nested
    @DisplayName("sendRequesBycode")
    class SendRequestByCode {

        @Test
        @DisplayName("Jugador envía solicitud por código correctamente")
        void sendRequesBycode_success() {
            when(teamRepository.findByCode("ABC123")).thenReturn(Optional.of(team));
            when(teamRepository.existsPlayerInAnyTeam(50L)).thenReturn(false);
            when(teamRepository.save(any(Team.class))).thenReturn(team);

            assertDoesNotThrow(() -> teamService.sendRequesBycode("ABC123", 50L));
            assertThat(team.getRequests()).contains(50L);
        }

        @Test
        @DisplayName("Lanza ResourceNotFoundException si código no existe")
        void sendRequesBycode_codeNotFound() {
            when(teamRepository.findByCode("INVALID")).thenReturn(Optional.empty());

            assertThrows(ResourceNotFoundException.class,
                    () -> teamService.sendRequesBycode("INVALID", 50L));
        }
    }
}