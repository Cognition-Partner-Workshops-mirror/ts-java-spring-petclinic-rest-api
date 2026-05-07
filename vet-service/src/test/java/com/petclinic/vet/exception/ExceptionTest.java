package com.petclinic.vet.exception;

import org.junit.jupiter.api.Test;
import org.springframework.http.ProblemDetail;
import org.springframework.web.context.request.WebRequest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

/**
 * Unit tests for exception classes and the global exception handler.
 */
class ExceptionTest {

    @Test
    void resourceNotFoundException_getters() {
        ResourceNotFoundException ex = new ResourceNotFoundException("Vet", 42);
        assertThat(ex.getResourceName()).isEqualTo("Vet");
        assertThat(ex.getResourceId()).isEqualTo(42);
        assertThat(ex.getMessage()).isEqualTo("Vet not found with id: 42");
    }

    @Test
    void globalExceptionHandler_handleGeneral_returns500() {
        GlobalExceptionHandler handler = new GlobalExceptionHandler();
        WebRequest request = mock(WebRequest.class);

        ProblemDetail result = handler.handleGeneral(
            new RuntimeException("unexpected error"), request);

        assertThat(result.getStatus()).isEqualTo(500);
        assertThat(result.getTitle()).isEqualTo("Internal Server Error");
        assertThat(result.getDetail()).isEqualTo("unexpected error");
    }

    @Test
    void globalExceptionHandler_handleResourceNotFound_returns404() {
        GlobalExceptionHandler handler = new GlobalExceptionHandler();
        WebRequest request = mock(WebRequest.class);

        ProblemDetail result = handler.handleResourceNotFound(
            new ResourceNotFoundException("Specialty", 99), request);

        assertThat(result.getStatus()).isEqualTo(404);
        assertThat(result.getTitle()).isEqualTo("Resource Not Found");
    }
}
