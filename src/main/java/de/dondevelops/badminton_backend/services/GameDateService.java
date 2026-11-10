package de.dondevelops.badminton_backend.services;

import com.example.model.GameDateInfo;
import de.dondevelops.badminton_backend.dtos.GameDateDto;
import de.dondevelops.badminton_backend.dtos.GameDto;
import de.dondevelops.badminton_backend.repositories.GameDateRepository;
import de.dondevelops.badminton_backend.repositories.GameRepository;
import de.dondevelops.badminton_backend.util.DateParser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.el.parser.BooleanNode;
import org.springframework.cglib.core.Local;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class GameDateService {

    private final GameDateRepository gameDateRepo;
    private final GameRepository gameRepo;

    public Optional<GameDateDto> getGameDateChangesForGameId(int gameId) {
        return gameDateRepo.findByGameId(gameId);
    }

    public void addChangedDateIfFeasible(GameDateDto request) {
        if (gameRepo.findById(request.getGameId()).isEmpty()) {
            log.error("Game with id {} does not exist; Cannot add date!", request.getGameId());
            return;
        }

        GameDateDto latestDate = getGameDateChangesForGameId(request.getGameId()).orElse(null);
        if (latestDate != null) {
            if (request.getCreated_at().isBefore(latestDate.getCreated_at()))
                // Date too old
                return;

            if (request.getGameTime().equals(latestDate.getGameTime())) {
                if (!request.getApproved())
                    // Same date but potentially disapproved
                    return;
                if (latestDate.getApproved())
                    // Duplicate date
                    return;
            }

            if (!request.getApproved() &&
                    latestDate.getGameTime()
                            .atZone(ZoneId.of("Europe/Berlin"))
                            .toLocalDateTime()
                            .withHour(0)
                            .withMinute(0)
                            .withSecond(0)
                            .withNano(0)
                            .atZone(ZoneId.of("Europe/Berlin"))
                            .toInstant()
                            .equals(request.getGameTime()))
                // loss of minutes and hours
                return;

        }

        // date accepted
        gameDateRepo.save(request);
    }

    public List<GameDateInfo> getGameDateInfosForTeam(int teamId) {
        List<GameDto> games = gameRepo.getGamesByTeam(teamId);

        return games.stream()
                .map(g -> gameDateRepo.findByGameId(g.getId()))
                .map(opt -> opt.map(
                            game -> GameDateInfo.builder()
                                    .time(DateParser.instanceToISO(game.getGameTime()))
                                    .approved(game.getApproved())
                                    .build()
                        ).orElse(null))
                .toList();
    }

    public void approveDate(int teamId, int gameIndex) {
        Optional<GameDto> game = gameRepo.findGame(teamId, gameIndex);
        game.flatMap(g -> gameDateRepo.findByGameId(g.getId())).ifPresent(gd -> {
            if (!Boolean.TRUE.equals(gd.getApproved())) {
                gd.setApproved(true);
                gameDateRepo.save(gd);
            }

        });
    }
}
