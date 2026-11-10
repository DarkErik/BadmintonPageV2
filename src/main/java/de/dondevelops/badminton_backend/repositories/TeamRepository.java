package de.dondevelops.badminton_backend.repositories;

import de.dondevelops.badminton_backend.dtos.TeamDto;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TeamRepository extends JpaRepository<TeamDto, Long> {
    Optional<TeamDto> findByTeamUrl(String url);
}
