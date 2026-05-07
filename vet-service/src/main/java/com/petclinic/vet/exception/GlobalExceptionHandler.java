package com.petclinic.vet.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.net.URI;
import java.time.Instant;
import java.util.List;
import java.util.Map;

/**
 * Global exception handler producing RFC 7807 Problem Details responses.
 * Handles resource-not-found (404), validation errors (400), and unexpected exceptions (500).
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /** Handles missing resources — returns 404 with a descriptive message. */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ProblemDetail handleNotFound(ResourceNotFoundException ex, WebRequest request) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
        problem.setTitle("Not Found");
        problem.setType(URI.create("about:blank"));
        problem.setProperty("timestamp", Instant.now());
        problem.setProperty("schemaValidationErrors", List.of());
        return problem;
    }

    /** Handles @Valid failures — returns 400 with per-field error details. */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail handleValidation(MethodArgumentNotValidException ex) {
        List<Map<String, String>> errors = ex.getBindingResult().getFieldErrors().stream()
            .map(fe -> Map.of("message", "[Path '/" + fe.getField() + "'] " + fe.getDefaultMessage()))
            .toList();

        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, "Validation failed");
        problem.setTitle("Bad Request");
        problem.setType(URI.create("about:blank"));
        problem.setProperty("timestamp", Instant.now());
        problem.setProperty("schemaValidationErrors", errors);
        return problem;
    }

    /** Catch-all for unexpected errors — returns 500. */
    @ExceptionHandler(Exception.class)
    public ProblemDetail handleGeneral(Exception ex) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
            HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage());
        problem.setTitle("Internal Server Error");
        problem.setType(URI.create("about:blank"));
        problem.setProperty("timestamp", Instant.now());
        problem.setProperty("schemaValidationErrors", List.of());
        return problem;
    }
}
