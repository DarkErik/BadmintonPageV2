package de.dondevelops.badminton_backend.controller;


import com.example.api.DefaultApi;
import com.example.model.*;
import de.dondevelops.badminton_backend.dtos.GameDateDto;
import de.dondevelops.badminton_backend.dtos.TeamDto;
import de.dondevelops.badminton_backend.services.CalenderService;
import de.dondevelops.badminton_backend.services.GameDateService;
import de.dondevelops.badminton_backend.services.GameService;
import de.dondevelops.badminton_backend.services.PlayerService;
import de.dondevelops.badminton_backend.util.DateParser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;

import java.time.Instant;
import java.util.List;

@Slf4j
@Controller
@RequiredArgsConstructor
public class RestController implements DefaultApi {

    private final GameService gameService;
    private final PlayerService playerService;
    private final GameDateService gameDateService;
    private final CalenderService calenderService;

    @Override
    public ResponseEntity<TeamInfo> getTeamInfo(String team) {
        log.info("Get-TeamInfo for: {}", team);
        return ResponseEntity.ok(gameService.getTeamInfoByUrl(team).orElseThrow()); // TODO check how orElseThrow works!
    }

    @Override
    public ResponseEntity<List<GameInfo>> getGames(String team) {
        log.info("Get-Games for: {}", team);
        TeamDto teamDto = gameService.getTeamByUrl(team).orElseThrow();

        return ResponseEntity.ok(gameService.getGameInfosByTeam(teamDto.getId()));

    }

    @Override
    public ResponseEntity<List<PlayerInfo>> addPlayer(String team, AddPlayerRequest addPlayerRequest) {
        log.info("Add player {} in game {} for team {}", addPlayerRequest.getName(), addPlayerRequest.getGameIndex(), team);
        TeamDto teamDto = gameService.getTeamByUrl(team).orElseThrow();

        playerService.addPlayer(teamDto.getId(), addPlayerRequest);

        return ResponseEntity.ok(playerService.getPlayerInfoForTeam(teamDto.getId()));
    }

    @Override
    public ResponseEntity<List<PlayerInfo>> getPlayers(String team) {
        log.info("Get Players for team {}", team);
        TeamDto teamDto = gameService.getTeamByUrl(team).orElseThrow();

        return ResponseEntity.ok(playerService.getPlayerInfoForTeam(teamDto.getId()));

    }

    @Override
    public ResponseEntity<List<PlayerInfo>> removePlayer(String team, RemovePlayerRequest removePlayerRequest) {
        log.info("Remove Player {} for game {} for team {}", removePlayerRequest.getName(), removePlayerRequest.getGameIndex(), team);
        TeamDto teamDto = gameService.getTeamByUrl(team).orElseThrow();

        playerService.removePlayer(teamDto.getId(), removePlayerRequest);

        return ResponseEntity.ok(playerService.getPlayerInfoForTeam(teamDto.getId()));
    }

    @Override
    public ResponseEntity<List<GameDateInfo>> getGameDates(String team) {
        log.info("Get GameDates for team: {}", team);
        TeamDto teamDto = gameService.getTeamByUrl(team).orElseThrow();
        return ResponseEntity.ok(gameDateService.getGameDateInfosForTeam(teamDto.getId()));
    }

    @Override
    public ResponseEntity<List<GameDateInfo>> approveDate(String team, Integer gameIndex) {
        log.info("Approve GameDate in game {} for team: {}", gameIndex, team);
        TeamDto teamDto = gameService.getTeamByUrl(team).orElseThrow();
        gameDateService.approveDate(teamDto.getId(), gameIndex);

        return ResponseEntity.ok(gameDateService.getGameDateInfosForTeam(teamDto.getId()));
    }

    @Override
    public ResponseEntity<List<GameDateInfo>> changeDate(String team, Integer gameIndex, ChangeDateRequest changeDateRequest) {
        log.info("Change GameDate in game {} for team {} to {}", gameIndex, team, changeDateRequest.getDate());
        TeamDto teamDto = gameService.getTeamByUrl(team).orElseThrow();

        Instant date = DateParser.stringToDate(changeDateRequest.getDate());
        if (date != null)
            gameDateService.addChangedDateIfFeasible(
                    GameDateDto.builder()
                            .gameId(gameService.getGameId(teamDto.getId(), gameIndex).orElseThrow())
                            .approved(true)
                            .created_at(Instant.now())
                            .gameTime(date)
                            .build()
            );

        return ResponseEntity.ok(gameDateService.getGameDateInfosForTeam(teamDto.getId()));
    }

    @Override
    public ResponseEntity<String> getCalendar(String team) {
        log.info("Requested Calender for team {}", team);
        TeamDto teamDto = gameService.getTeamByUrl(team).orElseThrow();
        return ResponseEntity.ok(calenderService.generateCalendar(teamDto.getId()));
    }
}
