package com.microservice.Servicio_TeamReadbull;

import com.microservice.Servicio_TeamReadbull.dto.Request.TeamRequestDTO;
import com.microservice.Servicio_TeamReadbull.repository.TeamRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/** 
@ExtendWith(MockitoExtension.class)
public class TeamServiceTest {

    @Mock
    private TeamRepository teamRepository;

    @Mock
    private TeamPlayerRepository teamPlayerRepository;

    @InjectMocks
    private TeamService teamService;

    private TeamRequestDTO validRequest;

    @BeforeEach
    void setUp() {
        validRequest = new TeamRequestDTO();
        validRequest.setName("Los Galácticos");
        validRequest.setColors("Blanco y Azul");
        validRequest.setCaptainId(1L);

        List<PlayerRequestDTO> players = new ArrayList<>();
        // Creamos 7 jugadores válidos (4 de Sistemas para cumplir la regla > 50%)
        for (int i = 1; i <= 7; i++) {
            PlayerRequestDTO p = new PlayerRequestDTO();
            p.setUserId((long) i);
            p.setDorsal(i);
            p.setAcademicProgram(i <= 4 ? "Ingeniería de Sistemas" : "Ingeniería Civil");
            players.add(p);
        }
        validRequest.setPlayers(players);
    }

    @Test
    void createTeam_Success() {
        when(teamPlayerRepository.existsByUserId(anyLong())).thenReturn(false);
        when(teamRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        assertDoesNotThrow(() -> teamService.createTeam(validRequest));
        verify(teamRepository, times(1)).save(any());
    }

    @Test
    void createTeam_FailsWhenLessThan7Players() {
        validRequest.getPlayers().remove(0); 
        
        Exception exception = assertThrows(IllegalArgumentException.class, () -> teamService.createTeam(validRequest));
        assertEquals("El equipo debe tener entre 7 y 12 jugadores.", exception.getMessage());
    }

    @Test
    void createTeam_FailsWhenDuplicateDorsal() {
        validRequest.getPlayers().get(1).setDorsal(1); 
        
        Exception exception = assertThrows(IllegalArgumentException.class, () -> teamService.createTeam(validRequest));
        assertEquals("No puede haber jugadores con el mismo dorsal en el equipo.", exception.getMessage());
    }

    @Test
    void createTeam_FailsWhenProgramProportionIsLow() {
        validRequest.getPlayers().forEach(p -> p.setAcademicProgram("Ingeniería Civil"));
        validRequest.getPlayers().get(0).setAcademicProgram("Ingeniería de Sistemas");
        validRequest.getPlayers().get(1).setAcademicProgram("Ingeniería de IA");
        validRequest.getPlayers().get(2).setAcademicProgram("Ingeniería Estadística");

        Exception exception = assertThrows(IllegalArgumentException.class, () -> teamService.createTeam(validRequest));
        assertEquals("Más de la mitad del equipo debe pertenecer a Sistemas, IA, Ciberseguridad o Estadística.", exception.getMessage());
    }
}
*/