package de.dondevelops.badminton_backend.dtos;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "teams")
public class TeamDto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "team_url", nullable = false, length = 10)
    private String teamUrl;

    @Column(name = "team_name", nullable = false, length = 20)
    private String teamName = "Mannschaft";

    @Column(name = "mini", nullable = false)
    private Boolean mini = false;

    @Column(name = "dbv_link", nullable = false)
    private String dbvLink;
}