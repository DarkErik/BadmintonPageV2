package de.dondevelops.badminton_backend.dtos;

import de.dondevelops.badminton_backend.util.InstantConverter;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.time.LocalDateTime;

@Entity
@Table(name = "game_dates")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GameDateDto {

    @Id
    @Column(name = "game_id", nullable = false)
    private Integer gameId;

    @Column(name = "game_time", nullable = false)
    @Convert(converter = InstantConverter.class)
    private Instant gameTime;

    @Column(name = "approved", nullable = false)
    private Boolean approved;


    @Column(name = "created_at", nullable = false)
    @Convert(converter = InstantConverter.class)
    private Instant created_at;


}