package com.petclinic.vet.exception;

import org.junit.jupiter.api.Test;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for {@link GlobalExceptionHandler} validating RFC 7807 Problem Details responses.
 */
class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    private MockHttpServletRequest createMockRequest() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/api/test");
        request.setServerName("localhost");
        request.setServerPort(8080);
        request.setScheme("http");
        return request;
    }

    @Test
    void handleResourceNotFound_shouldReturn404() {
        ResourceNotFoundException ex = new ResourceNotFoundException("Vet", 1);
        MockHttpServletRequest request = createMockRequest();

        ResponseEntity<ProblemDetail> response = handler.handleResourceNotFound(ex, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getTitle()).isEqualTo("ResourceNotFoundException");
        assertThat(response.getBody().getDetail()).contains("Vet not found with id: 1");
    }

    @Test
    void handleDuplicateResource_shouldReturn409() {
        DuplicateResourceException ex = new DuplicateResourceException("Specialty already exists");
        MockHttpServletRequest request = createMockRequest();

        ResponseEntity<ProblemDetail> response = handler.handleDuplicateResource(ex, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getTitle()).isEqualTo("DuplicateResourceException");
    }

    @Test
    void handleDataIntegrityViolation_shouldReturn409() {
        DataIntegrityViolationException ex = new DataIntegrityViolationException("constraint violation");
        MockHttpServletRequest request = createMockRequest();

        ResponseEntity<ProblemDetail> response = handler.handleDataIntegrityViolation(ex, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(response.getBody()).isNotNull();
    }

    @Test
    void handleGenericException_shouldReturn500() {
        RuntimeException ex = new RuntimeException("unexpected");
        MockHttpServletRequest request = createMockRequest();

        ResponseEntity<ProblemDetail> response = handler.handleGenericException(ex, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getDetail()).contains("unexpected error");
    }

    @Test
    void problemDetail_shouldContainTimestampAndValidationErrors() {
        ResourceNotFoundException ex = new ResourceNotFoundException("Specialty", 42);
        MockHttpServletRequest request = createMockRequest();

        ResponseEntity<ProblemDetail> response = handler.handleResourceNotFound(ex, request);

        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getProperties()).containsKey("timestamp");
        assertThat(response.getBody().getProperties()).containsKey("schemaValidationErrors");
    }

    @Test
    void resourceNotFoundException_shouldExposeResourceInfo() {
        ResourceNotFoundException ex = new ResourceNotFoundException("Vet", 5);

        assertThat(ex.getResourceName()).isEqualTo("Vet");
        assertThat(ex.getResourceId()).isEqualTo(5);
        assertThat(ex.getMessage()).isEqualTo("Vet not found with id: 5");
    }
}
