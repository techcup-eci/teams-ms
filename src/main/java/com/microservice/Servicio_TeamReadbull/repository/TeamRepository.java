package com.microservice.Servicio_TeamReadbull.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.microservice.Servicio_TeamReadbull.model.Team;

public interface TeamRepository extends JpaRepository<Team, Long> {

    Optional<Team> findByNameContainingIgnoreCase(String name);

    Team findByName(String name);

    List<Team> findAll();

    Optional<Team> findByCode(String code);

    @Query("SELECT COUNT(t) > 0 FROM Team t JOIN t.players p WHERE p = :jugadorId")
    boolean existsPlayerInAnyTeam(@Param("jugadorId") Long jugadorId);

    /** Check if a captain already has a team for a specific tournament */
    boolean existsByIdCaptainAndIdTournament(Long captainId, String idTournament);

    /** Find the team where the user is captain */
    Optional<Team> findByIdCaptain(Long captainId);
}
