package de.dondevelops.badminton_backend.repositories;

import de.dondevelops.badminton_backend.dtos.GameDateDto;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface GameDateRepository extends JpaRepository<GameDateDto, Integer> {
    Optional<GameDateDto> findByGameId(int gameId);
}
