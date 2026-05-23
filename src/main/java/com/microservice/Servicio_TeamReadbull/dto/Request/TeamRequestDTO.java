package com.microservice.Servicio_TeamReadbull.dto.Request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TeamRequestDTO {

    @NotBlank(message = "El nombre del equipo es obligatorio")
    private String name;

    private String idTournament;

    private String colors;

    private String photo;
}
