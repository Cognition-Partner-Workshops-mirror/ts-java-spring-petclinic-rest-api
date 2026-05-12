package com.petclinic.vet.exception;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for {@link GlobalExceptionHandler} verifying RFC 7807 Problem Detail responses.
 */
class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();
    private final WebRequest webRequest = new ServletWebRequest(new MockHttpServletRequest());

    @Test
    void shouldHandleResourceNotFoundException() {
        ResourceNotFoundException ex = new ResourceNotFoundException("Vet", 1);

        ResponseEntity<ProblemDetail> response = handler.handleResourceNotFound(ex, webRequest);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getTitle()).isEqualTo("Resource Not Found");
        assertThat(response.getBody().getDetail()).contains("Vet not found with id: 1");
    }

    @Test
    void shouldHandleDuplicateResourceException() {
        DuplicateResourceException ex = new DuplicateResourceException("Specialty", "name", "radiology");

        ResponseEntity<ProblemDetail> response = handler.handleDuplicateResource(ex, webRequest);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getTitle()).isEqualTo("Duplicate Resource");
        assertThat(response.getBody().getDetail()).contains("Specialty already exists with name: radiology");
    }

    @Test
    void shouldHandleGenericException() {
        Exception ex = new RuntimeException("something went wrong");

        ResponseEntity<ProblemDetail> response = handler.handleAllUncaughtExceptions(ex, webRequest);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getTitle()).isEqualTo("Internal Server Error");
    }

    @Test
    void resourceNotFoundShouldIncludeTimestamp() {
        ResourceNotFoundException ex = new ResourceNotFoundException("Vet", 1);

        ResponseEntity<ProblemDetail> response = handler.handleResourceNotFound(ex, webRequest);

        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getProperties()).containsKey("timestamp");
    }

    @Test
    void duplicateResourceShouldIncludeTimestamp() {
        DuplicateResourceException ex = new DuplicateResourceException("Specialty", "name", "test");

        ResponseEntity<ProblemDetail> response = handler.handleDuplicateResource(ex, webRequest);

        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getProperties()).containsKey("timestamp");
    }

    @Test
    void resourceNotFoundExceptionShouldExposeFields() {
        ResourceNotFoundException ex = new ResourceNotFoundException("Vet", 42);

        assertThat(ex.getResourceName()).isEqualTo("Vet");
        assertThat(ex.getResourceId()).isEqualTo(42);
    }

    @Test
    void duplicateResourceExceptionShouldExposeFields() {
        DuplicateResourceException ex = new DuplicateResourceException("Specialty", "name", "surgery");

        assertThat(ex.getResourceName()).isEqualTo("Specialty");
        assertThat(ex.getFieldName()).isEqualTo("name");
        assertThat(ex.getFieldValue()).isEqualTo("surgery");
    }
}
