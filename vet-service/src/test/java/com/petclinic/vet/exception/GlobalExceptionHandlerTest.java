package com.petclinic.vet.exception;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Unit tests for GlobalExceptionHandler.
 * Verifies RFC 7807 Problem Details responses for validation, not-found, and generic errors.
 */
class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void handleValidationException_returns400WithFieldErrors() {
        // Mock a validation exception with field errors
        MethodArgumentNotValidException ex = mock(MethodArgumentNotValidException.class);
        BindingResult bindingResult = mock(BindingResult.class);
        FieldError fieldError = new FieldError("vetRequestDto", "firstName", "must not be empty");

        when(ex.getBindingResult()).thenReturn(bindingResult);
        when(bindingResult.getFieldErrors()).thenReturn(List.of(fieldError));

        ProblemDetail result = handler.handleValidationException(ex);

        assertThat(result.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(result.getTitle()).isEqualTo("Validation Failed");
        assertThat(result.getDetail()).isEqualTo("One or more fields have validation errors.");
        assertThat(result.getProperties()).containsKey("timestamp");
        assertThat(result.getProperties()).containsKey("schemaValidationErrors");

        // Verify field errors are properly formatted
        @SuppressWarnings("unchecked")
        List<Map<String, String>> errors =
            (List<Map<String, String>>) result.getProperties().get("schemaValidationErrors");
        assertThat(errors).hasSize(1);
        assertThat(errors.get(0).get("message")).contains("firstName");
    }

    @Test
    void handleResourceNotFound_returns404() {
        ResourceNotFoundException ex = new ResourceNotFoundException("Vet not found with id: 42");

        ProblemDetail result = handler.handleResourceNotFound(ex);

        assertThat(result.getStatus()).isEqualTo(HttpStatus.NOT_FOUND.value());
        assertThat(result.getTitle()).isEqualTo("Not Found");
        assertThat(result.getDetail()).isEqualTo("Vet not found with id: 42");
        assertThat(result.getProperties()).containsKey("timestamp");
    }

    @Test
    void handleGenericException_returns500() {
        Exception ex = new RuntimeException("Unexpected failure");

        ProblemDetail result = handler.handleGenericException(ex);

        assertThat(result.getStatus()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
        assertThat(result.getTitle()).isEqualTo("Internal Server Error");
        assertThat(result.getDetail()).isEqualTo("Unexpected failure");
        assertThat(result.getProperties()).containsKey("timestamp");
    }
}
