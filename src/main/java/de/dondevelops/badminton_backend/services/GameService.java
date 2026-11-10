package de.dondevelops.badminton_backend.services;

import com.example.model.Comment;
import com.example.model.GameInfo;
import com.example.model.TeamInfo;
import de.dondevelops.badminton_backend.dtos.CommentDto;
import de.dondevelops.badminton_backend.dtos.DetailScoreDto;
import de.dondevelops.badminton_backend.dtos.GameDto;
import de.dondevelops.badminton_backend.dtos.TeamDto;
import de.dondevelops.badminton_backend.mapper.GameMapper;
import de.dondevelops.badminton_backend.mapper.TeamMapper;
import de.dondevelops.badminton_backend.repositories.CommentRepository;
import de.dondevelops.badminton_backend.repositories.DetailScoreRepository;
import de.dondevelops.badminton_backend.repositories.GameRepository;
import de.dondevelops.badminton_backend.repositories.TeamRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class GameService {
    private final GameMapper gameMapper;
    private final TeamMapper teamMapper;


    private final GameRepository gameRepo;
    private final DetailScoreRepository detailScoreRepo;
    private final CommentRepository commentRepo;
    private final TeamRepository teamRepo;

    public GameDto findGameOrEmptyDefault(int teamId, int gameOrdinal) {
        return gameRepo.findGame(teamId, gameOrdinal).orElse(GameDto.builder().gameOrdinal(gameOrdinal).teamId(teamId).build());
    }

    public Optional<Integer> getGameId(int teamId, int gameOrdinal) {
        return Optional.ofNullable(findGameOrEmptyDefault(teamId, gameOrdinal)).map(GameDto::getId);
    }

    public GameDto saveGames(GameDto game) {
        return gameRepo.save(game);
    }

    public void saveDetailScores(List<DetailScoreDto> detailScores) {
        detailScoreRepo.saveAll(detailScores);
    }

    public void saveComments(List<CommentDto> comments) {
        Map<String, List<CommentDto>> existingComments = commentRepo.findAll().stream().collect(Collectors.groupingBy(CommentDto::getText));

        List<CommentDto> filteredComments = comments.stream()
                .filter(c ->
                        !existingComments.containsKey(c.getText())
                        || existingComments.get(c.getText()).stream()
                                .noneMatch(existingComment ->
                                        existingComment.getCreationDate().equals(c.getCreationDate()))).toList();
        commentRepo.saveAll(filteredComments);
    }

    public List<GameDto> getGamesByTeam(int teamId) {
        return gameRepo.getGamesByTeam(teamId);
    }

    public List<GameInfo> getGameInfosByTeam(int teamId) {
        List<GameDto> games = gameRepo.getGamesByTeam(teamId);

        return games.stream().map(
                g ->
                    gameMapper.toGameInfo(g, detailScoreRepo.findByGameIdOrderByOrdinalAsc(g.getId()) ,commentRepo.findByGameIdOrderByCreationDateAsc(g.getId())
            )).toList();
    }

    public Optional<TeamInfo> getTeamInfoByUrl(String urlPath) {
        return teamRepo.findByTeamUrl(urlPath).map(teamMapper::toTeamInfo);
    }

    public Optional<TeamDto> getTeamByUrl(String urlPath) {
        return teamRepo.findByTeamUrl(urlPath);
    }

    public List<TeamDto> getAllTeams() {
        return teamRepo.findAll();
    }

    public void removeDuplicateComments() {
        int removedDuplicates = 0;
        Map<String, List<CommentDto>> groupedComments = commentRepo.findAll().stream().collect(Collectors.groupingBy(CommentDto::getText));
        for(var entry : groupedComments.entrySet()) {
            boolean first = true;
            for (CommentDto c : entry.getValue()) {
                if (first)
                    first = false;
                else {
                    commentRepo.delete(c);
                    removedDuplicates++;
                }
            }
        }
        log.info("Removed {} duplicate comments", removedDuplicates);
    }


}
