package de.dondevelops.badminton_backend.mapper;


import com.example.model.StandingEntry;
import de.dondevelops.badminton_backend.dtos.Standing;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface StandingMapper {

    @Mapping(source = "standing.displayedRank", target = "displayRank")
    @Mapping(source = "standing.clubName", target = "clubName")
    @Mapping(source = "standing.playedGames", target = "playedGames")
    @Mapping(source = "standing.wonPoints", target = "wonPoints")
    @Mapping(source = "standing.lostPoints", target = "lostPoints")
    StandingEntry mapTo(Standing standing);

    List<StandingEntry> mapTo(List<Standing> standing);
}


