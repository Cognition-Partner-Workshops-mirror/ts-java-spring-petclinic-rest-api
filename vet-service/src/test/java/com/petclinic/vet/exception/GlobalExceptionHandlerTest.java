package com.petclinic.vet.exception;

import java.net.URI;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.ServletWebRequest;

import static org.assertj.core.api.Assertions.assertThat;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void handleNotFound_returnsProblemDetail() {
        var request = new ServletWebRequest(new MockHttpServletRequest("GET", "/api/vets/999"));
        var ex = new ResourceNotFoundException("Vet", 999);

        ResponseEntity<ProblemDetail> response = handler.handleNotFound(ex, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getTitle()).isEqualTo("Not Found");
        assertThat(response.getBody().getDetail()).contains("Vet");
    }

    @Test
    void handleGeneral_returnsInternalServerError() {
        var request = new ServletWebRequest(new MockHttpServletRequest("GET", "/api/vets"));
        var ex = new RuntimeException("unexpected error");

        ResponseEntity<ProblemDetail> response = handler.handleGeneral(ex, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getTitle()).isEqualTo("Internal Server Error");
    }

    @Test
    void resourceNotFoundException_message() {
        var ex = new ResourceNotFoundException("Specialty", 42);
        assertThat(ex.getMessage()).isEqualTo("Specialty not found with id: 42");
    }
}
