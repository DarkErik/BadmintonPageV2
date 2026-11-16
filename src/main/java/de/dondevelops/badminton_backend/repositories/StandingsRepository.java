package de.dondevelops.badminton_backend.repositories;

import de.dondevelops.badminton_backend.dtos.Standing;
import de.dondevelops.badminton_backend.dtos.StandingPK;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface StandingsRepository extends JpaRepository<Standing, StandingPK> {
    List<Standing> findByTeamIdOrderByOrdinalAsc(Integer teamId);
}
