package com.petclinic.vet.exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for GlobalExceptionHandler.
 * Verifies RFC 7807 Problem Detail responses for each exception type.
 */
class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    @DisplayName("handleResourceNotFound returns 404 ProblemDetail")
    void handleResourceNotFound() {
        ResourceNotFoundException ex = new ResourceNotFoundException("Vet", 1);

        ProblemDetail result = handler.handleResourceNotFound(ex);

        assertThat(result.getStatus()).isEqualTo(HttpStatus.NOT_FOUND.value());
        assertThat(result.getTitle()).isEqualTo("Resource Not Found");
        assertThat(result.getDetail()).contains("Vet not found with id: 1");
        assertThat(result.getProperties()).containsKey("timestamp");
    }

    @Test
    @DisplayName("handleDuplicateResource returns 409 ProblemDetail")
    void handleDuplicateResource() {
        DuplicateResourceException ex = new DuplicateResourceException("Duplicate name");

        ProblemDetail result = handler.handleDuplicateResource(ex);

        assertThat(result.getStatus()).isEqualTo(HttpStatus.CONFLICT.value());
        assertThat(result.getTitle()).isEqualTo("Duplicate Resource");
        assertThat(result.getDetail()).isEqualTo("Duplicate name");
    }

    @Test
    @DisplayName("handleValidationErrors returns 400 ProblemDetail with field errors")
    void handleValidationErrors() {
        BeanPropertyBindingResult bindingResult =
            new BeanPropertyBindingResult(new Object(), "testObject");
        bindingResult.addError(new FieldError("testObject", "name", "must not be blank"));

        MethodArgumentNotValidException ex =
            new MethodArgumentNotValidException(null, bindingResult);

        ProblemDetail result = handler.handleValidationErrors(ex);

        assertThat(result.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(result.getTitle()).isEqualTo("Bad Request");
        assertThat(result.getProperties()).containsKey("schemaValidationErrors");
    }

    @Test
    @DisplayName("handleGenericException returns 500 ProblemDetail")
    void handleGenericException() {
        Exception ex = new RuntimeException("Unexpected error");

        ProblemDetail result = handler.handleGenericException(ex);

        assertThat(result.getStatus()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
        assertThat(result.getTitle()).isEqualTo("Internal Server Error");
        assertThat(result.getDetail()).isEqualTo("An unexpected error occurred");
    }
}
