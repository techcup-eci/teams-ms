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
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClient.RequestBodyUriSpec;
import org.springframework.web.reactive.function.client.WebClient.RequestBodySpec;
import org.springframework.web.reactive.function.client.WebClient.ResponseSpec;
import reactor.core.publisher.Mono;

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

    @Mock private TeamRepository teamRepository;
    @Mock private TeamMapper teamMapper;
    @Mock private WebClient webClient;
    @Mock private RequestBodyUriSpec requestBodyUriSpec;
    @Mock private RequestBodySpec requestBodySpec;
    @Mock private ResponseSpec responseSpec;

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
                .name("Nuevo Nombre")
                .idTournament(1L)
                .colors("Rojo")
                .photo("foto.png")
                .build();

        responseDTO = TeamResponseDTO.builder()
                .id(1L)
                .name("Redbull FC")
                .captainId(CAPTAIN_ID)
                .build();
    }

    @Nested @DisplayName("createTeam")
    class CreateTeam {

        @Test @DisplayName("Crea equipo correctamente")
        void createTeam_success() {
            when(teamRepository.save(any(Team.class))).thenReturn(team);
            when(teamMapper.toDto(any(Team.class))).thenReturn(responseDTO);
            assertNotNull(teamService.createTeam(dto, CAPTAIN_ID));
            verify(teamRepository).save(any(Team.class));
        }

        @Test @DisplayName("El capitán queda en la lista de jugadores")
        void createTeam_captainInPlayers() {
            when(teamRepository.save(any(Team.class))).thenAnswer(inv -> {
                Team t = inv.getArgument(0);
                assertThat(t.getPlayers()).contains(CAPTAIN_ID);
                return t;
            });
            when(teamMapper.toDto(any(Team.class))).thenReturn(responseDTO);
            teamService.createTeam(dto, CAPTAIN_ID);
        }

        @Test @DisplayName("Status inicial es NONE")
        void createTeam_initialStatusNone() {
            when(teamRepository.save(any(Team.class))).thenAnswer(inv -> {
                Team t = inv.getArgument(0);
                assertEquals(Team.TournamentStatus.NONE, t.getTournamentStatus());
                return t;
            });
            when(teamMapper.toDto(any(Team.class))).thenReturn(responseDTO);
            teamService.createTeam(dto, CAPTAIN_ID);
        }
    }

    @Nested @DisplayName("getTeamById")
    class GetTeamById {

        @Test @DisplayName("Retorna DTO cuando existe")
        void getTeamById_found() {
            when(teamRepository.findById(1L)).thenReturn(Optional.of(team));
            when(teamMapper.toDto(team)).thenReturn(responseDTO);
            assertNotNull(teamService.getTeamById(1L));
        }

        @Test @DisplayName("Lanza excepción cuando no existe")
        void getTeamById_notFound() {
            when(teamRepository.findById(99L)).thenReturn(Optional.empty());
            assertThrows(ResourceNotFoundException.class, () -> teamService.getTeamById(99L));
        }
    }

    @Nested @DisplayName("getAllTeams")
    class GetAllTeams {

        @Test @DisplayName("Retorna lista de equipos")
        void getAllTeams_returnsList() {
            when(teamRepository.findAll()).thenReturn(List.of(team));
            when(teamMapper.toDto(any())).thenReturn(responseDTO);
            assertEquals(1, teamService.getAllteams().size());
        }

        @Test @DisplayName("Retorna lista vacía")
        void getAllTeams_empty() {
            when(teamRepository.findAll()).thenReturn(List.of());
            assertTrue(teamService.getAllteams().isEmpty());
        }
    }

    @Nested @DisplayName("updateTeam")
    class UpdateTeam {

        @Test @DisplayName("Capitán actualiza equipo sin torneo activo")
        void updateTeam_success() {
            team.setTournamentStatus(Team.TournamentStatus.NONE);
            when(teamRepository.findById(1L)).thenReturn(Optional.of(team));
            when(teamRepository.save(any())).thenReturn(team);
            when(teamMapper.toDto(team)).thenReturn(responseDTO);
            assertDoesNotThrow(() -> teamService.updateTeam(1L, CAPTAIN_ID, dto));
            verify(teamRepository).save(team);
        }

        @Test @DisplayName("Lanza excepción si no es el capitán")
        void updateTeam_notCaptain() {
            when(teamRepository.findById(1L)).thenReturn(Optional.of(team));
            assertThrows(UnauthorizedException.class,
                    () -> teamService.updateTeam(1L, 99L, dto));
        }

        @Test @DisplayName("Lanza excepción si torneo ACTIVE")
        void updateTeam_activeTournament() {
            team.setTournamentStatus(Team.TournamentStatus.ACTIVE);
            when(teamRepository.findById(1L)).thenReturn(Optional.of(team));
            assertThrows(IllegalStateException.class,
                    () -> teamService.updateTeam(1L, CAPTAIN_ID, dto));
        }

        @Test @DisplayName("Lanza excepción si torneo IN_PROGRESS")
        void updateTeam_inProgressTournament() {
            team.setTournamentStatus(Team.TournamentStatus.IN_PROGRESS);
            when(teamRepository.findById(1L)).thenReturn(Optional.of(team));
            assertThrows(IllegalStateException.class,
                    () -> teamService.updateTeam(1L, CAPTAIN_ID, dto));
        }

        @Test @DisplayName("Lanza excepción si equipo no existe")
        void updateTeam_notFound() {
            when(teamRepository.findById(99L)).thenReturn(Optional.empty());
            assertThrows(ResourceNotFoundException.class,
                    () -> teamService.updateTeam(99L, CAPTAIN_ID, dto));
        }
    }

    @Nested @DisplayName("updateTournamentStatus")
    class UpdateTournamentStatus {

        @Test @DisplayName("Actualiza estado correctamente")
        void updateTournamentStatus_success() {
            when(teamRepository.findById(1L)).thenReturn(Optional.of(team));
            when(teamRepository.save(any())).thenReturn(team);
            when(teamMapper.toDto(team)).thenReturn(responseDTO);
            assertNotNull(teamService.updateTournamentStatus(1L, Team.TournamentStatus.ACTIVE));
            assertEquals(Team.TournamentStatus.ACTIVE, team.getTournamentStatus());
        }

        @Test @DisplayName("Lanza excepción si no existe")
        void updateTournamentStatus_notFound() {
            when(teamRepository.findById(99L)).thenReturn(Optional.empty());
            assertThrows(ResourceNotFoundException.class,
                    () -> teamService.updateTournamentStatus(99L, Team.TournamentStatus.ACTIVE));
        }
    }

    @Nested @DisplayName("deleteTeam")
    class DeleteTeam {

        @Test @DisplayName("Elimina equipo existente")
        void deleteTeam_success() {
            when(teamRepository.existsById(1L)).thenReturn(true);
            doNothing().when(teamRepository).deleteById(1L);
            assertDoesNotThrow(() -> teamService.deleteTeam(1L));
            verify(teamRepository).deleteById(1L);
        }

        @Test @DisplayName("Lanza excepción si no existe")
        void deleteTeam_notFound() {
            when(teamRepository.existsById(99L)).thenReturn(false);
            assertThrows(ResourceNotFoundException.class, () -> teamService.deleteTeam(99L));
        }
    }

    @Nested @DisplayName("removePlayer")
    class RemovePlayer {

        @Test @DisplayName("Capitán elimina jugador correctamente")
        void removePlayer_success() {
            team.setPlayers(new ArrayList<>(Arrays.asList(CAPTAIN_ID, 20L)));
            team.setCurrentPlayers(2);
            team.setTournamentStatus(Team.TournamentStatus.NONE);
            when(teamRepository.findById(1L)).thenReturn(Optional.of(team));
            when(teamRepository.save(any())).thenReturn(team);
            assertDoesNotThrow(() -> teamService.removePlayer(1L, 20L, CAPTAIN_ID));
            assertThat(team.getPlayers()).doesNotContain(20L);
        }

        @Test @DisplayName("Lanza excepción si no es el capitán")
        void removePlayer_notCaptain() {
            when(teamRepository.findById(1L)).thenReturn(Optional.of(team));
            assertThrows(UnauthorizedException.class,
                    () -> teamService.removePlayer(1L, 20L, 99L));
        }

        @Test @DisplayName("Lanza excepción si torneo activo")
        void removePlayer_activeTournament() {
            team.setTournamentStatus(Team.TournamentStatus.ACTIVE);
            when(teamRepository.findById(1L)).thenReturn(Optional.of(team));
            assertThrows(IllegalStateException.class,
                    () -> teamService.removePlayer(1L, 20L, CAPTAIN_ID));
        }

        @Test @DisplayName("Lanza excepción si jugador no está en el equipo")
        void removePlayer_playerNotFound() {
            team.setTournamentStatus(Team.TournamentStatus.NONE);
            when(teamRepository.findById(1L)).thenReturn(Optional.of(team));
            assertThrows(IllegalStateException.class,
                    () -> teamService.removePlayer(1L, 99L, CAPTAIN_ID));
        }

        @Test @DisplayName("Lanza excepción si equipo no existe")
        void removePlayer_teamNotFound() {
            when(teamRepository.findById(99L)).thenReturn(Optional.empty());
            assertThrows(ResourceNotFoundException.class,
                    () -> teamService.removePlayer(99L, 20L, CAPTAIN_ID));
        }
    }

    @Nested @DisplayName("getPendingRequest")
    class GetPendingRequest {

        @Test @DisplayName("Capitán ve solicitudes")
        void getPendingRequest_success() {
            team.setRequests(new ArrayList<>(List.of(30L, 40L)));
            when(teamRepository.findById(1L)).thenReturn(Optional.of(team));
            assertThat(teamService.getPendingRequest(1L, CAPTAIN_ID)).containsExactly(30L, 40L);
        }

        @Test @DisplayName("Lanza excepción si no es capitán")
        void getPendingRequest_notCaptain() {
            when(teamRepository.findById(1L)).thenReturn(Optional.of(team));
            assertThrows(UnauthorizedException.class,
                    () -> teamService.getPendingRequest(1L, 99L));
        }

        @Test @DisplayName("Lanza excepción si equipo no existe")
        void getPendingRequest_notFound() {
            when(teamRepository.findById(99L)).thenReturn(Optional.empty());
            assertThrows(ResourceNotFoundException.class,
                    () -> teamService.getPendingRequest(99L, CAPTAIN_ID));
        }
    }

    @Nested @DisplayName("rejectRequest")
    class RejectRequest {

        @Test @DisplayName("Capitán rechaza solicitud")
        void rejectRequest_success() {
            team.setRequests(new ArrayList<>(List.of(30L)));
            when(teamRepository.findById(1L)).thenReturn(Optional.of(team));
            when(teamRepository.save(any())).thenReturn(team);
            assertDoesNotThrow(() -> teamService.rejectRequest(1L, 30L, CAPTAIN_ID, null));
            assertThat(team.getRequests()).doesNotContain(30L);
        }

        @Test @DisplayName("Lanza excepción si capitanId es null")
        void rejectRequest_nullCaptain() {
            when(teamRepository.findById(1L)).thenReturn(Optional.of(team));
            assertThrows(UnauthorizedException.class,
                    () -> teamService.rejectRequest(1L, 30L, null, null));
        }

        @Test @DisplayName("Lanza excepción si no es el capitán")
        void rejectRequest_notCaptain() {
            when(teamRepository.findById(1L)).thenReturn(Optional.of(team));
            assertThrows(UnauthorizedException.class,
                    () -> teamService.rejectRequest(1L, 30L, 99L, null));
        }

        @Test @DisplayName("Lanza excepción si no hay solicitud pendiente")
        void rejectRequest_noRequest() {
            when(teamRepository.findById(1L)).thenReturn(Optional.of(team));
            assertThrows(IllegalStateException.class,
                    () -> teamService.rejectRequest(1L, 99L, CAPTAIN_ID, null));
        }

        @Test @DisplayName("Lanza excepción si equipo no existe")
        void rejectRequest_notFound() {
            when(teamRepository.findById(99L)).thenReturn(Optional.empty());
            assertThrows(ResourceNotFoundException.class,
                    () -> teamService.rejectRequest(99L, 30L, CAPTAIN_ID, null));
        }
    }

    @Nested @DisplayName("acceptRequest")
    class AcceptRequest {

        @SuppressWarnings({"unchecked", "rawtypes"})
        private void mockWebClientOk() {
            doReturn(requestBodyUriSpec).when(webClient).post();
            doReturn(requestBodyUriSpec).when(requestBodyUriSpec).uri(anyString());
            doReturn(requestBodyUriSpec).when(requestBodyUriSpec).header(anyString(), any());
            doReturn(requestBodySpec).when(requestBodyUriSpec).bodyValue(any());
            doReturn(responseSpec).when(requestBodySpec).retrieve();
            doReturn(Mono.just(true)).when(responseSpec).bodyToMono(Boolean.class);
        }

        @Test @DisplayName("Capitán acepta solicitud correctamente")
        void acceptRequest_success() {
            team.setRequests(new ArrayList<>(List.of(30L)));
            team.setPlayers(new ArrayList<>(List.of(CAPTAIN_ID)));
            team.setCurrentPlayers(1);
            mockWebClientOk();
            when(teamRepository.findById(1L)).thenReturn(Optional.of(team));
            when(teamRepository.save(any())).thenReturn(team);
            when(teamMapper.toDto(any())).thenReturn(responseDTO);
            assertDoesNotThrow(() -> teamService.acceptRequest(1L, 30L, CAPTAIN_ID, "Bearer token"));
            assertThat(team.getPlayers()).contains(30L);
        }

        @Test @DisplayName("Lanza excepción si userId es null")
        void acceptRequest_nullUserId() {
            when(teamRepository.findById(1L)).thenReturn(Optional.of(team));
            assertThrows(UnauthorizedException.class,
                    () -> teamService.acceptRequest(1L, 30L, null, null));
        }

        @Test @DisplayName("Lanza excepción si no es el capitán")
        void acceptRequest_notCaptain() {
            when(teamRepository.findById(1L)).thenReturn(Optional.of(team));
            assertThrows(UnauthorizedException.class,
                    () -> teamService.acceptRequest(1L, 30L, 99L, null));
        }

        @Test @DisplayName("Lanza excepción si no hay solicitud pendiente")
        void acceptRequest_noRequest() {
            when(teamRepository.findById(1L)).thenReturn(Optional.of(team));
            assertThrows(IllegalStateException.class,
                    () -> teamService.acceptRequest(1L, 99L, CAPTAIN_ID, null));
        }

        @Test @DisplayName("Lanza excepción si equipo no existe")
        void acceptRequest_notFound() {
            when(teamRepository.findById(99L)).thenReturn(Optional.empty());
            assertThrows(ResourceNotFoundException.class,
                    () -> teamService.acceptRequest(99L, 30L, CAPTAIN_ID, null));
        }
    }

    @Nested @DisplayName("sendRequest")
    class SendRequest {

        @Test @DisplayName("Jugador envía solicitud correctamente")
        void sendRequest_success() {
            team.setPlayers(new ArrayList<>(List.of(CAPTAIN_ID)));
            team.setCurrentPlayers(1);
            when(teamRepository.findById(1L)).thenReturn(Optional.of(team));
            when(teamRepository.existsPlayerInAnyTeam(50L)).thenReturn(false);
            when(teamRepository.save(any())).thenReturn(team);
            assertDoesNotThrow(() -> teamService.sendRequest(1L, 50L));
            assertThat(team.getRequests()).contains(50L);
        }

        @Test @DisplayName("Lanza excepción si equipo lleno")
        void sendRequest_teamFull() {
            List<Long> full = new ArrayList<>();
            for (long i = 1; i <= 12; i++) full.add(i);
            team.setPlayers(full);
            team.setCurrentPlayers(12);
            when(teamRepository.findById(1L)).thenReturn(Optional.of(team));
            assertThrows(IllegalStateException.class, () -> teamService.sendRequest(1L, 50L));
        }

        @Test @DisplayName("Lanza excepción si jugador ya está en el equipo")
        void sendRequest_alreadyInTeam() {
            team.setPlayers(new ArrayList<>(List.of(CAPTAIN_ID, 20L)));
            when(teamRepository.findById(1L)).thenReturn(Optional.of(team));
            assertThrows(IllegalStateException.class, () -> teamService.sendRequest(1L, 20L));
        }

        @Test @DisplayName("Lanza excepción si jugador está en otro equipo")
        void sendRequest_inAnotherTeam() {
            team.setPlayers(new ArrayList<>(List.of(CAPTAIN_ID)));
            when(teamRepository.findById(1L)).thenReturn(Optional.of(team));
            when(teamRepository.existsPlayerInAnyTeam(50L)).thenReturn(true);
            assertThrows(IllegalStateException.class, () -> teamService.sendRequest(1L, 50L));
        }

        @Test @DisplayName("Lanza excepción si ya tiene solicitud pendiente")
        void sendRequest_duplicate() {
            team.setPlayers(new ArrayList<>(List.of(CAPTAIN_ID)));
            team.setRequests(new ArrayList<>(List.of(50L)));
            when(teamRepository.findById(1L)).thenReturn(Optional.of(team));
            when(teamRepository.existsPlayerInAnyTeam(50L)).thenReturn(false);
            assertThrows(IllegalStateException.class, () -> teamService.sendRequest(1L, 50L));
        }

        @Test @DisplayName("Lanza excepción si equipo no existe")
        void sendRequest_notFound() {
            when(teamRepository.findById(99L)).thenReturn(Optional.empty());
            assertThrows(ResourceNotFoundException.class, () -> teamService.sendRequest(99L, 50L));
        }
    }

    @Nested @DisplayName("sendRequestBycode")
    class SendRequestBycode {

        @Test @DisplayName("Jugador envía solicitud por código correctamente")
        void sendRequestBycode_success() {
            team.setPlayers(new ArrayList<>(List.of(CAPTAIN_ID)));
            team.setCurrentPlayers(1);
            when(teamRepository.findByCode("ABC123")).thenReturn(Optional.of(team));
            when(teamRepository.existsPlayerInAnyTeam(50L)).thenReturn(false);
            when(teamRepository.save(any())).thenReturn(team);
            assertDoesNotThrow(() -> teamService.sendRequestBycode("ABC123", 50L));
            assertThat(team.getRequests()).contains(50L);
        }

        @Test @DisplayName("Lanza excepción si código inválido")
        void sendRequestBycode_invalidCode() {
            when(teamRepository.findByCode("INVALID")).thenReturn(Optional.empty());
            assertThrows(ResourceNotFoundException.class,
                    () -> teamService.sendRequestBycode("INVALID", 50L));
        }

        @Test @DisplayName("Lanza excepción si equipo lleno")
        void sendRequestBycode_teamFull() {
            List<Long> full = new ArrayList<>();
            for (long i = 1; i <= 12; i++) full.add(i);
            team.setPlayers(full);
            team.setCurrentPlayers(12);
            when(teamRepository.findByCode("ABC123")).thenReturn(Optional.of(team));
            assertThrows(IllegalStateException.class,
                    () -> teamService.sendRequestBycode("ABC123", 50L));
        }

        @Test @DisplayName("Lanza excepción si solicitud duplicada")
        void sendRequestBycode_duplicate() {
            team.setPlayers(new ArrayList<>(List.of(CAPTAIN_ID)));
            team.setRequests(new ArrayList<>(List.of(50L)));
            when(teamRepository.findByCode("ABC123")).thenReturn(Optional.of(team));
            assertThrows(IllegalStateException.class,
                    () -> teamService.sendRequestBycode("ABC123", 50L));
        }
    }
}
