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
        ResourceNotFoundException ex = new ResourceNotFoundException("Vet", 42);
        ServletWebRequest request = new ServletWebRequest(new MockHttpServletRequest("GET", "/api/vets/42"));

        ResponseEntity<ProblemDetail> response = handler.handleNotFound(ex, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getTitle()).isEqualTo("Not Found");
        assertThat(response.getBody().getDetail()).contains("Vet not found with id 42");
        assertThat(response.getBody().getType()).isEqualTo(URI.create("/api/vets/42"));
    }

    @Test
    void handleGeneral_returnsInternalServerError() {
        RuntimeException ex = new RuntimeException("something broke");
        ServletWebRequest request = new ServletWebRequest(new MockHttpServletRequest("GET", "/api/vets"));

        ResponseEntity<ProblemDetail> response = handler.handleGeneral(ex, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getTitle()).isEqualTo("Internal Server Error");
    }
}
