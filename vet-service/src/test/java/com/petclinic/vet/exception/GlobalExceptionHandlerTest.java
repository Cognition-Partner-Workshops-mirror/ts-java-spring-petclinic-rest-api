package com.petclinic.vet.exception;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import static org.assertj.core.api.Assertions.assertThat;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();
    private final MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/vets/1");

    @Test
    void handleResourceNotFound_returnsProblemDetail() {
        ResourceNotFoundException ex = new ResourceNotFoundException("Vet", 42);

        ProblemDetail problem = handler.handleResourceNotFound(ex, request);

        assertThat(problem.getStatus()).isEqualTo(HttpStatus.NOT_FOUND.value());
        assertThat(problem.getTitle()).isEqualTo("Vet Not Found");
        assertThat(problem.getDetail()).isEqualTo("Vet not found with id: 42");
        assertThat(problem.getProperties()).containsKey("timestamp");
        assertThat(problem.getProperties()).containsKey("schemaValidationErrors");
    }

    @Test
    void handleValidation_returnsProblemDetail() {
        BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(new Object(), "vetRequestDto");
        bindingResult.addError(new FieldError("vetRequestDto", "firstName", "First name is required"));
        MethodArgumentNotValidException ex = new MethodArgumentNotValidException(null, bindingResult);

        ProblemDetail problem = handler.handleValidation(ex, request);

        assertThat(problem.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(problem.getTitle()).isEqualTo("Validation Error");
        assertThat(problem.getDetail()).isEqualTo("Validation failed");
        assertThat(problem.getProperties()).containsKey("schemaValidationErrors");
    }

    @Test
    void handleGeneral_returnsProblemDetail() {
        RuntimeException ex = new RuntimeException("Something went wrong");

        ProblemDetail problem = handler.handleGeneral(ex, request);

        assertThat(problem.getStatus()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
        assertThat(problem.getTitle()).isEqualTo("Internal Server Error");
        assertThat(problem.getDetail()).isEqualTo("Something went wrong");
        assertThat(problem.getProperties()).containsKey("timestamp");
    }
}
