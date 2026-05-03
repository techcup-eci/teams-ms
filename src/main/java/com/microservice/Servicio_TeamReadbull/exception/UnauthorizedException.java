package com.microservice.Servicio_TeamReadbull.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@ResponseStatus(HttpStatus.FORBIDDEN)
public class UnauthorizedException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public UnauthorizedException(String message) {
        super(message);
    }

    public static UnauthorizedException notCaptain(Long teamId) {
        log.error("Acceso denegado: el usuario no es capitán del equipo con ID {}", teamId);
        return new UnauthorizedException(
                String.format("Solo el capitán puede gestionar solicitudes del equipo con ID '%s'", teamId));
    }
}
