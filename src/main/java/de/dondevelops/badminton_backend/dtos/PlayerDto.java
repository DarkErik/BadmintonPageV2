package de.dondevelops.badminton_backend.dtos;

import de.dondevelops.badminton_backend.util.InstantConverter;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "players")
@IdClass(PlayerPK.class)
public class PlayerDto {

    @Id
    @Column(name = "game_id", nullable = false)
    private Integer gameId;

    @Id
    @Column(name = "name", nullable = false, columnDefinition = "TEXT")
    private String name;

    @Id
    @Column(name = "man", nullable = false)
    private Boolean man;

    @Column(name = "replacement", nullable = false)
    private Boolean replacement;

    @Column(name = "created_at", nullable = false)
    @Convert(converter = InstantConverter.class)
    private Instant createdAt;
}