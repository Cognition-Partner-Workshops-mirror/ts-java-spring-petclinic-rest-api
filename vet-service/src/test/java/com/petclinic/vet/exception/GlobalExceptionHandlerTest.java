package com.petclinic.vet.exception;

import org.junit.jupiter.api.Test;
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
 * Unit tests for GlobalExceptionHandler verifying RFC 7807 Problem Details responses.
 */
class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void handleResourceNotFound_returns404ProblemDetail() {
        ResourceNotFoundException ex = new ResourceNotFoundException("Vet not found with id: 1");

        ProblemDetail result = handler.handleResourceNotFound(ex);

        assertThat(result.getStatus()).isEqualTo(404);
        assertThat(result.getTitle()).isEqualTo("Resource Not Found");
        assertThat(result.getDetail()).isEqualTo("Vet not found with id: 1");
        assertThat(result.getProperties()).containsKey("timestamp");
        assertThat(result.getProperties()).containsKey("schemaValidationErrors");
    }

    @Test
    @SuppressWarnings("unchecked")
    void handleValidationErrors_returns400WithFieldErrors() {
        BindingResult bindingResult = mock(BindingResult.class);
        FieldError fieldError = new FieldError("vetRequestDto", "firstName", "First name is required");
        when(bindingResult.getFieldErrors()).thenReturn(List.of(fieldError));

        MethodArgumentNotValidException ex = new MethodArgumentNotValidException(null, bindingResult);

        ProblemDetail result = handler.handleValidationErrors(ex);

        assertThat(result.getStatus()).isEqualTo(400);
        assertThat(result.getTitle()).isEqualTo("Bad Request");
        assertThat(result.getDetail()).isEqualTo("Validation failed");
        List<Map<String, String>> errors = (List<Map<String, String>>) result.getProperties().get("schemaValidationErrors");
        assertThat(errors).hasSize(1);
        assertThat(errors.get(0).get("message")).contains("firstName");
    }

    @Test
    void handleGenericException_returns500ProblemDetail() {
        RuntimeException ex = new RuntimeException("Unexpected error");

        ProblemDetail result = handler.handleGenericException(ex);

        assertThat(result.getStatus()).isEqualTo(500);
        assertThat(result.getTitle()).isEqualTo("Internal Server Error");
        assertThat(result.getDetail()).isEqualTo("Unexpected error");
    }
}
