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

    @BeforeEach
    void setUp() {
        team = new Team();
        team.setId(1L);
        team.setName("Redbull FC");
        team.setIdCaptain(10L);
        team.setTournamentStatus(Team.TournamentStatus.NONE);
        team.setColors("Rojo y Azul");
        team.setPhoto("foto.png");
        team.setPlayers(new ArrayList<>(List.of(10L)));
        team.setCurrentPlayers(1);

        dto = TeamRequestDTO.builder()
                .name("Nuevo Nombre")
                .idTournament(1L)
                .idCaptain(10L)
                .captainId(10L)
                .colors("Rojo")
                .photo("foto.png")
                .build();

        responseDTO = TeamResponseDTO.builder()
                .id(1L)
                .name("Redbull FC")
                .idCaptain(10L)
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

            TeamResponseDTO result = teamService.createTeam(dto);

            assertNotNull(result);
            verify(teamRepository).save(any(Team.class));
            verify(teamMapper).toDto(any(Team.class));
        }

        @Test
        @DisplayName("El equipo se guarda con el capitán en la lista de jugadores")
        void createTeam_captainAddedToPlayers() {
            when(teamRepository.save(any(Team.class))).thenAnswer(inv -> {
                Team t = inv.getArgument(0);
                assertThat(t.getPlayers()).contains(dto.getCaptainId());
                return t;
            });
            when(teamMapper.toDto(any(Team.class))).thenReturn(responseDTO);

            teamService.createTeam(dto);

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

            teamService.createTeam(dto);
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
            team2.setName("Otro FC");
            team2.setIdCaptain(20L);
            team2.setTournamentStatus(Team.TournamentStatus.NONE);
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
    // updateTeam
    // =========================================================
    @Nested
    @DisplayName("updateTeam")
    class UpdateTeam {

        @Test
        @DisplayName("Actualiza equipo sin torneo activo")
        void updateTeam_noActiveTournament_success() {
            team.setTournamentStatus(Team.TournamentStatus.NONE);
            when(teamRepository.findById(1L)).thenReturn(Optional.of(team));
            when(teamRepository.save(any(Team.class))).thenReturn(team);
            when(teamMapper.toDto(team)).thenReturn(responseDTO);

            TeamResponseDTO result = teamService.updateTeam(1L, dto);

            assertNotNull(result);
            assertEquals("Nuevo Nombre", team.getName());
            verify(teamRepository).save(team);
        }

        @Test
        @DisplayName("Actualiza equipo con torneo FINISHED")
        void updateTeam_tournamentFinished_success() {
            team.setTournamentStatus(Team.TournamentStatus.FINISHED);
            when(teamRepository.findById(1L)).thenReturn(Optional.of(team));
            when(teamRepository.save(any(Team.class))).thenReturn(team);
            when(teamMapper.toDto(team)).thenReturn(responseDTO);

            assertDoesNotThrow(() -> teamService.updateTeam(1L, dto));
            verify(teamRepository).save(team);
        }

        @Test
        @DisplayName("Actualiza equipo con torneo DRAFT")
        void updateTeam_tournamentDraft_success() {
            team.setTournamentStatus(Team.TournamentStatus.DRAFT);
            when(teamRepository.findById(1L)).thenReturn(Optional.of(team));
            when(teamRepository.save(any(Team.class))).thenReturn(team);
            when(teamMapper.toDto(team)).thenReturn(responseDTO);

            assertDoesNotThrow(() -> teamService.updateTeam(1L, dto));
        }

        @Test
        @DisplayName("Lanza IllegalStateException cuando torneo está ACTIVE")
        void updateTeam_tournamentActive_throwsException() {
            team.setTournamentStatus(Team.TournamentStatus.ACTIVE);
            when(teamRepository.findById(1L)).thenReturn(Optional.of(team));

            IllegalStateException ex = assertThrows(IllegalStateException.class,
                    () -> teamService.updateTeam(1L, dto));

            assertThat(ex.getMessage()).contains("Activo o En Progreso");
            verify(teamRepository, never()).save(any());
        }

        @Test
        @DisplayName("Lanza IllegalStateException cuando torneo está IN_PROGRESS")
        void updateTeam_tournamentInProgress_throwsException() {
            team.setTournamentStatus(Team.TournamentStatus.IN_PROGRESS);
            when(teamRepository.findById(1L)).thenReturn(Optional.of(team));

            IllegalStateException ex = assertThrows(IllegalStateException.class,
                    () -> teamService.updateTeam(1L, dto));

            assertThat(ex.getMessage()).contains("Activo o En Progreso");
            verify(teamRepository, never()).save(any());
        }

        @Test
        @DisplayName("Lanza ResourceNotFoundException cuando equipo no existe")
        void updateTeam_notFound_throwsException() {
            when(teamRepository.findById(99L)).thenReturn(Optional.empty());

            assertThrows(ResourceNotFoundException.class,
                    () -> teamService.updateTeam(99L, dto));
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
        void deleteTeam_notFound_throwsException() {
            when(teamRepository.existsById(99L)).thenReturn(false);

            assertThrows(ResourceNotFoundException.class,
                    () -> teamService.deleteTeam(99L));
            verify(teamRepository, never()).deleteById(any());
        }
    }

    // =========================================================
    // addPlayer
    // =========================================================
    @Nested
    @DisplayName("addPlayer")
    class AddPlayer {

        @Test
        @DisplayName("Agrega jugador al equipo correctamente")
        void addPlayer_success() {
            when(teamRepository.findById(1L)).thenReturn(Optional.of(team));
            when(teamRepository.save(any(Team.class))).thenReturn(team);
            when(teamMapper.toDto(team)).thenReturn(responseDTO);

            TeamResponseDTO result = teamService.addPlayer(1L, 20L);

            assertNotNull(result);
            assertThat(team.getPlayers()).contains(20L);
        }

        @Test
        @DisplayName("Lanza ResourceNotFoundException si equipo no existe")
        void addPlayer_teamNotFound() {
            when(teamRepository.findById(99L)).thenReturn(Optional.empty());

            assertThrows(ResourceNotFoundException.class,
                    () -> teamService.addPlayer(99L, 20L));
        }

        @Test
        @DisplayName("Lanza IllegalStateException si el equipo está lleno (12 jugadores)")
        void addPlayer_teamFull_throwsException() {
            List<Long> fullPlayers = new ArrayList<>();
            for (long i = 1; i <= 12; i++) fullPlayers.add(i);
            team.setPlayers(fullPlayers);
            team.setCurrentPlayers(12);

            when(teamRepository.findById(1L)).thenReturn(Optional.of(team));

            assertThrows(IllegalStateException.class,
                    () -> teamService.addPlayer(1L, 99L));
        }
    }

    // =========================================================
    // removePlayer
    // =========================================================
    @Nested
    @DisplayName("removePlayer")
    class RemovePlayer {

        @Test
        @DisplayName("Elimina jugador correctamente cuando no hay torneo activo")
        void removePlayer_success() {
            team.setPlayers(new ArrayList<>(Arrays.asList(10L, 20L)));
            team.setCurrentPlayers(2);
            team.setTournamentStatus(Team.TournamentStatus.NONE);

            when(teamRepository.findById(1L)).thenReturn(Optional.of(team));
            when(teamRepository.save(any(Team.class))).thenReturn(team);
            when(teamMapper.toDto(team)).thenReturn(responseDTO);

            TeamResponseDTO result = teamService.removePlayer(1L, 20L);

            assertNotNull(result);
            assertThat(team.getPlayers()).doesNotContain(20L);
        }

        @Test
        @DisplayName("Lanza IllegalStateException si equipo está en torneo activo")
        void removePlayer_activeTournament_throwsException() {
            team.setTournamentStatus(Team.TournamentStatus.ACTIVE);
            when(teamRepository.findById(1L)).thenReturn(Optional.of(team));

            IllegalStateException ex = assertThrows(IllegalStateException.class,
                    () -> teamService.removePlayer(1L, 20L));

            assertThat(ex.getMessage()).contains("Activo o En Progreso");
        }

        @Test
        @DisplayName("Lanza IllegalStateException si se intenta eliminar al capitán")
        void removePlayer_captain_throwsException() {
            team.setTournamentStatus(Team.TournamentStatus.NONE);
            team.setPlayers(new ArrayList<>(Arrays.asList(10L, 20L)));
            when(teamRepository.findById(1L)).thenReturn(Optional.of(team));

            assertThrows(IllegalStateException.class,
                    () -> teamService.removePlayer(1L, 10L)); // 10L es el capitán
        }

        @Test
        @DisplayName("Lanza ResourceNotFoundException si equipo no existe")
        void removePlayer_teamNotFound() {
            when(teamRepository.findById(99L)).thenReturn(Optional.empty());

            assertThrows(ResourceNotFoundException.class,
                    () -> teamService.removePlayer(99L, 20L));
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
            List<Long> requests = new ArrayList<>(List.of(30L, 40L));
            team.setRequests(requests);
            when(teamRepository.findById(1L)).thenReturn(Optional.of(team));

            List<Long> result = teamService.getPendingRequest(1L, 10L);

            assertThat(result).containsExactly(30L, 40L);
        }

        @Test
        @DisplayName("No capitán lanza UnauthorizedException")
        void getPendingRequest_notCaptain_throwsException() {
            when(teamRepository.findById(1L)).thenReturn(Optional.of(team));

            assertThrows(UnauthorizedException.class,
                    () -> teamService.getPendingRequest(1L, 99L)); // 99L no es capitán
        }

        @Test
        @DisplayName("Lanza ResourceNotFoundException si equipo no existe")
        void getPendingRequest_teamNotFound() {
            when(teamRepository.findById(99L)).thenReturn(Optional.empty());

            assertThrows(ResourceNotFoundException.class,
                    () -> teamService.getPendingRequest(99L, 10L));
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
            List<Long> requests = new ArrayList<>(List.of(30L));
            team.setRequests(requests);
            when(teamRepository.findById(1L)).thenReturn(Optional.of(team));
            when(teamRepository.save(any(Team.class))).thenReturn(team);

            assertDoesNotThrow(() -> teamService.rejectRequest(1L, 30L, 10L, null));
            assertThat(team.getRequests()).doesNotContain(30L);
        }

        @Test
        @DisplayName("Lanza UnauthorizedException si userId es null")
        void rejectRequest_nullUserId_throwsException() {
            when(teamRepository.findById(1L)).thenReturn(Optional.of(team));

            assertThrows(UnauthorizedException.class,
                    () -> teamService.rejectRequest(1L, 30L, null, null));
        }

        @Test
        @DisplayName("Lanza UnauthorizedException si no es el capitán")
        void rejectRequest_notCaptain_throwsException() {
            when(teamRepository.findById(1L)).thenReturn(Optional.of(team));

            assertThrows(UnauthorizedException.class,
                    () -> teamService.rejectRequest(1L, 30L, 99L, null));
        }

        @Test
        @DisplayName("Lanza ResourceNotFoundException si equipo no existe")
        void rejectRequest_teamNotFound() {
            when(teamRepository.findById(99L)).thenReturn(Optional.empty());

            assertThrows(ResourceNotFoundException.class,
                    () -> teamService.rejectRequest(99L, 30L, 10L, null));
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
            List<Long> requests = new ArrayList<>(List.of(30L));
            team.setRequests(requests);
            team.setPlayers(new ArrayList<>(List.of(10L)));
            team.setCurrentPlayers(1);
            when(teamRepository.findById(1L)).thenReturn(Optional.of(team));
            when(teamRepository.save(any(Team.class))).thenReturn(team);

            assertDoesNotThrow(() -> teamService.acceptRequest(1L, 30L, 10L, null));
            assertThat(team.getPlayers()).contains(30L);
            assertThat(team.getRequests()).doesNotContain(30L);
        }

        @Test
        @DisplayName("Lanza UnauthorizedException si userId es null")
        void acceptRequest_nullUserId_throwsException() {
            when(teamRepository.findById(1L)).thenReturn(Optional.of(team));

            assertThrows(UnauthorizedException.class,
                    () -> teamService.acceptRequest(1L, 30L, null, null));
        }

        @Test
        @DisplayName("Lanza UnauthorizedException si no es el capitán")
        void acceptRequest_notCaptain_throwsException() {
            when(teamRepository.findById(1L)).thenReturn(Optional.of(team));

            assertThrows(UnauthorizedException.class,
                    () -> teamService.acceptRequest(1L, 30L, 99L, null));
        }

        @Test
        @DisplayName("Lanza ResourceNotFoundException si equipo no existe")
        void acceptRequest_teamNotFound() {
            when(teamRepository.findById(99L)).thenReturn(Optional.empty());

            assertThrows(ResourceNotFoundException.class,
                    () -> teamService.acceptRequest(99L, 30L, 10L, null));
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
            team.setPlayers(new ArrayList<>(List.of(10L)));
            team.setCurrentPlayers(1);
            when(teamRepository.findById(1L)).thenReturn(Optional.of(team));
            when(teamRepository.existsPlayerInAnyTeam(50L)).thenReturn(false);
            when(teamRepository.save(any(Team.class))).thenReturn(team);

            assertDoesNotThrow(() -> teamService.sendRequest(1L, 50L));
            assertThat(team.getRequests()).contains(50L);
        }

        @Test
        @DisplayName("Lanza IllegalStateException si equipo lleno")
        void sendRequest_teamFull_throwsException() {
            List<Long> fullPlayers = new ArrayList<>();
            for (long i = 1; i <= 12; i++) fullPlayers.add(i);
            team.setPlayers(fullPlayers);
            team.setCurrentPlayers(12);
            when(teamRepository.findById(1L)).thenReturn(Optional.of(team));

            assertThrows(IllegalStateException.class,
                    () -> teamService.sendRequest(1L, 50L));
        }

        @Test
        @DisplayName("Lanza IllegalStateException si jugador ya está en el equipo")
        void sendRequest_playerAlreadyInTeam_throwsException() {
            team.setPlayers(new ArrayList<>(List.of(10L, 20L)));
            team.setCurrentPlayers(2);
            when(teamRepository.findById(1L)).thenReturn(Optional.of(team));

            assertThrows(IllegalStateException.class,
                    () -> teamService.sendRequest(1L, 20L)); // 20L ya está
        }

        @Test
        @DisplayName("Lanza IllegalStateException si jugador pertenece a otro equipo")
        void sendRequest_playerInAnotherTeam_throwsException() {
            team.setPlayers(new ArrayList<>(List.of(10L)));
            team.setCurrentPlayers(1);
            when(teamRepository.findById(1L)).thenReturn(Optional.of(team));
            when(teamRepository.existsPlayerInAnyTeam(50L)).thenReturn(true);

            assertThrows(IllegalStateException.class,
                    () -> teamService.sendRequest(1L, 50L));
        }

        @Test
        @DisplayName("Lanza IllegalStateException si jugador ya tiene solicitud pendiente")
        void sendRequest_duplicateRequest_throwsException() {
            team.setPlayers(new ArrayList<>(List.of(10L)));
            team.setCurrentPlayers(1);
            team.setRequests(new ArrayList<>(List.of(50L)));
            when(teamRepository.findById(1L)).thenReturn(Optional.of(team));
            when(teamRepository.existsPlayerInAnyTeam(50L)).thenReturn(false);

            assertThrows(IllegalStateException.class,
                    () -> teamService.sendRequest(1L, 50L));
        }

        @Test
        @DisplayName("Lanza ResourceNotFoundException si equipo no existe")
        void sendRequest_teamNotFound() {
            when(teamRepository.findById(99L)).thenReturn(Optional.empty());

            assertThrows(ResourceNotFoundException.class,
                    () -> teamService.sendRequest(99L, 50L));
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
            team.setPlayers(new ArrayList<>(List.of(10L)));
            team.setCurrentPlayers(1);
            when(teamRepository.findByCode("ABC123")).thenReturn(Optional.of(team));
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

        @Test
        @DisplayName("Lanza IllegalStateException si equipo lleno")
        void sendRequesBycode_teamFull_throwsException() {
            List<Long> fullPlayers = new ArrayList<>();
            for (long i = 1; i <= 12; i++) fullPlayers.add(i);
            team.setPlayers(fullPlayers);
            team.setCurrentPlayers(12);
            when(teamRepository.findByCode("ABC123")).thenReturn(Optional.of(team));

            assertThrows(IllegalStateException.class,
                    () -> teamService.sendRequesBycode("ABC123", 50L));
        }

        @Test
        @DisplayName("Lanza IllegalStateException si jugador ya tiene solicitud pendiente")
        void sendRequesBycode_duplicateRequest_throwsException() {
            team.setPlayers(new ArrayList<>(List.of(10L)));
            team.setCurrentPlayers(1);
            team.setRequests(new ArrayList<>(List.of(50L)));
            when(teamRepository.findByCode("ABC123")).thenReturn(Optional.of(team));

            assertThrows(IllegalStateException.class,
                    () -> teamService.sendRequesBycode("ABC123", 50L));
        }
    }
}
