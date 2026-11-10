package de.dondevelops.badminton_backend.repositories;

import de.dondevelops.badminton_backend.dtos.CommentDto;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<CommentDto, Long> {
    List<CommentDto> findByGameIdOrderByCreationDateAsc(Integer gameId);
}
