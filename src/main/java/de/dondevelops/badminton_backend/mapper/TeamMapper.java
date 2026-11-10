package de.dondevelops.badminton_backend.mapper;

import com.example.model.TeamInfo;
import de.dondevelops.badminton_backend.dtos.TeamDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface TeamMapper {

    @Mapping(source = "team.teamUrl", target = "teamUrl")
    @Mapping(source = "team.teamName", target = "teamName")
    @Mapping(source = "team.mini", target = "mini")
    TeamInfo toTeamInfo(TeamDto team);
}
