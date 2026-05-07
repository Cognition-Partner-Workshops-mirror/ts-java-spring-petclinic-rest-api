package com.petclinic.vet.exception;

import org.junit.jupiter.api.Test;
import org.springframework.http.ProblemDetail;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.context.request.WebRequest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void handleResourceNotFound_returnsProblemDetail404() {
        ResourceNotFoundException ex = new ResourceNotFoundException("Vet", 42);
        WebRequest request = mock(WebRequest.class);
        when(request.getDescription(false)).thenReturn("uri=/api/vets/42");

        ProblemDetail result = handler.handleResourceNotFound(ex, request);

        assertThat(result.getStatus()).isEqualTo(404);
        assertThat(result.getTitle()).isEqualTo("Vet not found");
        assertThat(result.getDetail()).contains("42");
        assertThat(result.getProperties()).containsKey("timestamp");
        assertThat(result.getProperties()).containsKey("schemaValidationErrors");
    }

    @Test
    void handleValidation_returnsProblemDetail400() {
        BindingResult bindingResult = mock(BindingResult.class);
        FieldError fieldError = new FieldError("vet", "firstName", "must not be blank");
        when(bindingResult.getFieldErrors()).thenReturn(List.of(fieldError));

        MethodArgumentNotValidException ex = new MethodArgumentNotValidException(null, bindingResult);
        WebRequest request = mock(WebRequest.class);
        when(request.getDescription(false)).thenReturn("uri=/api/vets");

        ProblemDetail result = handler.handleValidation(ex, request);

        assertThat(result.getStatus()).isEqualTo(400);
        assertThat(result.getTitle()).isEqualTo("Bad Request");
        assertThat(result.getProperties()).containsKey("schemaValidationErrors");
    }

    @Test
    void handleGeneral_returnsProblemDetail500() {
        Exception ex = new RuntimeException("something broke");
        WebRequest request = mock(WebRequest.class);
        when(request.getDescription(false)).thenReturn("uri=/api/vets");

        ProblemDetail result = handler.handleGeneral(ex, request);

        assertThat(result.getStatus()).isEqualTo(500);
        assertThat(result.getTitle()).isEqualTo("RuntimeException");
        assertThat(result.getDetail()).isEqualTo("something broke");
    }

    @Test
    void resourceNotFoundException_exposesFields() {
        ResourceNotFoundException ex = new ResourceNotFoundException("Specialty", 5);
        assertThat(ex.getResourceName()).isEqualTo("Specialty");
        assertThat(ex.getResourceId()).isEqualTo(5);
        assertThat(ex.getMessage()).contains("Specialty").contains("5");
    }
}
