package de.dondevelops.badminton_backend.mapper;

import com.example.model.Comment;
import com.example.model.GameDetailScore;
import com.example.model.GameInfo;
import de.dondevelops.badminton_backend.dtos.CommentDto;
import de.dondevelops.badminton_backend.dtos.DetailScoreDto;
import de.dondevelops.badminton_backend.dtos.GameDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.time.LocalDateTime;
import java.util.List;

@Mapper(componentModel = "spring")
public interface GameMapper {

    @Mapping(source = "game.gameTime", target = "time")
    @Mapping(source = "game.originalGameTime", target = "originalTime")
    @Mapping(source = "game.host", target = "host")
    @Mapping(source = "game.guest", target = "guest")
    @Mapping(source = "game.matchLink", target = "matchLink")
    @Mapping(source = "game.score", target = "score")
    @Mapping(source = "game.location", target = "location")
    @Mapping(source = "game.locationLink", target = "locationLink")
    @Mapping(source = "comments", target = "comments")
    @Mapping(source = "detailScores", target = "detailScores")
    GameInfo toGameInfo(GameDto game, List<DetailScoreDto> detailScores, List<CommentDto> comments);

    @Mapping(source = "gameType", target = "type")
    @Mapping(source = "hostPlayer", target = "hostPlayer")
    @Mapping(source = "guestPlayer", target = "guestPlayer")
    @Mapping(source = "sets", target = "sets")
    GameDetailScore toGameDetailScore(DetailScoreDto detailScore);

    @Mapping(source = "author", target = "author")
    @Mapping(source = "text", target = "text")
    @Mapping(source = "creationDate", target = "timeOfCreation")
    Comment toComment(CommentDto comment);

    default String map(LocalDateTime value) {
        return value == null ? null : value.toString();
    }


}
