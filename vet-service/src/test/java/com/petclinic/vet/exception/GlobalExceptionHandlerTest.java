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
    void handleResourceNotFound() {
        ResourceNotFoundException ex = new ResourceNotFoundException("Vet", 42);
        MockHttpServletRequest servletRequest = new MockHttpServletRequest();
        servletRequest.setRequestURI("/vets/42");
        ServletWebRequest request = new ServletWebRequest(servletRequest);

        ResponseEntity<ProblemDetail> response = handler.handleResourceNotFound(ex, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getTitle()).isEqualTo("Vet Not Found");
        assertThat(response.getBody().getDetail()).contains("Vet not found with id 42");
    }

    @Test
    void handleAllUncaught() {
        Exception ex = new RuntimeException("unexpected error");
        MockHttpServletRequest servletRequest = new MockHttpServletRequest();
        servletRequest.setRequestURI("/vets");
        ServletWebRequest request = new ServletWebRequest(servletRequest);

        ResponseEntity<ProblemDetail> response = handler.handleAllUncaught(ex, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getTitle()).isEqualTo("Internal Server Error");
    }

    @Test
    void resourceNotFoundException_hasProperties() {
        ResourceNotFoundException ex = new ResourceNotFoundException("Specialty", 5);

        assertThat(ex.getResourceName()).isEqualTo("Specialty");
        assertThat(ex.getResourceId()).isEqualTo(5);
        assertThat(ex.getMessage()).contains("Specialty not found with id 5");
    }
}
