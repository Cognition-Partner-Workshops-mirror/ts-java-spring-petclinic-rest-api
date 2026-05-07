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
    void handleNotFound_returnsProblemDetail() {
        ResourceNotFoundException ex = new ResourceNotFoundException("Vet", 42);
        MockHttpServletRequest servletRequest = new MockHttpServletRequest("GET", "/vets/42");
        ServletWebRequest request = new ServletWebRequest(servletRequest);

        ResponseEntity<ProblemDetail> response = handler.handleNotFound(ex, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        ProblemDetail body = response.getBody();
        assertThat(body).isNotNull();
        assertThat(body.getTitle()).isEqualTo("Vet Not Found");
        assertThat(body.getDetail()).contains("42");
        assertThat(body.getProperties()).containsKey("timestamp");
        assertThat(body.getProperties()).containsKey("schemaValidationErrors");
    }

    @Test
    void handleAll_returnsInternalServerError() {
        Exception ex = new RuntimeException("Unexpected error");
        MockHttpServletRequest servletRequest = new MockHttpServletRequest("GET", "/vets");
        ServletWebRequest request = new ServletWebRequest(servletRequest);

        ResponseEntity<ProblemDetail> response = handler.handleAll(ex, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        ProblemDetail body = response.getBody();
        assertThat(body).isNotNull();
        assertThat(body.getTitle()).isEqualTo("Internal Server Error");
        assertThat(body.getDetail()).isEqualTo("Unexpected error");
    }

    @Test
    void resourceNotFoundException_properties() {
        ResourceNotFoundException ex = new ResourceNotFoundException("Specialty", 7);
        assertThat(ex.getResourceName()).isEqualTo("Specialty");
        assertThat(ex.getResourceId()).isEqualTo(7);
        assertThat(ex.getMessage()).contains("Specialty").contains("7");
    }
}
