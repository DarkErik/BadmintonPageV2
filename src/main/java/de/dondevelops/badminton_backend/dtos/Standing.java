package de.dondevelops.badminton_backend.dtos;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "standings")
@IdClass(StandingPK.class)
public class Standing {

    @Id
    @Column(name = "team_id", nullable = false)
    private Integer teamId;

    @Id
    @Column(name = "ordinal", nullable = false)
    private Integer ordinal;

    @Builder.Default
    @Column(name = "displayed_rank", nullable = false)
    private Integer displayedRank = -1;

    @Column(name = "club_name", nullable = false, columnDefinition = "TEXT")
    private String clubName;

    @Builder.Default
    @Column(name = "played_games", nullable = false)
    private Integer playedGames = 0;

    @Builder.Default
    @Column(name = "won_points", nullable = false)
    private Integer wonPoints = 0;

    @Builder.Default
    @Column(name = "lost_points", nullable = false)
    private Integer lostPoints = 0;
}