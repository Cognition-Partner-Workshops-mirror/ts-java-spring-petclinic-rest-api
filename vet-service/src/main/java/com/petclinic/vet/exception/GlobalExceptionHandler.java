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

        List<Map<String, String>> errors = ex.getBindingResult().getFieldErrors().stream()
            .map(fieldError -> Map.of(
                "field", fieldError.getField(),
                "rejectedValue", Objects.toString(fieldError.getRejectedValue(), "null"),
                "message", Objects.toString(fieldError.getDefaultMessage(), "Validation failed")
            ))
            .toList();
        detail.setProperty("schemaValidationErrors", errors);

        return ResponseEntity.status(status).body(detail);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ProblemDetail> handleDataIntegrity(DataIntegrityViolationException ex, HttpServletRequest request) {
        log.warn("Data integrity violation: {}", ex.getMessage());
        ProblemDetail detail = buildProblemDetail(ex, HttpStatus.CONFLICT, request,
            "The requested operation violates a data constraint");
        return ResponseEntity.status(HttpStatus.CONFLICT).body(detail);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ProblemDetail> handleGeneral(Exception ex, HttpServletRequest request) {
        log.error("Unexpected error at {} {}", request.getMethod(), request.getRequestURI(), ex);
        ProblemDetail detail = buildProblemDetail(ex, HttpStatus.INTERNAL_SERVER_ERROR, request,
            "An unexpected error occurred while processing your request");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(detail);
    }

    private ProblemDetail buildProblemDetail(Exception ex, HttpStatus status, HttpServletRequest request, String detail) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(status, detail);
        problemDetail.setType(URI.create(request.getRequestURL().toString()));
        problemDetail.setTitle(ex.getClass().getSimpleName());
        problemDetail.setProperty("timestamp", Instant.now());
        return problemDetail;
    }
}
