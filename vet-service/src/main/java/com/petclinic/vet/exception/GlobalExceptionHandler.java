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
import java.util.Objects;

/**
 * Global exception handler producing RFC 7807 ProblemDetail responses.
 * Modeled after the monolith's ExceptionControllerAdvice.
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);
    private static final String ERROR_UNEXPECTED = "An unexpected error occurred while processing your request";
    private static final String ERROR_DATA_INTEGRITY = "The requested resource could not be processed due to a data constraint violation";
    private static final String ERROR_INVALID_REQUEST = "The request contains invalid or missing parameters";

    /**
     * Builds a ProblemDetail with standard fields including timestamp and empty validation errors.
     */
    private ProblemDetail buildProblemDetail(Exception e, HttpStatus status, StringBuffer url, String detail) {
        ProblemDetail problemDetail = ProblemDetail.forStatus(status);
        problemDetail.setType(URI.create(url.toString()));
        problemDetail.setTitle(e.getClass().getSimpleName());
        problemDetail.setDetail(detail);
        problemDetail.setProperty("timestamp", Instant.now());
        problemDetail.setProperty("schemaValidationErrors", List.<ValidationMessageDto>of());
        return problemDetail;
    }

    /**
     * Handles ResourceNotFoundException -> 404 Not Found.
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    @ResponseBody
    public ResponseEntity<ProblemDetail> handleResourceNotFoundException(ResourceNotFoundException e, HttpServletRequest request) {
        logger.warn("Resource not found at {} {}: {}", request.getMethod(), request.getRequestURI(), e.getMessage());
        HttpStatus status = HttpStatus.NOT_FOUND;
        ProblemDetail detail = buildProblemDetail(e, status, request.getRequestURL(), e.getMessage());
        return ResponseEntity.status(status).body(detail);
    }

    /**
     * Handles validation errors -> 400 Bad Request with field-level error details.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseBody
    public ResponseEntity<ProblemDetail> handleMethodArgumentNotValidException(MethodArgumentNotValidException e, HttpServletRequest request) {
        HttpStatus status = HttpStatus.BAD_REQUEST;
        ProblemDetail detail = buildProblemDetail(e, status, request.getRequestURL(), ERROR_INVALID_REQUEST);
        if (e.getBindingResult().hasErrors()) {
            List<ValidationMessageDto> schemaValidationErrors = e.getBindingResult().getFieldErrors().stream()
                .map(fieldError -> {
                    String rejectedValue = Objects.toString(fieldError.getRejectedValue(), "null");
                    String defaultMessage = Objects.toString(fieldError.getDefaultMessage(), "Validation failed");
                    String message = "Field '%s' %s (rejected value: %s)".formatted(
                        fieldError.getField(),
                        defaultMessage,
                        rejectedValue);
                    ValidationMessageDto dto = new ValidationMessageDto(message);
                    dto.putAdditionalProperty("field", fieldError.getField());
                    dto.putAdditionalProperty("rejectedValue", rejectedValue);
                    dto.putAdditionalProperty("defaultMessage", defaultMessage);
                    return dto;
                })
                .toList();
            logger.debug("Validation error at {} {}: {}", request.getMethod(), request.getRequestURI(), e.getBindingResult().getFieldErrors());
            detail.setProperty("schemaValidationErrors", schemaValidationErrors);
        }
        return ResponseEntity.status(status).body(detail);
    }

    /**
     * Handles database constraint violations -> 409 Conflict.
     */
    @ExceptionHandler(DataIntegrityViolationException.class)
    @ResponseBody
    public ResponseEntity<ProblemDetail> handleDataIntegrityViolationException(DataIntegrityViolationException e, HttpServletRequest request) {
        logger.warn("Data integrity violation at {} {}: {}", request.getMethod(), request.getRequestURI(), e.getMessage());
        HttpStatus status = HttpStatus.CONFLICT;
        ProblemDetail detail = buildProblemDetail(e, status, request.getRequestURL(), ERROR_DATA_INTEGRITY);
        return ResponseEntity.status(status).body(detail);
    }

    /**
     * Catches all other unhandled exceptions -> 500 Internal Server Error.
     */
    @ExceptionHandler(Exception.class)
    @ResponseBody
    public ResponseEntity<ProblemDetail> handleGeneralException(Exception e, HttpServletRequest request) {
        logger.error("Unexpected error at {} {}", request.getMethod(), request.getRequestURI(), e);
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        ProblemDetail detail = buildProblemDetail(e, status, request.getRequestURL(), ERROR_UNEXPECTED);
        return ResponseEntity.status(status).body(detail);
    }
}
