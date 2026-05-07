package com.petclinic.vet.exception;

import java.net.URI;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;

/**
 * Centralized exception handler producing RFC 7807 Problem Detail responses.
 * Handles validation errors, not-found exceptions, and unexpected failures.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ProblemDetail> handleNotFound(ResourceNotFoundException ex, WebRequest request) {
        ProblemDetail detail = buildProblemDetail(ex, HttpStatus.NOT_FOUND, request, ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(detail);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ProblemDetail> handleValidation(MethodArgumentNotValidException ex, WebRequest request) {
        ProblemDetail detail = buildProblemDetail(ex, HttpStatus.BAD_REQUEST, request,
            "The request contains invalid or missing parameters");
        List<Map<String, String>> errors = ex.getBindingResult().getFieldErrors().stream()
            .map(fe -> Map.of(
                "field", fe.getField(),
                "message", "Field '%s' %s (rejected value: %s)".formatted(
                    fe.getField(),
                    Objects.toString(fe.getDefaultMessage(), "Validation failed"),
                    Objects.toString(fe.getRejectedValue(), "null"))
            ))
            .toList();
        detail.setProperty("schemaValidationErrors", errors);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(detail);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ProblemDetail> handleGeneral(Exception ex, WebRequest request) {
        ProblemDetail detail = buildProblemDetail(ex, HttpStatus.INTERNAL_SERVER_ERROR, request,
            "An unexpected error occurred while processing your request");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(detail);
    }

    private ProblemDetail buildProblemDetail(Exception ex, HttpStatus status, WebRequest request, String detailMsg) {
        ProblemDetail pd = ProblemDetail.forStatus(status);
        if (request instanceof ServletWebRequest swr) {
            pd.setType(URI.create(swr.getRequest().getRequestURL().toString()));
        }
        pd.setTitle(ex.getClass().getSimpleName());
        pd.setDetail(detailMsg);
        pd.setProperty("timestamp", Instant.now());
        pd.setProperty("schemaValidationErrors", List.of());
        return pd;
    }
}
