package com.microservice.Servicio_TeamReadbull.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.microservice.Servicio_TeamReadbull.model.Team;

public interface TeamRepository extends JpaRepository<Team, Long> {

    Optional<Team> findByNameContainingIgnoreCase(String name);

    Team findByName(String name);

    List<Team> findAll();
}
