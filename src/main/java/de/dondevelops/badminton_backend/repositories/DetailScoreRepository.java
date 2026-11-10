package de.dondevelops.badminton_backend.repositories;

import de.dondevelops.badminton_backend.dtos.DetailScoreDto;
import de.dondevelops.badminton_backend.dtos.DetailScorePK;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DetailScoreRepository extends JpaRepository<DetailScoreDto, DetailScorePK> {
    List<DetailScoreDto> findByGameIdOrderByOrdinalAsc(Integer gameId);
}
