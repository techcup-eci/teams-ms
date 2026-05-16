package com.microservice.Servicio_TeamReadbull.controllers;

import com.microservice.Servicio_TeamReadbull.dto.Request.TeamRequestDTO;
import com.microservice.Servicio_TeamReadbull.dto.Request.UpdateNameRequestDTO;
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
    private final Long CAPTAIN_ID = 10L;

    @BeforeEach
    void setUp() {
        responseDTO = TeamResponseDTO.builder()
                .id(1L)
                .name("Redbull FC")
                .idCaptain(CAPTAIN_ID)
                .colors("Rojo")
                .photo("foto.png")
                .tournamentStatus(Team.TournamentStatus.NONE)
                .build();

        requestDTO = TeamRequestDTO.builder()
                .name("Redbull FC")
                .idTournament(1L)
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
            when(teamService.createTeam(any(TeamRequestDTO.class), eq(CAPTAIN_ID))).thenReturn(responseDTO);

            ResponseEntity<TeamResponseDTO> response = teamController.createTeam(requestDTO, CAPTAIN_ID);

            assertEquals(HttpStatus.CREATED, response.getStatusCode());
            assertNotNull(response.getBody());
            assertEquals(1L, response.getBody().getId());
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
        void getTeamById_notFound() {
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
    @DisplayName("updateTeamName")
    class UpdateTeamName {

        @Test
        @DisplayName("Retorna 200 con equipo actualizado")
        void updateTeamName_returns200() {
            UpdateNameRequestDTO dto = new UpdateNameRequestDTO();
            dto.setName("Nuevo Nombre");

            when(teamService.updateTeamName(1L, "Nuevo Nombre", CAPTAIN_ID)).thenReturn(responseDTO);

            ResponseEntity<TeamResponseDTO> response = teamController.updateTeamName(1L, dto, CAPTAIN_ID);

            assertEquals(HttpStatus.OK, response.getStatusCode());
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
    @DisplayName("getPendingRequest")
    class GetPendingRequest {

        @Test
        @DisplayName("Retorna 200 con solicitudes")
        void getPendingRequest_returns200() {
            when(teamService.getPendingRequest(1L, CAPTAIN_ID)).thenReturn(List.of(30L, 40L));

            ResponseEntity<List<Long>> response = teamController.getPendingRequest(1L, CAPTAIN_ID);

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
            doNothing().when(teamService).rejectRequest(1L, 30L, CAPTAIN_ID, null);

            ResponseEntity<Void> response = teamController.rejectRequest(1L, 30L, CAPTAIN_ID);

            assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        }
    }

    @Nested
    @DisplayName("acceptRequest")
    class AcceptRequest {

        @Test
        @DisplayName("Retorna 204 al aceptar")
        void acceptRequest_returns204() {
            doNothing().when(teamService).acceptRequest(1L, 30L, CAPTAIN_ID, null);

            ResponseEntity<Void> response = teamController.acceptRequest(1L, 30L, CAPTAIN_ID);

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
    }

    @Nested
    @DisplayName("sendRequestByCode")
    class SendRequestByCode {

        @Test
        @DisplayName("Retorna 204 al unirse por código")
        void joinByCode_returns204() {
            doNothing().when(teamService).sendRequesBycode("ABC123", 50L);

            ResponseEntity<Void> response = teamController.sendRequestByCode("ABC123", 50L);

            assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        }

        @Test
        @DisplayName("Lanza excepción si código inválido")
        void joinByCode_invalidCode() {
            doThrow(ResourceNotFoundException.notFound("Team", "code: INVALID"))
                    .when(teamService).sendRequesBycode("INVALID", 50L);

            assertThrows(ResourceNotFoundException.class,
                    () -> teamController.sendRequestByCode("INVALID", 50L));
        }
    }
}