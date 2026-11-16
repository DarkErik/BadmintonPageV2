package de.dondevelops.badminton_backend.dtos;

import lombok.*;

import java.io.Serializable;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@EqualsAndHashCode
public class StandingPK implements Serializable {
    private Integer teamId;
    private Integer ordinal;
}
