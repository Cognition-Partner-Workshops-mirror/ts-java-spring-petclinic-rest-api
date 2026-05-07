package com.petclinic.vet.exception;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;

import static org.assertj.core.api.Assertions.assertThat;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();
    private final WebRequest request = new ServletWebRequest(new MockHttpServletRequest());

    @Test
    void handleResourceNotFound() {
        ResourceNotFoundException ex = new ResourceNotFoundException("Vet", 1);
        ResponseEntity<ProblemDetail> response = handler.handleResourceNotFound(ex, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getTitle()).isEqualTo("Vet Not Found");
        assertThat(response.getBody().getDetail()).contains("Vet");
        assertThat(response.getBody().getProperties()).containsKey("timestamp");
    }

    @Test
    void handleDuplicateResource() {
        DuplicateResourceException ex = new DuplicateResourceException("Specialty", "name", "radiology");
        ResponseEntity<ProblemDetail> response = handler.handleDuplicateResource(ex, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getTitle()).isEqualTo("Duplicate Specialty");
        assertThat(response.getBody().getProperties()).containsKey("timestamp");
    }

    @Test
    void handleGenericException() {
        Exception ex = new RuntimeException("something broke");
        ResponseEntity<ProblemDetail> response = handler.handleGenericException(ex, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getTitle()).isEqualTo("Internal Server Error");
        assertThat(response.getBody().getDetail()).isEqualTo("something broke");
        assertThat(response.getBody().getProperties()).containsKey("timestamp");
    }
}
