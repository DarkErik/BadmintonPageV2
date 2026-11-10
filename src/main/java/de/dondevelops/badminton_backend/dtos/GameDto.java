package de.dondevelops.badminton_backend.dtos;

import de.dondevelops.badminton_backend.util.InstantConverter;
import jakarta.persistence.*;

import java.time.Instant;
import java.time.LocalDateTime;

import lombok.*;


@Entity
@Table(name = "games")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GameDto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "team_id", nullable = false)
    private Integer teamId;

    @Column(name = "game_ordinal", nullable = false)
    private Integer gameOrdinal;

    @Column(name = "game_time", nullable = false)
    @Convert(converter = InstantConverter.class)
    private Instant gameTime;

    @Column(name = "original_game_time", nullable = false)
    @Convert(converter = InstantConverter.class)
    private Instant originalGameTime;

    @Column(name = "host", nullable = false)
    private String host;

    @Column(name = "guest", nullable = false)
    private String guest;

    @Column(name = "score", nullable = false)
    private String score;

    @Column(name = "match_link", nullable = false)
    private String matchLink;

    @Column(name = "location", nullable = false)
    private String location;

    @Column(name = "location_link", nullable = false)
    private String locationLink;



}


