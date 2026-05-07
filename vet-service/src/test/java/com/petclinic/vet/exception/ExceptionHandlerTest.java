package com.petclinic.vet.exception;

import com.petclinic.vet.dto.ProblemDetailDto;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Path;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ExceptionHandlerTest {

    private GlobalExceptionHandler handler;
    private HttpServletRequest request;

    @BeforeEach
    void setUp() {
        handler = new GlobalExceptionHandler();
        request = mock(HttpServletRequest.class);
        when(request.getRequestURL()).thenReturn(new StringBuffer("http://localhost:8081/api/vets"));
    }

    @Test
    void handleResourceNotFound_returns404() {
        ResourceNotFoundException ex = new ResourceNotFoundException("Vet", 99);

        ResponseEntity<ProblemDetailDto> response = handler.handleResourceNotFound(ex, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getStatus()).isEqualTo(404);
        assertThat(response.getBody().getTitle()).isEqualTo("Not Found");
        assertThat(response.getBody().getDetail()).contains("Vet");
        assertThat(response.getBody().getDetail()).contains("99");
        assertThat(response.getBody().getTimestamp()).isNotNull();
        assertThat(response.getBody().getType()).isEqualTo("http://localhost:8081/api/vets");
    }

    @Test
    void handleValidation_returns400WithErrors() {
        BindingResult bindingResult = mock(BindingResult.class);
        FieldError fieldError = new FieldError("vetRequestDto", "firstName", "must not be blank");
        when(bindingResult.getFieldErrors()).thenReturn(Arrays.asList(fieldError));

        MethodArgumentNotValidException ex = new MethodArgumentNotValidException(null, bindingResult);

        ResponseEntity<ProblemDetailDto> response = handler.handleValidation(ex, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getStatus()).isEqualTo(400);
        assertThat(response.getBody().getSchemaValidationErrors()).hasSize(1);
        assertThat(response.getBody().getSchemaValidationErrors().get(0).getMessage())
            .contains("firstName");
    }

    @Test
    @SuppressWarnings("unchecked")
    void handleConstraintViolation_returns400() {
        Set<ConstraintViolation<?>> violations = new HashSet<>();
        ConstraintViolation<?> violation = mock(ConstraintViolation.class);
        Path path = mock(Path.class);
        when(path.toString()).thenReturn("name");
        when(violation.getPropertyPath()).thenReturn(path);
        when(violation.getMessage()).thenReturn("must not be blank");
        violations.add(violation);

        ConstraintViolationException ex = new ConstraintViolationException(violations);

        ResponseEntity<ProblemDetailDto> response = handler.handleConstraintViolation(ex, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getStatus()).isEqualTo(400);
        assertThat(response.getBody().getSchemaValidationErrors()).hasSize(1);
    }

    @Test
    void handleGenericException_returns500() {
        Exception ex = new RuntimeException("Unexpected error");

        ResponseEntity<ProblemDetailDto> response = handler.handleGenericException(ex, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getStatus()).isEqualTo(500);
        assertThat(response.getBody().getTitle()).isEqualTo("Internal Server Error");
        assertThat(response.getBody().getDetail()).isEqualTo("Unexpected error");
    }

    @Test
    void resourceNotFoundException_getters() {
        ResourceNotFoundException ex = new ResourceNotFoundException("Specialty", 5);
        assertThat(ex.getEntityName()).isEqualTo("Specialty");
        assertThat(ex.getEntityId()).isEqualTo(5);
        assertThat(ex.getMessage()).isEqualTo("Specialty not found with id 5");
    }
}
