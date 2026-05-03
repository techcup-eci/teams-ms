package edu.eci.dosw.teamsms.dto;

import lombok.Data;
import java.util.List;

@Data
public class TeamRequestDTO {
    private String name;
    private String colors;
    private Long captainId;
    private List<PlayerRequestDTO> players;
}