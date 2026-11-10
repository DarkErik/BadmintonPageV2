package de.dondevelops.badminton_backend.dtos;

import de.dondevelops.badminton_backend.util.InstantConverter;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.time.LocalDateTime;

@Entity
@Table(name = "comments")
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CommentDto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;


    @Column(name = "game_id", nullable = false)
    private Integer gameId;

    @Column(name = "author", nullable = false)
    private String author;

    @Column(name = "creation_date", nullable = false)
    @Convert(converter = InstantConverter.class)
    private Instant creationDate;

    @Column(name = "text", nullable = false)
    private String text;

}