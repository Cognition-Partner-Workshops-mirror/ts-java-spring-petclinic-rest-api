package com.petclinic.vet.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import java.net.URI;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Global exception handler producing RFC 7807 Problem Details responses.
 * Covers validation errors, not-found, duplicates, data integrity, and unexpected exceptions.
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * Build a standard ProblemDetail with timestamp and empty validation errors.
     */
    private ProblemDetail buildProblemDetail(Exception e, HttpStatus status, String url, String detail) {
        ProblemDetail problemDetail = ProblemDetail.forStatus(status);
        problemDetail.setType(URI.create(url));
        problemDetail.setTitle(e.getClass().getSimpleName());
        problemDetail.setDetail(detail);
        problemDetail.setProperty("timestamp", Instant.now());
        problemDetail.setProperty("schemaValidationErrors", List.of());
        return problemDetail;
    }

    /** Handle resource not found — returns 404 */
    @ExceptionHandler(ResourceNotFoundException.class)
    @ResponseBody
    public ResponseEntity<ProblemDetail> handleResourceNotFound(ResourceNotFoundException e, HttpServletRequest request) {
        logger.warn("Resource not found at {} {}: {}", request.getMethod(), request.getRequestURI(), e.getMessage());
        HttpStatus status = HttpStatus.NOT_FOUND;
        ProblemDetail detail = buildProblemDetail(e, status, request.getRequestURL().toString(), e.getMessage());
        return ResponseEntity.status(status).body(detail);
    }

    /** Handle duplicate resource — returns 409 Conflict */
    @ExceptionHandler(DuplicateResourceException.class)
    @ResponseBody
    public ResponseEntity<ProblemDetail> handleDuplicateResource(DuplicateResourceException e, HttpServletRequest request) {
        logger.warn("Duplicate resource at {} {}: {}", request.getMethod(), request.getRequestURI(), e.getMessage());
        HttpStatus status = HttpStatus.CONFLICT;
        ProblemDetail detail = buildProblemDetail(e, status, request.getRequestURL().toString(), e.getMessage());
        return ResponseEntity.status(status).body(detail);
    }

    /** Handle bean validation errors — returns 400 with field-level details */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseBody
    public ResponseEntity<ProblemDetail> handleValidationException(MethodArgumentNotValidException e, HttpServletRequest request) {
        HttpStatus status = HttpStatus.BAD_REQUEST;
        ProblemDetail detail = buildProblemDetail(e, status, request.getRequestURL().toString(),
            "The request contains invalid or missing parameters");

        // Attach field-level validation errors matching OpenAPI ValidationMessage schema
        List<Map<String, Object>> validationErrors = e.getBindingResult().getFieldErrors().stream()
            .map(fieldError -> {
                String rejectedValue = Objects.toString(fieldError.getRejectedValue(), "null");
                String defaultMessage = Objects.toString(fieldError.getDefaultMessage(), "Validation failed");
                String message = String.format("Field '%s' %s (rejected value: %s)",
                    fieldError.getField(), defaultMessage, rejectedValue);
                return Map.<String, Object>of(
                    "message", message,
                    "field", fieldError.getField(),
                    "rejectedValue", rejectedValue,
                    "defaultMessage", defaultMessage
                );
            })
            .toList();
        detail.setProperty("schemaValidationErrors", validationErrors);
        logger.debug("Validation error at {} {}: {}", request.getMethod(), request.getRequestURI(),
            e.getBindingResult().getFieldErrors());
        return ResponseEntity.status(status).body(detail);
    }

    /** Handle data integrity violations — returns 404 (matching monolith convention) */
    @ExceptionHandler(DataIntegrityViolationException.class)
    @ResponseBody
    public ResponseEntity<ProblemDetail> handleDataIntegrityViolation(DataIntegrityViolationException e, HttpServletRequest request) {
        logger.warn("Data integrity violation at {} {}: {}", request.getMethod(), request.getRequestURI(), e.getMessage());
        HttpStatus status = HttpStatus.CONFLICT;
        ProblemDetail detail = buildProblemDetail(e, status, request.getRequestURL().toString(),
            "The requested resource could not be processed due to a data constraint violation");
        return ResponseEntity.status(status).body(detail);
    }

    /** Catch-all for unexpected exceptions — returns 500 */
    @ExceptionHandler(Exception.class)
    @ResponseBody
    public ResponseEntity<ProblemDetail> handleGenericException(Exception e, HttpServletRequest request) {
        logger.error("Unexpected error at {} {}", request.getMethod(), request.getRequestURI(), e);
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        ProblemDetail detail = buildProblemDetail(e, status, request.getRequestURL().toString(),
            "An unexpected error occurred while processing your request");
        return ResponseEntity.status(status).body(detail);
    }
}
