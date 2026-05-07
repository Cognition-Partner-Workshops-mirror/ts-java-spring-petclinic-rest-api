package com.petclinic.vet.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.net.URI;
import java.time.Instant;
import java.util.List;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ProblemDetail handleResourceNotFound(ResourceNotFoundException ex, HttpServletRequest request) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
        problem.setTitle(ex.getResourceName() + " Not Found");
        problem.setType(URI.create(request.getRequestURL().toString()));
        problem.setProperty("timestamp", Instant.now());
        problem.setProperty("schemaValidationErrors", List.of());
        return problem;
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail handleValidation(MethodArgumentNotValidException ex, HttpServletRequest request) {
        List<Map<String, String>> validationErrors = ex.getBindingResult().getFieldErrors().stream()
            .map(this::fieldErrorToMap)
            .toList();

        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, "Validation failed");
        problem.setTitle("Validation Error");
        problem.setType(URI.create(request.getRequestURL().toString()));
        problem.setProperty("timestamp", Instant.now());
        problem.setProperty("schemaValidationErrors", validationErrors);
        return problem;
    }

    @ExceptionHandler(Exception.class)
    public ProblemDetail handleGeneral(Exception ex, HttpServletRequest request) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage());
        problem.setTitle("Internal Server Error");
        problem.setType(URI.create(request.getRequestURL().toString()));
        problem.setProperty("timestamp", Instant.now());
        problem.setProperty("schemaValidationErrors", List.of());
        return problem;
    }

    private Map<String, String> fieldErrorToMap(FieldError error) {
        return Map.of("message", String.format("[Path '/%s'] %s", error.getField(), error.getDefaultMessage()));
    }
}
