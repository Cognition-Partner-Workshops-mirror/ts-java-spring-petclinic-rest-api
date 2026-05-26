package com.petclinic.vet.exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for {@link GlobalExceptionHandler} verifying RFC 7807 responses.
 */
class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();
    private final WebRequest webRequest = new ServletWebRequest(new MockHttpServletRequest());

    @Test
    @DisplayName("handleResourceNotFound returns 404 with ProblemDetail body")
    void handleResourceNotFound_returns404() {
        ResourceNotFoundException ex = new ResourceNotFoundException("Vet not found with id: 42");
        ResponseEntity<ProblemDetail> response = handler.handleResourceNotFound(ex, webRequest);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getTitle()).isEqualTo("Resource Not Found");
        assertThat(response.getBody().getDetail()).contains("42");
        assertThat(response.getBody().getStatus()).isEqualTo(404);
        // Verify timestamp property is present
        assertThat(response.getBody().getProperties()).containsKey("timestamp");
        assertThat(response.getBody().getProperties()).containsKey("schemaValidationErrors");
    }

    @Test
    @DisplayName("handleAllExceptions returns 500 with ProblemDetail body")
    void handleAllExceptions_returns500() {
        Exception ex = new RuntimeException("Unexpected error occurred");
        ResponseEntity<ProblemDetail> response = handler.handleAllExceptions(ex, webRequest);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getTitle()).isEqualTo("Internal Server Error");
        assertThat(response.getBody().getDetail()).contains("Unexpected error");
        assertThat(response.getBody().getStatus()).isEqualTo(500);
    }
}
