package edu.eci.dosw.teamsms.service;

import edu.eci.dosw.teamsms.dto.TeamRequestDTO;
import edu.eci.dosw.teamsms.dto.PlayerRequestDTO;
import edu.eci.dosw.teamsms.entity.Team;
import edu.eci.dosw.teamsms.entity.TeamPlayer;
import edu.eci.dosw.teamsms.repository.TeamRepository;
import edu.eci.dosw.teamsms.repository.TeamPlayerRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
public class TeamService {

    @Autowired
    private TeamRepository teamRepository;

    @Autowired
    private TeamPlayerRepository teamPlayerRepository;

    private static final List<String> VALID_PROGRAMS = List.of(
            "Ingeniería de Sistemas", "Ingeniería de IA", 
            "Ingeniería de Ciberseguridad", "Ingeniería Estadística"
    );

    @Transactional
    public Team createTeam(TeamRequestDTO request) {
        log.info("Iniciando validaciones para crear equipo: {}", request.getName());

        List<PlayerRequestDTO> players = request.getPlayers();

        // 1. Validar cantidad mínima y máxima de jugadores (7 a 12)
        if (players.size() < 7 || players.size() > 12) {
            throw new IllegalArgumentException("El equipo debe tener entre 7 y 12 jugadores.");
        }

        // 2. Validar dorsales duplicados en el mismo equipo
        Set<Integer> dorsals = players.stream().map(PlayerRequestDTO::getDorsal).collect(Collectors.toSet());
        if (dorsals.size() != players.size()) {
            throw new IllegalArgumentException("No puede haber jugadores con el mismo dorsal en el equipo.");
        }

        // 3. Validar proporción de programas académicos (> 50%)
        long targetProgramCount = players.stream()
                .filter(p -> VALID_PROGRAMS.contains(p.getAcademicProgram()))
                .count();
        if (targetProgramCount <= players.size() / 2.0) {
            throw new IllegalArgumentException("Más de la mitad del equipo debe pertenecer a Sistemas, IA, Ciberseguridad o Estadística.");
        }

        // 4. Validar que ningún jugador esté ya en otro equipo
        for (PlayerRequestDTO player : players) {
            if (teamPlayerRepository.existsByUserId(player.getUserId())) {
                throw new IllegalArgumentException("El jugador con ID " + player.getUserId() + " ya pertenece a otro equipo.");
            }
        }

        // Mapeo y guardado
        Team team = new Team();
        team.setName(request.getName());
        team.setColors(request.getColors());
        team.setCaptainId(request.getCaptainId());
        team.setState("CREATED");

        List<TeamPlayer> teamPlayers = players.stream().map(dto -> {
            TeamPlayer tp = new TeamPlayer();
            tp.setUserId(dto.getUserId());
            tp.setDorsal(dto.getDorsal());
            tp.setAcademicProgram(dto.getAcademicProgram());
            tp.setTeam(team);
            return tp;
        }).collect(Collectors.toList());

        team.setPlayers(teamPlayers);
        return teamRepository.save(team);
    }
}