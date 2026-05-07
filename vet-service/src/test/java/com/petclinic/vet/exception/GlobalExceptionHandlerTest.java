package com.petclinic.vet.exception;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void handleResourceNotFound_shouldReturn404() {
        ResourceNotFoundException ex = new ResourceNotFoundException("Vet", 99);
        ResponseEntity<ProblemDetail> response = handler.handleResourceNotFound(ex, null);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getTitle()).isEqualTo("Not Found");
        assertThat(response.getBody().getProperties()).containsKey("timestamp");
        assertThat(response.getBody().getProperties()).containsKey("schemaValidationErrors");
    }

    @Test
    void handleGenericException_shouldReturn500() {
        Exception ex = new RuntimeException("Something went wrong");
        ResponseEntity<ProblemDetail> response = handler.handleGenericException(ex, null);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getTitle()).isEqualTo("Internal Server Error");
        assertThat(response.getBody().getDetail()).isEqualTo("Something went wrong");
        assertThat(response.getBody().getProperties()).containsKey("timestamp");
        assertThat(response.getBody().getProperties()).containsKey("schemaValidationErrors");
    }
}
