package com.microservice.Servicio_TeamReadbull.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@ResponseStatus(HttpStatus.NOT_FOUND)
public class ResourceNotFoundException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public ResourceNotFoundException(String message) {
        super(message);
    }

    public static ResourceNotFoundException notFound(String resourceType, Object resourceId) {
        log.error("Objeto {} con ID {} no encontrado", resourceType, resourceId);
        return new ResourceNotFoundException(
                String.format("%s with ID '%s' not found", resourceType, resourceId));
    }
}
