package de.dondevelops.badminton_backend.services;

import com.example.model.StandingEntry;
import de.dondevelops.badminton_backend.dtos.Standing;
import de.dondevelops.badminton_backend.mapper.StandingMapper;
import de.dondevelops.badminton_backend.repositories.StandingsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StandingsService {

    private final StandingMapper mapper;

    private final StandingsRepository repo;

    public void saveStanding(Standing standing) {
        repo.save(standing);
    }

    public List<StandingEntry> getStandingsByTeamOrdered(Integer teamID) {
        return mapper.mapTo(repo.findByTeamIdOrderByOrdinalAsc(teamID));
    }

}
