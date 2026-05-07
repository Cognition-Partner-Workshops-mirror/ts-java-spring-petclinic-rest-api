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
import java.util.stream.Collectors;

/**
 * Global exception handler producing RFC 7807 Problem Detail responses.
 * Covers resource-not-found, validation errors, and unexpected server errors.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /** Handle 404 — resource not found */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ProblemDetail handleResourceNotFound(ResourceNotFoundException ex, WebRequest request) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
            HttpStatus.NOT_FOUND, ex.getMessage());
        problem.setTitle("Resource Not Found");
        problem.setType(URI.create("about:blank"));
        problem.setProperty("timestamp", Instant.now().toString());
        problem.setProperty("schemaValidationErrors", List.of());
        return problem;
    }

    /** Handle 400 — bean validation failures from @Valid */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail handleValidation(MethodArgumentNotValidException ex, WebRequest request) {
        // Collect all field-level validation error messages
        List<Map<String, String>> validationErrors = ex.getBindingResult()
            .getFieldErrors()
            .stream()
            .map(fieldError -> Map.of("message",
                String.format("[Path '/%s'] %s", fieldError.getField(), fieldError.getDefaultMessage())))
            .collect(Collectors.toList());

        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
            HttpStatus.BAD_REQUEST, "Validation failed for one or more fields.");
        problem.setTitle("Bad Request");
        problem.setType(URI.create("about:blank"));
        problem.setProperty("timestamp", Instant.now().toString());
        problem.setProperty("schemaValidationErrors", validationErrors);
        return problem;
    }

    /** Handle 500 — catch-all for unexpected exceptions */
    @ExceptionHandler(Exception.class)
    public ProblemDetail handleGeneral(Exception ex, WebRequest request) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
            HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage());
        problem.setTitle("Internal Server Error");
        problem.setType(URI.create("about:blank"));
        problem.setProperty("timestamp", Instant.now().toString());
        problem.setProperty("schemaValidationErrors", List.of());
        return problem;
    }
}
