package com.microservice.Servicio_TeamReadbull.controllers;

import com.microservice.Servicio_TeamReadbull.dto.Request.TeamRequestDTO;
import com.microservice.Servicio_TeamReadbull.dto.Response.TeamResponseDTO;
import com.microservice.Servicio_TeamReadbull.exception.ResourceNotFoundException;
import com.microservice.Servicio_TeamReadbull.model.Team;
import com.microservice.Servicio_TeamReadbull.service.TeamService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TeamControllerTest {

    @Mock
    private TeamService teamService;

    @InjectMocks
    private TeamController teamController;

    private TeamResponseDTO responseDTO;
    private TeamRequestDTO requestDTO;

    @BeforeEach
    void setUp() {
        responseDTO = TeamResponseDTO.builder()
                .id(1L)
                .name("Redbull FC")
                .idCaptain(10L)
                .colors("Rojo")
                .photo("foto.png")
                .tournamentStatus(Team.TournamentStatus.NONE)
                .build();

        requestDTO = TeamRequestDTO.builder()
                .name("Redbull FC")
                .idTournament(1L)
                .idCaptain(10L)
                .captainId(10L)
                .colors("Rojo")
                .photo("foto.png")
                .build();
    }

    @Nested
    @DisplayName("createTeam")
    class CreateTeam {

        @Test
        @DisplayName("Retorna 201 con el DTO creado")
        void createTeam_returns201() {
            when(teamService.createTeam(any(TeamRequestDTO.class))).thenReturn(responseDTO);
            ResponseEntity<TeamResponseDTO> response = teamController.createTeam(requestDTO);
            assertEquals(HttpStatus.CREATED, response.getStatusCode());
            assertNotNull(response.getBody());
            assertEquals(1L, response.getBody().getId());
            verify(teamService).createTeam(any(TeamRequestDTO.class));
        }
    }

    @Nested
    @DisplayName("getTeamById")
    class GetTeamById {

        @Test
        @DisplayName("Retorna 200 con el equipo")
        void getTeamById_returns200() {
            when(teamService.getTeamById(1L)).thenReturn(responseDTO);
            ResponseEntity<TeamResponseDTO> response = teamController.getTeamById(1L);
            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertEquals("Redbull FC", response.getBody().getName());
        }

        @Test
        @DisplayName("Lanza excepción si no existe")
        void getTeamById_notFound_throwsException() {
            when(teamService.getTeamById(99L))
                    .thenThrow(ResourceNotFoundException.notFound("Team", 99L));
            assertThrows(ResourceNotFoundException.class, () -> teamController.getTeamById(99L));
        }
    }

    @Nested
    @DisplayName("getAllTeams")
    class GetAllTeams {

        @Test
        @DisplayName("Retorna 200 con lista")
        void getAllTeams_returns200() {
            when(teamService.getAllteams()).thenReturn(List.of(responseDTO));
            ResponseEntity<List<TeamResponseDTO>> response = teamController.getAllTeams();
            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertEquals(1, response.getBody().size());
        }

        @Test
        @DisplayName("Retorna 200 con lista vacía")
        void getAllTeams_empty() {
            when(teamService.getAllteams()).thenReturn(List.of());
            ResponseEntity<List<TeamResponseDTO>> response = teamController.getAllTeams();
            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertTrue(response.getBody().isEmpty());
        }
    }

    @Nested
    @DisplayName("updateTeam")
    class UpdateTeam {

        @Test
        @DisplayName("Retorna 501 NOT_IMPLEMENTED")
        void updateTeam_returns501() {
            ResponseEntity<TeamResponseDTO> response = teamController.updateTeam(1L, requestDTO);
            assertEquals(HttpStatus.NOT_IMPLEMENTED, response.getStatusCode());
            verifyNoInteractions(teamService);
        }
    }

    @Nested
    @DisplayName("updateTournamentStatus")
    class UpdateTournamentStatus {

        @Test
        @DisplayName("Retorna 200 con equipo actualizado")
        void updateTournamentStatus_returns200() {
            when(teamService.updateTournamentStatus(1L, Team.TournamentStatus.ACTIVE))
                    .thenReturn(responseDTO);
            ResponseEntity<TeamResponseDTO> response =
                    teamController.updateTournamentStatus(1L, Team.TournamentStatus.ACTIVE);
            assertEquals(HttpStatus.OK, response.getStatusCode());
            verify(teamService).updateTournamentStatus(1L, Team.TournamentStatus.ACTIVE);
        }

        @Test
        @DisplayName("Lanza excepción si equipo no existe")
        void updateTournamentStatus_notFound() {
            when(teamService.updateTournamentStatus(99L, Team.TournamentStatus.ACTIVE))
                    .thenThrow(ResourceNotFoundException.notFound("Team", 99L));
            assertThrows(ResourceNotFoundException.class,
                    () -> teamController.updateTournamentStatus(99L, Team.TournamentStatus.ACTIVE));
        }
    }

    @Nested
    @DisplayName("deleteTeam")
    class DeleteTeam {

        @Test
        @DisplayName("Retorna 204 al eliminar")
        void deleteTeam_returns204() {
            doNothing().when(teamService).deleteTeam(1L);
            ResponseEntity<Void> response = teamController.deleteTeam(1L);
            assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        }

        @Test
        @DisplayName("Lanza excepción si no existe")
        void deleteTeam_notFound() {
            doThrow(ResourceNotFoundException.notFound("Team", 99L)).when(teamService).deleteTeam(99L);
            assertThrows(ResourceNotFoundException.class, () -> teamController.deleteTeam(99L));
        }
    }

    @Nested
    @DisplayName("addPlayer")
    class AddPlayer {

        @Test
        @DisplayName("Retorna 200 con equipo actualizado")
        void addPlayer_returns200() {
            when(teamService.addPlayer(1L, 20L)).thenReturn(responseDTO);
            ResponseEntity<TeamResponseDTO> response = teamController.addPlayer(1L, 20L);
            assertEquals(HttpStatus.OK, response.getStatusCode());
        }

        @Test
        @DisplayName("Lanza excepción si equipo no existe")
        void addPlayer_notFound() {
            when(teamService.addPlayer(99L, 20L))
                    .thenThrow(ResourceNotFoundException.notFound("Team", 99L));
            assertThrows(ResourceNotFoundException.class, () -> teamController.addPlayer(99L, 20L));
        }
    }

    @Nested
    @DisplayName("removePlayer")
    class RemovePlayer {

        @Test
        @DisplayName("Retorna 200 al eliminar jugador")
        void removePlayer_returns200() {
            when(teamService.removePlayer(1L, 20L)).thenReturn(responseDTO);
            ResponseEntity<TeamResponseDTO> response = teamController.removePlayer(1L, 20L);
            assertEquals(HttpStatus.OK, response.getStatusCode());
        }

        @Test
        @DisplayName("Lanza excepción si equipo no existe")
        void removePlayer_notFound() {
            when(teamService.removePlayer(99L, 20L))
                    .thenThrow(ResourceNotFoundException.notFound("Team", 99L));
            assertThrows(ResourceNotFoundException.class, () -> teamController.removePlayer(99L, 20L));
        }

        @Test
        @DisplayName("Lanza excepción si torneo activo")
        void removePlayer_activeTournament() {
            when(teamService.removePlayer(1L, 20L))
                    .thenThrow(new IllegalStateException("No se puede eliminar"));
            assertThrows(IllegalStateException.class, () -> teamController.removePlayer(1L, 20L));
        }
    }

    @Nested
    @DisplayName("getPendingRequest")
    class GetPendingRequest {

        @Test
        @DisplayName("Retorna 200 con solicitudes")
        void getPendingRequest_returns200() {
            when(teamService.getPendingRequest(1L, 10L)).thenReturn(List.of(30L, 40L));
            ResponseEntity<List<Long>> response = teamController.getPendingRequest(1L, 10L);
            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertEquals(2, response.getBody().size());
        }
    }

    @Nested
    @DisplayName("rejectRequest")
    class RejectRequest {

        @Test
        @DisplayName("Retorna 204 al rechazar")
        void rejectRequest_returns204() {
            doNothing().when(teamService).rejectRequest(1L, 30L, 10L, null);
            ResponseEntity<Void> response = teamController.rejectRequest(10L, 30L, 1L, null);
            assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        }
    }

    @Nested
    @DisplayName("acceptRequest")
    class AcceptRequest {

        @Test
        @DisplayName("Retorna 204 al aceptar")
        void acceptRequest_returns204() {
            doNothing().when(teamService).acceptRequest(1L, 30L, 10L, null);
            ResponseEntity<Void> response = teamController.acceptRequest(10L, 30L, 1L, null);
            assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        }
    }

    @Nested
    @DisplayName("sendRequest")
    class SendRequest {

        @Test
        @DisplayName("Retorna 204 al enviar solicitud")
        void sendRequest_returns204() {
            doNothing().when(teamService).sendRequest(1L, 50L);
            ResponseEntity<Void> response = teamController.sendRequest(1L, 50L);
            assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        }

        @Test
        @DisplayName("Lanza excepción si equipo lleno")
        void sendRequest_teamFull() {
            doThrow(new IllegalStateException("El equipo ya tiene el máximo"))
                    .when(teamService).sendRequest(1L, 50L);
            assertThrows(IllegalStateException.class, () -> teamController.sendRequest(1L, 50L));
        }
    }

    @Nested
    @DisplayName("sendRequesBycode")
    class SendRequestByCode {

        @Test
        @DisplayName("Retorna 204 al unirse por código")
        void joinByCode_returns204() {
            doNothing().when(teamService).sendRequesBycode("ABC123", 50L);
            ResponseEntity<Void> response = teamController.sendRequesBycode("ABC123", 50L);
            assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        }

        @Test
        @DisplayName("Lanza excepción si código inválido")
        void joinByCode_invalidCode() {
            doThrow(ResourceNotFoundException.notFound("Team", "code: INVALID"))
                    .when(teamService).sendRequesBycode("INVALID", 50L);
            assertThrows(ResourceNotFoundException.class,
                    () -> teamController.sendRequesBycode("INVALID", 50L));
        }
    }
}
