package de.dondevelops.badminton_backend.repositories;

import de.dondevelops.badminton_backend.dtos.GameDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.NativeQuery;

import java.util.List;
import java.util.Optional;

public interface GameRepository extends JpaRepository<GameDto, Integer> {

    @NativeQuery(value = "SELECT * FROM games WHERE team_id = :teamId and game_ordinal = :gameIndex LIMIT 1")
    Optional<GameDto> findGame(Integer teamId, Integer gameIndex);

    @NativeQuery(value = "SELECT * FROM games WHERE team_id = :teamId")
    List<GameDto> getGamesByTeam(Integer teamId);

    @NativeQuery(value = "SELECT * FROM games WHERE team_id = :teamId")
    List<GameDto> getGamesByTeamOrderByGameOrdinalAsc(Integer teamId);


}
