package com.petclinic.vet.exception;

import org.junit.jupiter.api.Test;
import org.springframework.http.ProblemDetail;

import static org.assertj.core.api.Assertions.assertThat;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void handleNotFound_returnsProblemDetail() {
        ResourceNotFoundException ex = new ResourceNotFoundException("Vet", 42);

        ProblemDetail result = handler.handleNotFound(ex, null);

        assertThat(result.getStatus()).isEqualTo(404);
        assertThat(result.getTitle()).isEqualTo("Not Found");
        assertThat(result.getDetail()).contains("Vet");
        assertThat(result.getDetail()).contains("42");
        assertThat(result.getProperties()).containsKey("timestamp");
    }

    @Test
    void handleGeneral_returnsProblemDetail() {
        Exception ex = new RuntimeException("Something went wrong");

        ProblemDetail result = handler.handleGeneral(ex);

        assertThat(result.getStatus()).isEqualTo(500);
        assertThat(result.getTitle()).isEqualTo("Internal Server Error");
        assertThat(result.getDetail()).isEqualTo("Something went wrong");
        assertThat(result.getProperties()).containsKey("timestamp");
    }
}
