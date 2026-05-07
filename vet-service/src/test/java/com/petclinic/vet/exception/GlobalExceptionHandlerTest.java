package com.petclinic.vet.exception;

import org.junit.jupiter.api.Test;
import org.springframework.http.ProblemDetail;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.core.MethodParameter;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void handleResourceNotFound_returns404() {
        ResourceNotFoundException ex = new ResourceNotFoundException("Vet", 1);

        ProblemDetail result = handler.handleResourceNotFound(ex, null);

        assertThat(result.getStatus()).isEqualTo(404);
        assertThat(result.getTitle()).isEqualTo("Not Found");
        assertThat(result.getDetail()).contains("Vet");
        assertThat(result.getProperties()).containsKey("timestamp");
        assertThat(result.getProperties()).containsKey("schemaValidationErrors");
    }

    @Test
    void handleIllegalArgument_returns400() {
        IllegalArgumentException ex = new IllegalArgumentException("bad input");

        ProblemDetail result = handler.handleIllegalArgument(ex, null);

        assertThat(result.getStatus()).isEqualTo(400);
        assertThat(result.getTitle()).isEqualTo("Bad Request");
        assertThat(result.getDetail()).isEqualTo("bad input");
    }

    @Test
    void handleGenericException_returns500() {
        Exception ex = new RuntimeException("unexpected");

        ProblemDetail result = handler.handleGenericException(ex, null);

        assertThat(result.getStatus()).isEqualTo(500);
        assertThat(result.getTitle()).isEqualTo("Internal Server Error");
    }

    @Test
    void handleValidation_returns400WithErrors() throws Exception {
        BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(new Object(), "target");
        bindingResult.addError(new FieldError("target", "name", "must not be blank"));

        MethodParameter param = new MethodParameter(
            this.getClass().getDeclaredMethod("handleValidation_returns400WithErrors"), -1);
        MethodArgumentNotValidException ex = new MethodArgumentNotValidException(param, bindingResult);

        ProblemDetail result = handler.handleValidation(ex, null);

        assertThat(result.getStatus()).isEqualTo(400);
        assertThat(result.getTitle()).isEqualTo("Bad Request");
        Object errors = result.getProperties().get("schemaValidationErrors");
        assertThat(errors).isInstanceOf(List.class);
        assertThat((List<?>) errors).isNotEmpty();
    }

    @Test
    void resourceNotFoundException_hasProperties() {
        ResourceNotFoundException ex = new ResourceNotFoundException("Specialty", 42);

        assertThat(ex.getResourceName()).isEqualTo("Specialty");
        assertThat(ex.getResourceId()).isEqualTo(42);
        assertThat(ex.getMessage()).contains("Specialty").contains("42");
    }
}
