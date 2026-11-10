package de.dondevelops.badminton_backend.services;

import com.example.model.AddPlayerRequest;
import com.example.model.PlayerInfo;
import com.example.model.RemovePlayerRequest;
import de.dondevelops.badminton_backend.dtos.GameDto;
import de.dondevelops.badminton_backend.dtos.PlayerDto;
import de.dondevelops.badminton_backend.dtos.PlayerPK;
import de.dondevelops.badminton_backend.dtos.TeamDto;
import de.dondevelops.badminton_backend.repositories.GameRepository;
import de.dondevelops.badminton_backend.repositories.PlayerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.swing.text.html.Option;
import java.time.Instant;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class PlayerService {
    private final PlayerRepository playerRepo;
    private final GameRepository gameRepo;

    public List<PlayerInfo> getPlayerInfoForTeam(int teamId) {

        List<GameDto> games = gameRepo.getGamesByTeamOrderByGameOrdinalAsc(teamId);

        return games.stream()
                .map( g -> {
                    List<PlayerDto> players = playerRepo.findByGameIdOrderByCreatedAtAsc(g.getId());
                    return PlayerInfo.builder()
                            .registeredMans(
                                    players.stream()
                                            .filter(p -> p.getMan() && !p.getReplacement())
                                            .map(PlayerDto::getName)
                                            .toList())
                            .registeredMaybeMans(
                                    players.stream()
                                            .filter(p -> p.getMan() && p.getReplacement())
                                            .map(PlayerDto::getName)
                                            .toList())
                            .registeredWoman(
                                    players.stream()
                                            .filter(p -> !p.getMan() && !p.getReplacement())
                                            .map(PlayerDto::getName)
                                            .toList())
                            .registeredMaybeWoman(
                                    players.stream()
                                            .filter(p -> !p.getMan() && p.getReplacement())
                                            .map(PlayerDto::getName)
                                            .toList())
                            .build();
                }).toList();
    }

    public void addPlayer(int teamId, AddPlayerRequest req) {
        Optional<GameDto> game = gameRepo.findGame(teamId, req.getGameIndex());
        if (game.isEmpty()) {
            log.info("No game found: {}", req.getGameIndex());
            return; // No corresponding game found
        }
        playerRepo.save(
            PlayerDto.builder()
                    .gameId(game.get().getId())
                    .name(req.getName())
                    .replacement(req.getMaybe())
                    .man(req.getMan())
                    .createdAt(Instant.now())
                    .build()
        );

    }


    public void removePlayer(int teamId, RemovePlayerRequest req) {
        Optional<GameDto> game = gameRepo.findGame(teamId, req.getGameIndex());
        if (game.isEmpty())
            return; // No corresponding game found
        game.flatMap(g -> playerRepo.findPlayerByGameIdAndNameAndMan(g.getId(), req.getName(), req.getMan()))
                .ifPresent(p -> playerRepo.deleteById(new PlayerPK(game.get().getId(), req.getName(), req.getMan())));

    }

}
