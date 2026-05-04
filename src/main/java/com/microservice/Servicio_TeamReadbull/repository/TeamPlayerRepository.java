package com.microservice.Servicio_TeamReadbull.repository;

import com.microservice.Servicio_TeamReadbull.model.TeamPlayer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TeamPlayerRepository extends JpaRepository<TeamPlayer, Long> {
    boolean existsByUserId(Long userId);
}