package de.dondevelops.badminton_backend.dtos;

import de.dondevelops.badminton_backend.util.StringListConverter;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity(name = "detail_scores")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@IdClass(DetailScorePK.class)
public class DetailScoreDto {

    @Id
    @Column(name = "game_id", nullable = false)
    private Integer gameId;

    @Id
    @Column(name = "ordinal", nullable = false)
    private Integer ordinal;


    @Column(name = "game_type", nullable = false)
    private String gameType;

    @Convert(converter = StringListConverter.class)
    @Column(name = "host_player", columnDefinition = "TEXT", nullable = false)
    private List<String> hostPlayer;

    @Convert(converter = StringListConverter.class)
    @Column(name = "guest_player", columnDefinition = "TEXT", nullable = false)
    private List<String> guestPlayer;

    @Convert(converter = StringListConverter.class)
    @Column(name = "sets", columnDefinition = "TEXT", nullable = false)
    private List<String> sets;
}