package com.petclinic.vet.exception;

import org.junit.jupiter.api.Test;
import org.springframework.http.ProblemDetail;
import org.springframework.web.context.request.WebRequest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

/**
 * Unit tests for GlobalExceptionHandler to cover all exception handling paths.
 */
class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();
    private final WebRequest webRequest = mock(WebRequest.class);

    @Test
    void handleResourceNotFound_returns404ProblemDetail() {
        ResourceNotFoundException ex = new ResourceNotFoundException("Vet", 42);

        ProblemDetail result = handler.handleResourceNotFound(ex, webRequest);

        assertThat(result.getStatus()).isEqualTo(404);
        assertThat(result.getTitle()).isEqualTo("Resource Not Found");
        assertThat(result.getDetail()).contains("Vet not found with id: 42");
        assertThat(result.getProperties()).containsKey("timestamp");
        assertThat(result.getProperties()).containsKey("schemaValidationErrors");
    }

    @Test
    void handleIllegalArgument_returns400ProblemDetail() {
        IllegalArgumentException ex = new IllegalArgumentException("Invalid input");

        ProblemDetail result = handler.handleIllegalArgument(ex, webRequest);

        assertThat(result.getStatus()).isEqualTo(400);
        assertThat(result.getTitle()).isEqualTo("Bad Request");
        assertThat(result.getDetail()).isEqualTo("Invalid input");
        assertThat(result.getProperties()).containsKey("timestamp");
    }

    @Test
    void handleGeneralException_returns500ProblemDetail() {
        Exception ex = new RuntimeException("Something unexpected");

        ProblemDetail result = handler.handleGeneralException(ex, webRequest);

        assertThat(result.getStatus()).isEqualTo(500);
        assertThat(result.getTitle()).isEqualTo("Internal Server Error");
        assertThat(result.getDetail()).isEqualTo("An unexpected error occurred");
        assertThat(result.getProperties()).containsKey("timestamp");
    }
}
