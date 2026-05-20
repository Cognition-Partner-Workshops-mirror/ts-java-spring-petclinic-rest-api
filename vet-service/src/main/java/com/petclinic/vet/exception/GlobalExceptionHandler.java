package com.petclinic.vet.exception;

import com.petclinic.vet.dto.ValidationMessageDto;
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

/**
 * Global exception handler for the Vet microservice.
 * Returns RFC 7807 Problem Details responses for all error types.
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * Build a ProblemDetail response with the standard fields matching the OpenAPI ProblemDetail schema.
     */
    private ProblemDetail buildProblemDetail(Exception e, HttpStatus status, String url, String detail,
                                            List<ValidationMessageDto> validationErrors) {
        ProblemDetail problemDetail = ProblemDetail.forStatus(status);
        problemDetail.setType(URI.create(url));
        problemDetail.setTitle(e.getClass().getSimpleName());
        problemDetail.setDetail(detail);
        problemDetail.setProperty("timestamp", Instant.now());
        problemDetail.setProperty("schemaValidationErrors", validationErrors);
        return problemDetail;
    }

    /** Handle resource not found errors (404). */
    @ExceptionHandler(ResourceNotFoundException.class)
    @ResponseBody
    public ResponseEntity<ProblemDetail> handleNotFound(ResourceNotFoundException e, HttpServletRequest request) {
        logger.warn("Resource not found at {} {}: {}", request.getMethod(), request.getRequestURI(), e.getMessage());
        ProblemDetail detail = buildProblemDetail(e, HttpStatus.NOT_FOUND,
            request.getRequestURL().toString(), e.getMessage(), List.of());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(detail);
    }

    /** Handle bean validation errors (400). */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseBody
    public ResponseEntity<ProblemDetail> handleValidation(MethodArgumentNotValidException e,
                                                          HttpServletRequest request) {
        logger.warn("Validation error at {} {}: {}", request.getMethod(), request.getRequestURI(), e.getMessage());
        List<ValidationMessageDto> errors = e.getBindingResult().getFieldErrors().stream()
            .map(fe -> new ValidationMessageDto(
                "[Path '/" + fe.getField() + "'] " + fe.getDefaultMessage()))
            .toList();
        ProblemDetail detail = buildProblemDetail(e, HttpStatus.BAD_REQUEST,
            request.getRequestURL().toString(),
            "The request contains invalid or missing parameters", errors);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(detail);
    }

    /** Handle database constraint violations (400). */
    @ExceptionHandler(DataIntegrityViolationException.class)
    @ResponseBody
    public ResponseEntity<ProblemDetail> handleDataIntegrity(DataIntegrityViolationException e,
                                                             HttpServletRequest request) {
        logger.warn("Data integrity violation at {} {}: {}", request.getMethod(), request.getRequestURI(),
            e.getMessage());
        ProblemDetail detail = buildProblemDetail(e, HttpStatus.BAD_REQUEST,
            request.getRequestURL().toString(),
            "Data constraint violation: " + e.getMostSpecificCause().getMessage(), List.of());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(detail);
    }

    /** Handle all other unexpected exceptions (500). */
    @ExceptionHandler(Exception.class)
    @ResponseBody
    public ResponseEntity<ProblemDetail> handleGeneral(Exception e, HttpServletRequest request) {
        logger.error("Unexpected error at {} {}", request.getMethod(), request.getRequestURI(), e);
        ProblemDetail detail = buildProblemDetail(e, HttpStatus.INTERNAL_SERVER_ERROR,
            request.getRequestURL().toString(),
            "An unexpected error occurred while processing your request", List.of());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(detail);
    }
}
