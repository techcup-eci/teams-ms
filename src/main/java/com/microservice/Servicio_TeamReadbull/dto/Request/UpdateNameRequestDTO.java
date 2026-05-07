package com.microservice.Servicio_TeamReadbull.dto.Request;

import jakarta.validation.constraints.NotBlank;

public class UpdateNameRequestDTO {

    @NotBlank(message = "El nombre no puede estar vacío")
    private String name;

    public UpdateNameRequestDTO() {}

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
}