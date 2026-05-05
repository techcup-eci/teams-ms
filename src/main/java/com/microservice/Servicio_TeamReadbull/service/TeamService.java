package com.microservice.Servicio_TeamReadbull.service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.microservice.Servicio_TeamReadbull.dto.Request.TeamRequestDTO;
import com.microservice.Servicio_TeamReadbull.dto.Response.UserProfileDTO;
import com.microservice.Servicio_TeamReadbull.model.Team;
import com.microservice.Servicio_TeamReadbull.repository.TeamRepository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class TeamService {

    @Autowired
    private TeamRepository teamRepository;

    @Autowired
    private RestTemplate restTemplate;

    // IMPORTANTE: URL del microservicio de usuarios. ¡Pregúntale a tu equipo el puerto exacto!
    private final String USUARIOS_MS_URL = "http://localhost:8081/api/users/";

    public Team createTeam(TeamRequestDTO request) {
        log.info("Creando equipo. IDs recibidos: {}", request.getPlayers());

        // 1. Regla básica: Mínimo 7, máximo 12[cite: 2]
        if (request.getPlayers().size() < 7 || request.getPlayers().size() > 12) {
            throw new IllegalArgumentException("El equipo debe tener entre 7 y 12 jugadores.");
        }

        // 2. Usar los métodos de validación conectados al Microservicio de Usuarios[cite: 2]
        if (!playersWithDiferentDorsal(request.getPlayers())) {
            throw new IllegalArgumentException("No puede haber jugadores con el mismo dorsal en el equipo.");
        }

        if (!halfOfStudentsAreOfAllowedPrograms(request.getPlayers())) {
            throw new IllegalArgumentException("Más de la mitad del equipo debe pertenecer a Sistemas, IA, Ciberseguridad o Estadística.");
        }

        // 3. Guardar en BD (Solo nuestra info y la lista de IDs)[cite: 2]
        Team team = new Team();
        team.setName(request.getName());
        team.setColors(request.getColors());
        team.setIdCaptain(request.getCaptainId()); 
        team.setPhoto(request.getPhoto());
        team.setPlayers(request.getPlayers());
        team.setCurrentPlayers(request.getPlayers().size());
        team.setValidTeam(true); 

        return teamRepository.save(team);
    }

    public boolean playersWithDiferentDorsal(List<Long> playerIds) {
        Set<Integer> dorsals = new HashSet<>();
        
        for (Long id : playerIds) {
            try {
                // El RestTemplate hace la petición HTTP al otro microservicio
                UserProfileDTO user = restTemplate.getForObject(USUARIOS_MS_URL + id, UserProfileDTO.class);
                
                if (user != null && user.getDorsal() != null) {
                    if (!dorsals.add(user.getDorsal())) {
                        return false; // Si no se pudo añadir al Set, es porque ya existía un dorsal igual[cite: 2]
                    }
                }
            } catch (Exception e) {
                log.error("Error al consultar el usuario con ID {}: {}", id, e.getMessage());
                throw new RuntimeException("No se pudo comunicar con el MS de Usuarios para verificar al jugador " + id);
            }
        }
        return true; 
    }

    public boolean halfOfStudentsAreOfAllowedPrograms(List<Long> playerIds) {
        // Los programas permitidos según los criterios de aceptación[cite: 2]
        List<String> allowedPrograms = List.of(
            "Ingeniería de Sistemas", "Ingeniería de IA", 
            "Ingeniería de Ciberseguridad", "Ingeniería Estadística"
        );
        
        int countAllowed = 0;
        
        for (Long id : playerIds) {
            try {
                UserProfileDTO user = restTemplate.getForObject(USUARIOS_MS_URL + id, UserProfileDTO.class);
                
                if (user != null && allowedPrograms.contains(user.getAcademicProgram())) {
                    countAllowed++;
                }
            } catch (Exception e) {
                log.error("Error al consultar el usuario con ID {}: {}", id, e.getMessage());
                throw new RuntimeException("No se pudo comunicar con el MS de Usuarios para verificar el programa del jugador " + id);
            }
        }
    
        return countAllowed > (playerIds.size() / 2.0); 
    }
}