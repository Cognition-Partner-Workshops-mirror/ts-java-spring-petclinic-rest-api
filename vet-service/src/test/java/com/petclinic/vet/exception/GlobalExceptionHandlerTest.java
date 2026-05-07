package com.petclinic.vet.exception;

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
    void handleResourceNotFound_returnsProblemDetail() {
        ResourceNotFoundException ex = new ResourceNotFoundException("Vet", 42);
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/vets/42");
        request.setServerName("localhost");
        request.setServerPort(8081);
        request.setScheme("http");
        ServletWebRequest webRequest = new ServletWebRequest(request);

        ResponseEntity<ProblemDetail> response = handler.handleResourceNotFound(ex, webRequest);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        ProblemDetail body = response.getBody();
        assertThat(body).isNotNull();
        assertThat(body.getTitle()).isEqualTo("Vet not found");
        assertThat(body.getDetail()).contains("Vet");
        assertThat(body.getProperties()).containsKey("timestamp");
        assertThat(body.getProperties()).containsKey("schemaValidationErrors");
    }

    @Test
    void handleAllUncaught_returnsProblemDetail() {
        Exception ex = new RuntimeException("Something went wrong");
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/vets");
        request.setServerName("localhost");
        request.setServerPort(8081);
        request.setScheme("http");
        ServletWebRequest webRequest = new ServletWebRequest(request);

        ResponseEntity<ProblemDetail> response = handler.handleAllUncaught(ex, webRequest);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        ProblemDetail body = response.getBody();
        assertThat(body).isNotNull();
        assertThat(body.getTitle()).isEqualTo("Internal Server Error");
    }

    @Test
    void resourceNotFoundException_exposesFields() {
        ResourceNotFoundException ex = new ResourceNotFoundException("Specialty", 5);
        assertThat(ex.getResourceName()).isEqualTo("Specialty");
        assertThat(ex.getResourceId()).isEqualTo(5);
        assertThat(ex.getMessage()).contains("Specialty not found with id: 5");
    }
}
