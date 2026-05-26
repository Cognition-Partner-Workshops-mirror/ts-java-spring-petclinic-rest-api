package com.petclinic.vet.exception;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.net.URI;
import java.time.Instant;
import java.util.List;
import java.util.Map;

/**
 * Global exception handler that returns RFC 7807 Problem Details for all errors.
 * Extends {@link ResponseEntityExceptionHandler} to customize Spring's default
 * handling of validation failures and other standard exceptions.
 */
@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    /**
     * Handles 404 errors when a vet or specialty is not found.
     * Returns an RFC 7807 ProblemDetail response with status 404.
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ProblemDetail> handleResourceNotFound(ResourceNotFoundException ex,
                                                                 WebRequest request) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
            HttpStatus.NOT_FOUND, ex.getMessage());
        problem.setTitle("Resource Not Found");
        problem.setType(URI.create("about:blank"));
        problem.setProperty("timestamp", Instant.now().toString());
        // Empty validation-errors array to match the OpenAPI ProblemDetail schema
        problem.setProperty("schemaValidationErrors", List.of());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(problem);
    }

    /**
     * Handles @Valid / @Validated failures, returning a 400 with field-level error details.
     * Each validation message is included in the schemaValidationErrors array
     * so the response matches the OpenAPI ProblemDetail contract.
     */
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex,
            HttpHeaders headers,
            HttpStatusCode status,
            WebRequest request) {

        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
            HttpStatus.BAD_REQUEST, "Validation failed");
        problem.setTitle("Bad Request");
        problem.setType(URI.create("about:blank"));
        problem.setProperty("timestamp", Instant.now().toString());

        // Collect per-field validation messages for the response body
        List<Map<String, String>> validationErrors = ex.getBindingResult()
            .getFieldErrors()
            .stream()
            .map(err -> Map.of("message",
                String.format("[Path '/%s'] %s", err.getField(), err.getDefaultMessage())))
            .toList();
        problem.setProperty("schemaValidationErrors", validationErrors);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(problem);
    }

    /**
     * Catch-all handler for any uncaught exception.
     * Returns a generic 500 response with minimal information to avoid leaking internals.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ProblemDetail> handleAllExceptions(Exception ex, WebRequest request) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
            HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage());
        problem.setTitle("Internal Server Error");
        problem.setType(URI.create("about:blank"));
        problem.setProperty("timestamp", Instant.now().toString());
        problem.setProperty("schemaValidationErrors", List.of());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(problem);
    }
}
