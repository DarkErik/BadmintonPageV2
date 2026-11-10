package de.dondevelops.badminton_backend;

import com.example.model.AddPlayerRequest;
import de.dondevelops.badminton_backend.dtos.GameDateDto;
import de.dondevelops.badminton_backend.dtos.GameDto;
import de.dondevelops.badminton_backend.dtos.TeamDto;
import de.dondevelops.badminton_backend.services.GameDateService;
import de.dondevelops.badminton_backend.services.GameService;
import de.dondevelops.badminton_backend.services.PlayerService;
import lombok.RequiredArgsConstructor;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import tools.jackson.core.json.JsonFactory;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.json.JsonMapper;

import java.io.File;
import java.time.Instant;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
@Order(2)
public class OldBackendDataLoader implements CommandLineRunner {
//    private static final String OLD_DATA_PATH = "C:\\Users\\Erik\\Downloads\\data";
//    private static final String OLD_DATA_PATH = "oldData";
    private static final String OLD_DATA_PATH = "-";

    private final GameService gameService;
    private final GameDateService gameDateService;
    private final PlayerService playerService;

    @Override
    public void run(String... args) throws Exception {
        if (true)
            return;
        JsonMapper jsonReader = new JsonMapper(new JsonFactory());
        List<TeamDto> teams = gameService.getAllTeams();

        for(TeamDto team : teams) {
            log.info("Load team: {}", team.getTeamName());
            List<GameDto> games = gameService.getGamesByTeam(team.getId());

            File dataFile = new File(OLD_DATA_PATH + File.separator + team.getTeamUrl() + File.separator + "dates.json");
            if (dataFile.exists())
                importDataNode(jsonReader.readTree(dataFile), games);
            else
                log.info("FILE DOES NOT EXIST: {}", dataFile);

            File playersFile = new File(OLD_DATA_PATH + File.separator + team.getTeamUrl() + File.separator + "players.json");
            if (playersFile.exists())
                importPlayerNode(jsonReader.readTree(playersFile), games);
            else
                log.info("FILE DOES NOT EXIST: {}", playersFile);


        }
    }

    private void importDataNode(JsonNode node, List<GameDto> games) {
        log.info("Import dates...");
        for(GameDto game : games) {
            JsonNode local = node.path(game.getGameOrdinal());
            int datesSize = local.path("dates").size();
            if (datesSize > 0) {
                log.info("Added date!");
                gameDateService.addChangedDateIfFeasible(
                        GameDateDto.builder()
                                .gameId(game.getId())
                                .gameTime(Instant.parse(local.path("dates").path(datesSize - 1).asString()))
                                .created_at(Instant.parse(local.path("registeredTime").path(datesSize - 1).asString()))
                                .approved(local.path("approved").asBoolean())
                                .build());
            }
        }
    }

    private void importPlayerNode(JsonNode node, List<GameDto> games) {
        log.info("Import players...");
        int playerIndx = 0;
        for(GameDto game : games) {
            log.info("Game: {}", game.getGameOrdinal());
            JsonNode local = node.path(game.getGameOrdinal());
            for(JsonNode peep : local.path("registeredMans"))
                playerService.addPlayer(game.getTeamId(), AddPlayerRequest.builder()
                                .gameIndex(game.getGameOrdinal())
                                .man(true)
                                .maybe(false)
                                .name(peep.asString())
                        .build());

            for(JsonNode peep : local.path("registeredMaybeMans"))
                playerService.addPlayer(game.getTeamId(), AddPlayerRequest.builder()
                        .gameIndex(game.getGameOrdinal())
                        .man(true)
                        .maybe(true)
                        .name(peep.asString())
                        .build());

            for(JsonNode peep : local.path("registeredWoman"))
                playerService.addPlayer(game.getTeamId(), AddPlayerRequest.builder()
                        .gameIndex(game.getGameOrdinal())
                        .man(false)
                        .maybe(false)
                        .name(peep.asString())
                        .build());

            for(JsonNode peep : local.path("registeredMaybeWoman")) {
                playerService.addPlayer(game.getTeamId(), AddPlayerRequest.builder()
                        .gameIndex(game.getGameOrdinal())
                        .man(false)
                        .maybe(true)
                        .name(peep.asString())
                        .build());
            }
        }
    }
}
