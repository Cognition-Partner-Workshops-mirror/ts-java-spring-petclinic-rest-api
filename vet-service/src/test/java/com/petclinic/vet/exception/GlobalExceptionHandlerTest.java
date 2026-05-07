package com.petclinic.vet.exception;

import org.junit.jupiter.api.Test;
import org.springframework.http.ProblemDetail;
import org.springframework.web.servlet.resource.NoResourceFoundException;
import org.springframework.http.HttpMethod;

import static org.assertj.core.api.Assertions.assertThat;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void handleResourceNotFound() {
        ResourceNotFoundException ex = new ResourceNotFoundException("Vet", 1);
        ProblemDetail detail = handler.handleResourceNotFound(ex, null);

        assertThat(detail.getStatus()).isEqualTo(404);
        assertThat(detail.getTitle()).isEqualTo("Vet not found");
        assertThat(detail.getDetail()).contains("Vet");
        assertThat(detail.getProperties()).containsKey("timestamp");
    }

    @Test
    void handleGenericException() {
        Exception ex = new RuntimeException("something went wrong");
        ProblemDetail detail = handler.handleGenericException(ex);

        assertThat(detail.getStatus()).isEqualTo(500);
        assertThat(detail.getTitle()).isEqualTo("Internal Server Error");
        assertThat(detail.getDetail()).isEqualTo("something went wrong");
    }

    @Test
    void handleNoResourceFound() throws Exception {
        NoResourceFoundException ex = new NoResourceFoundException(HttpMethod.GET, "/api/unknown");
        ProblemDetail detail = handler.handleNoResourceFound(ex);

        assertThat(detail.getStatus()).isEqualTo(404);
        assertThat(detail.getTitle()).isEqualTo("NoResourceFoundException");
    }

    @Test
    void resourceNotFoundException_getters() {
        ResourceNotFoundException ex = new ResourceNotFoundException("Specialty", 42);
        assertThat(ex.getResourceName()).isEqualTo("Specialty");
        assertThat(ex.getResourceId()).isEqualTo(42);
        assertThat(ex.getMessage()).contains("42");
    }
}
