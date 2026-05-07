package com.petclinic.vet.controller;

import com.petclinic.vet.exception.GlobalExceptionHandler;
import com.petclinic.vet.exception.ResourceNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.ServletWebRequest;

import static org.assertj.core.api.Assertions.assertThat;

class ExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void handleResourceNotFound_returnsProblemDetail() {
        ResourceNotFoundException ex = new ResourceNotFoundException("Vet", 999);
        MockHttpServletRequest httpRequest = new MockHttpServletRequest();
        httpRequest.setRequestURI("/api/vets/999");
        ServletWebRequest webRequest = new ServletWebRequest(httpRequest);

        ResponseEntity<ProblemDetail> response = handler.handleResourceNotFound(ex, webRequest);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        ProblemDetail body = response.getBody();
        assertThat(body).isNotNull();
        assertThat(body.getTitle()).isEqualTo("Not Found");
        assertThat(body.getDetail()).contains("Vet not found with id: 999");
        assertThat(body.getProperties()).containsKey("timestamp");
        assertThat(body.getProperties()).containsKey("schemaValidationErrors");
    }

    @Test
    void handleGenericException_returnsProblemDetail() {
        Exception ex = new RuntimeException("Something went wrong");
        MockHttpServletRequest httpRequest = new MockHttpServletRequest();
        httpRequest.setRequestURI("/api/vets");
        ServletWebRequest webRequest = new ServletWebRequest(httpRequest);

        ResponseEntity<ProblemDetail> response = handler.handleGenericException(ex, webRequest);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        ProblemDetail body = response.getBody();
        assertThat(body).isNotNull();
        assertThat(body.getTitle()).isEqualTo("Internal Server Error");
        assertThat(body.getDetail()).isEqualTo("Something went wrong");
    }

    @Test
    void resourceNotFoundException_message() {
        ResourceNotFoundException ex = new ResourceNotFoundException("Specialty", 42);
        assertThat(ex.getMessage()).isEqualTo("Specialty not found with id: 42");
    }
}
