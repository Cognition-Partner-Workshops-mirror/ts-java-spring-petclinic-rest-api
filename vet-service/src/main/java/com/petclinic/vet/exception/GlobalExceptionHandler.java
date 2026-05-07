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
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ProblemDetail handleResourceNotFound(ResourceNotFoundException ex, HttpServletRequest request) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
        problem.setTitle("Not Found");
        problem.setType(URI.create(request.getRequestURL().toString()));
        problem.setProperty("timestamp", Instant.now());
        problem.setProperty("schemaValidationErrors", List.of());
        return problem;
    }

    @ExceptionHandler(DuplicateResourceException.class)
    public ProblemDetail handleDuplicateResource(DuplicateResourceException ex, HttpServletRequest request) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, ex.getMessage());
        problem.setTitle("Bad Request");
        problem.setType(URI.create(request.getRequestURL().toString()));
        problem.setProperty("timestamp", Instant.now());
        problem.setProperty("schemaValidationErrors", List.of());
        return problem;
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail handleValidationErrors(MethodArgumentNotValidException ex, HttpServletRequest request) {
        List<Map<String, String>> validationErrors = ex.getBindingResult().getAllErrors().stream()
            .map(error -> {
                String field = error instanceof FieldError fe ? fe.getField() : error.getObjectName();
                String message = error.getDefaultMessage();
                return Map.of("message", String.format("[Path '/%s'] %s", field, message));
            })
            .collect(Collectors.toList());

        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, "Validation failed");
        problem.setTitle("Bad Request");
        problem.setType(URI.create(request.getRequestURL().toString()));
        problem.setProperty("timestamp", Instant.now());
        problem.setProperty("schemaValidationErrors", validationErrors);
        return problem;
    }

    @ExceptionHandler(Exception.class)
    public ProblemDetail handleGenericException(Exception ex, HttpServletRequest request) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage());
        problem.setTitle("Internal Server Error");
        problem.setType(URI.create(request.getRequestURL().toString()));
        problem.setProperty("timestamp", Instant.now());
        problem.setProperty("schemaValidationErrors", List.of());
        return problem;
    }
}
