package com.microservice.Servicio_TeamReadbull.dto.Response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SolicitudResponseDTO {

    private Long id;
    private Long idJugador;
    private Long idEquipo;
    private String estado;
}
