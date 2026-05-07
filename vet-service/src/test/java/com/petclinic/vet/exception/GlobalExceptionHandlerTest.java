package com.petclinic.vet.exception;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;

import static org.assertj.core.api.Assertions.assertThat;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void handleNotFound_returns404WithProblemDetail() {
        ResourceNotFoundException ex = new ResourceNotFoundException("Vet", 99);
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/vets/99");
        request.setServerName("localhost");
        request.setServerPort(8081);

        ResponseEntity<ProblemDetail> response = handler.handleNotFound(ex, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getTitle()).isEqualTo("ResourceNotFoundException");
        assertThat(response.getBody().getDetail()).contains("Vet");
    }

    @Test
    void handleGeneral_returns500WithProblemDetail() {
        Exception ex = new RuntimeException("Something broke");
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/vets");
        request.setServerName("localhost");
        request.setServerPort(8081);

        ResponseEntity<ProblemDetail> response = handler.handleGeneral(ex, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getDetail()).contains("unexpected");
    }

    @Test
    void handleIllegalArgument_returns400WithProblemDetail() {
        IllegalArgumentException ex = new IllegalArgumentException("Unknown specialties: [cardiology]");
        MockHttpServletRequest request = new MockHttpServletRequest("POST", "/api/vets");
        request.setServerName("localhost");
        request.setServerPort(8081);

        ResponseEntity<ProblemDetail> response = handler.handleIllegalArgument(ex, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getTitle()).isEqualTo("IllegalArgumentException");
        assertThat(response.getBody().getDetail()).contains("Unknown specialties");
    }

    @Test
    void handleDataIntegrity_returns409WithProblemDetail() {
        org.springframework.dao.DataIntegrityViolationException ex =
            new org.springframework.dao.DataIntegrityViolationException("duplicate key");
        MockHttpServletRequest request = new MockHttpServletRequest("POST", "/api/specialties");
        request.setServerName("localhost");
        request.setServerPort(8081);

        ResponseEntity<ProblemDetail> response = handler.handleDataIntegrity(ex, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getDetail()).contains("constraint");
    }
}
