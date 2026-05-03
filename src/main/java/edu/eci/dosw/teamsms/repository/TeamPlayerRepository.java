package edu.eci.dosw.teamsms.repository;

import edu.eci.dosw.teamsms.entity.TeamPlayer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TeamPlayerRepository extends JpaRepository<TeamPlayer, Long> {
    boolean existsByUserId(Long userId);
}
