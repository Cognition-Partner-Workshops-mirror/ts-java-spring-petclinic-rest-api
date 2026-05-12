package com.petclinic.vet.exception;

import org.junit.jupiter.api.Test;
import org.springframework.http.ProblemDetail;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.context.request.WebRequest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

/**
 * Unit tests for GlobalExceptionHandler to verify RFC 7807 Problem Details formatting.
 */
class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void handleResourceNotFound_returns404ProblemDetail() {
        ResourceNotFoundException ex = new ResourceNotFoundException("Vet", 1);
        WebRequest request = mock(WebRequest.class);

        ProblemDetail result = handler.handleResourceNotFound(ex, request);

        assertThat(result.getStatus()).isEqualTo(404);
        assertThat(result.getTitle()).isEqualTo("Resource Not Found");
        assertThat(result.getDetail()).contains("Vet");
        assertThat(result.getProperties()).containsKey("timestamp");
        assertThat(result.getProperties()).containsKey("schemaValidationErrors");
    }

    @Test
    void handleDuplicateResource_returns400ProblemDetail() {
        DuplicateResourceException ex = new DuplicateResourceException("Already exists");
        WebRequest request = mock(WebRequest.class);

        ProblemDetail result = handler.handleDuplicateResource(ex, request);

        assertThat(result.getStatus()).isEqualTo(400);
        assertThat(result.getTitle()).isEqualTo("Duplicate Resource");
        assertThat(result.getDetail()).isEqualTo("Already exists");
    }

    @Test
    void handleValidationException_returns400WithFieldErrors() {
        // Create a MethodArgumentNotValidException with field errors
        BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(new Object(), "target");
        bindingResult.addError(new FieldError("target", "name", "must not be blank"));

        MethodArgumentNotValidException ex = new MethodArgumentNotValidException(
            null, bindingResult);

        ProblemDetail result = handler.handleValidationException(ex);

        assertThat(result.getStatus()).isEqualTo(400);
        assertThat(result.getTitle()).isEqualTo("Validation Error");
        assertThat(result.getProperties()).containsKey("schemaValidationErrors");
    }

    @Test
    void handleGenericException_returns500ProblemDetail() {
        Exception ex = new RuntimeException("Unexpected error");
        WebRequest request = mock(WebRequest.class);

        ProblemDetail result = handler.handleGenericException(ex, request);

        assertThat(result.getStatus()).isEqualTo(500);
        assertThat(result.getTitle()).isEqualTo("Internal Server Error");
        assertThat(result.getDetail()).isEqualTo("An unexpected error occurred");
    }
}
