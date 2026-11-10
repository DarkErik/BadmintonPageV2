package de.dondevelops.badminton_backend.dtos;

import jakarta.persistence.Column;
import jakarta.persistence.Id;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
public class DetailScorePK {
    private Integer gameId;
    private Integer ordinal;
}
