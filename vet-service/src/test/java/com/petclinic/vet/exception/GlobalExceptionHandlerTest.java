package com.petclinic.vet.exception;

import org.junit.jupiter.api.Test;
import org.springframework.http.ProblemDetail;

import static org.assertj.core.api.Assertions.assertThat;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void handleNotFound_returnsProblemDetailWith404() {
        ResourceNotFoundException ex = new ResourceNotFoundException("Vet", 1);
        ProblemDetail result = handler.handleNotFound(ex, null);

        assertThat(result.getStatus()).isEqualTo(404);
        assertThat(result.getTitle()).isEqualTo("Not Found");
        assertThat(result.getDetail()).contains("Vet not found with id: 1");
    }

    @Test
    void handleGeneral_returnsProblemDetailWith500() {
        Exception ex = new RuntimeException("unexpected");
        ProblemDetail result = handler.handleGeneral(ex);

        assertThat(result.getStatus()).isEqualTo(500);
        assertThat(result.getTitle()).isEqualTo("Internal Server Error");
        assertThat(result.getDetail()).isEqualTo("unexpected");
    }
}
