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

import java.net.URI;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ProblemDetail> handleNotFound(ResourceNotFoundException ex, HttpServletRequest request) {
        log.debug("Resource not found: {}", ex.getMessage());
        ProblemDetail detail = buildProblemDetail(ex, HttpStatus.NOT_FOUND, request, ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(detail);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ProblemDetail> handleValidation(MethodArgumentNotValidException ex, HttpServletRequest request) {
        HttpStatus status = HttpStatus.BAD_REQUEST;
        ProblemDetail detail = buildProblemDetail(ex, status, request, "The request contains invalid or missing parameters");

        List<Map<String, Object>> errors = ex.getBindingResult().getFieldErrors().stream()
            .map(fieldError -> {
                String rejectedValue = Objects.toString(fieldError.getRejectedValue(), "null");
                String defaultMessage = Objects.toString(fieldError.getDefaultMessage(), "Validation failed");
                String message = "Field '%s' %s (rejected value: %s)".formatted(
                    fieldError.getField(), defaultMessage, rejectedValue);
                return Map.<String, Object>of(
                    "message", message,
                    "field", fieldError.getField(),
                    "rejectedValue", rejectedValue
                );
            })
            .toList();

        detail.setProperty("schemaValidationErrors", errors);
        return ResponseEntity.status(status).body(detail);
    }

    // Handles bad request input such as unknown specialty names during vet create/update
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ProblemDetail> handleIllegalArgument(IllegalArgumentException ex, HttpServletRequest request) {
        log.debug("Bad request: {}", ex.getMessage());
        ProblemDetail detail = buildProblemDetail(ex, HttpStatus.BAD_REQUEST, request, ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(detail);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ProblemDetail> handleDataIntegrity(DataIntegrityViolationException ex, HttpServletRequest request) {
        log.warn("Data integrity violation: {}", ex.getMessage());
        ProblemDetail detail = buildProblemDetail(ex, HttpStatus.CONFLICT, request,
            "The requested resource could not be processed due to a data constraint violation");
        return ResponseEntity.status(HttpStatus.CONFLICT).body(detail);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ProblemDetail> handleGeneral(Exception ex, HttpServletRequest request) {
        log.error("Unexpected error at {} {}", request.getMethod(), request.getRequestURI(), ex);
        ProblemDetail detail = buildProblemDetail(ex, HttpStatus.INTERNAL_SERVER_ERROR, request,
            "An unexpected error occurred while processing your request");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(detail);
    }

    private ProblemDetail buildProblemDetail(Exception ex, HttpStatus status, HttpServletRequest request, String detailMsg) {
        ProblemDetail problemDetail = ProblemDetail.forStatus(status);
        problemDetail.setType(URI.create(request.getRequestURL().toString()));
        problemDetail.setTitle(ex.getClass().getSimpleName());
        problemDetail.setDetail(detailMsg);
        problemDetail.setProperty("timestamp", Instant.now());
        problemDetail.setProperty("schemaValidationErrors", List.of());
        return problemDetail;
    }
}
