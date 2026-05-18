package com.microservice.Servicio_TeamReadbull.mappers;

import com.microservice.Servicio_TeamReadbull.dto.Request.TeamRequestDTO;
import com.microservice.Servicio_TeamReadbull.dto.Response.TeamResponseDTO;
import com.microservice.Servicio_TeamReadbull.model.Team;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class TeamMapperTest {

    @Autowired
    private TeamMapper teamMapper;

    @Test
    @DisplayName("toDto convierte Team a TeamResponseDTO correctamente")
    void toDto_mapsAllFields() {
        Team team = new Team();
        team.setId(1L);
        team.setName("Redbull FC");
        team.setIdCaptain(10L);
        team.setIdTournament(5L);
        team.setColors("Rojo y Azul");
        team.setPhoto("foto.png");
        team.setTournamentStatus(Team.TournamentStatus.NONE);
        team.setPlayers(new ArrayList<>(List.of(10L, 20L)));
        team.setCurrentPlayers(2);

        TeamResponseDTO dto = teamMapper.toDto(team);

        assertNotNull(dto);
        assertEquals(1L, dto.getId());
        assertEquals("Redbull FC", dto.getName());
        assertEquals(10L, dto.getCaptainId());
        assertEquals(5L, dto.getIdTournament());
        assertEquals("Rojo y Azul", dto.getColors());
        assertEquals("foto.png", dto.getPhoto());
        assertEquals(Team.TournamentStatus.NONE, dto.getTournamentStatus());
    }

    @Test
    @DisplayName("toDto con equipo mínimo no lanza excepción")
    void toDto_minimalTeam_doesNotThrow() {
        Team team = new Team();
        team.setId(2L);
        team.setName("Minimal FC");
        team.setIdCaptain(1L);
        team.setTournamentStatus(Team.TournamentStatus.DRAFT);
        team.setColors("Blanco");
        team.setPhoto("foto2.png");
        team.setPlayers(new ArrayList<>());
        team.setCurrentPlayers(0);

        assertDoesNotThrow(() -> teamMapper.toDto(team));
    }

    @Test
    @DisplayName("toEntity convierte TeamRequestDTO a Team correctamente")
    void toEntity_mapsFields() {
        TeamRequestDTO dto = TeamRequestDTO.builder()
                .name("Nuevo Equipo")
                .idTournament(3L)
                .colors("Verde")
                .photo("verde.png")
                .build();

        Team team = teamMapper.toEntity(dto);

        assertNotNull(team);
        assertEquals("Nuevo Equipo", team.getName());
        assertEquals(3L, team.getIdTournament());
        assertEquals("Verde", team.getColors());
        assertEquals("verde.png", team.getPhoto());
        // id debe ser ignorado (null)
        assertNull(team.getId());
    }

    @Test
    @DisplayName("toDto mapea lista de jugadores correctamente")
    void toDto_mapsPlayersList() {
        Team team = new Team();
        team.setId(3L);
        team.setName("Team Players");
        team.setIdCaptain(1L);
        team.setTournamentStatus(Team.TournamentStatus.NONE);
        team.setColors("Azul");
        team.setPhoto("foto.png");
        team.setPlayers(new ArrayList<>(List.of(1L, 2L, 3L, 4L, 5L)));
        team.setCurrentPlayers(5);

        TeamResponseDTO dto = teamMapper.toDto(team);

        assertNotNull(dto);
        assertThat(dto.getCurrentPlayers()).isEqualTo(5);
    }
}
