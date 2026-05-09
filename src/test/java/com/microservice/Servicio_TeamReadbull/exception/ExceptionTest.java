package com.microservice.Servicio_TeamReadbull.exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class ExceptionTest {

    @Test
    @DisplayName("ResourceNotFoundException.notFound genera mensaje correcto con Long")
    void resourceNotFound_withLongId() {
        ResourceNotFoundException ex = ResourceNotFoundException.notFound("Team", 1L);
        assertThat(ex.getMessage()).contains("Team").contains("1");
    }

    @Test
    @DisplayName("ResourceNotFoundException.notFound genera mensaje correcto con String")
    void resourceNotFound_withStringId() {
        ResourceNotFoundException ex = ResourceNotFoundException.notFound("Team", "code: ABC123");
        assertThat(ex.getMessage()).contains("Team").contains("ABC123");
    }

    @Test
    @DisplayName("ResourceNotFoundException se puede instanciar con mensaje directo")
    void resourceNotFound_directConstructor() {
        ResourceNotFoundException ex = new ResourceNotFoundException("Recurso no encontrado");
        assertEquals("Recurso no encontrado", ex.getMessage());
    }

    @Test
    @DisplayName("UnauthorizedException.notCaptain genera mensaje correcto")
    void unauthorizedException_notCaptain() {
        UnauthorizedException ex = UnauthorizedException.notCaptain(42L);
        assertThat(ex.getMessage()).contains("42").contains("capitán");
    }

    @Test
    @DisplayName("UnauthorizedException se puede instanciar con mensaje directo")
    void unauthorizedException_directConstructor() {
        UnauthorizedException ex = new UnauthorizedException("Acceso denegado");
        assertEquals("Acceso denegado", ex.getMessage());
    }
}
