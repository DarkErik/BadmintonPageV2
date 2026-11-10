package de.dondevelops.badminton_backend.dtos;

import lombok.*;

import java.io.Serializable;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
public class PlayerPK implements Serializable {
    private Integer gameId;
    private String name;
    private Boolean man;
}
