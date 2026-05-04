package com.microservice.Servicio_TeamReadbull.dto.Request;

import lombok.Data;

@Data
public class PlayerRequestDTO {
    private Long userId;
    private Integer dorsal;
    private String academicProgram;
}