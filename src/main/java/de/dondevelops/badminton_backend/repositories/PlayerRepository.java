package de.dondevelops.badminton_backend.repositories;

import de.dondevelops.badminton_backend.dtos.PlayerDto;
import de.dondevelops.badminton_backend.dtos.PlayerPK;
import org.jspecify.annotations.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PlayerRepository extends JpaRepository<PlayerDto, PlayerPK> {

    List<PlayerDto> findByGameIdOrderByCreatedAtAsc(Integer gameId);

    void deleteById(@NonNull PlayerPK playerId);

    Optional<PlayerDto> findPlayerByGameIdAndNameAndMan(Integer gameId, String name, boolean man);
}
