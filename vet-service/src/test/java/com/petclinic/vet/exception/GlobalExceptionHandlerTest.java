package com.petclinic.vet.exception;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;

import static org.assertj.core.api.Assertions.assertThat;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();
    private final MockHttpServletRequest request = new MockHttpServletRequest("GET", "/petclinic/api/vets/1");

    @Test
    void handleNotFound_returns404() {
        ResourceNotFoundException ex = new ResourceNotFoundException("Vet", 1);
        ResponseEntity<ProblemDetail> response = handler.handleNotFound(ex, request);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody().getDetail()).isEqualTo("Vet not found with id 1");
    }

    @Test
    void handleDuplicate_returns409() {
        DuplicateResourceException ex = new DuplicateResourceException("Specialty already exists");
        ResponseEntity<ProblemDetail> response = handler.handleDuplicate(ex, request);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(response.getBody().getDetail()).isEqualTo("Specialty already exists");
    }

    @Test
    void handleGeneral_returns500() {
        Exception ex = new RuntimeException("boom");
        ResponseEntity<ProblemDetail> response = handler.handleGeneral(ex, request);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody().getDetail()).isEqualTo("An unexpected error occurred");
    }
}
