package com.microservice.Servicio_TeamReadbull.dto.Request;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TeamRequestDTO {

    private Long id;

    private String name;

    private Long idTournament;

    private Long captainId;

    private List<Long> idPlayers;

    private int currentPlayers;

    private String photo;

    private String colors;

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getColors() { return colors; }
    public void setColors(String colors) { this.colors = colors; }

    public Long getCaptainId() { return captainId; }
    public void setCaptainId(Long captainId) { this.captainId = captainId; }

    public String getPhoto() { return photo; }
    public void setPhoto(String photo) { this.photo = photo; }

    public List<Long> getPlayers() { return idPlayers; }
    public void setPlayers(List<Long> players) { this.idPlayers = players; }

}
