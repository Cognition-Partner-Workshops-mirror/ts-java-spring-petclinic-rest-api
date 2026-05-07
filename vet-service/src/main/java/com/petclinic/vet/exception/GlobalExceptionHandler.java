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

@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    private ProblemDetail buildProblemDetail(Exception e, HttpStatus status, String url, String detail) {
        ProblemDetail problemDetail = ProblemDetail.forStatus(status);
        problemDetail.setType(URI.create(url));
        problemDetail.setTitle(e.getClass().getSimpleName());
        problemDetail.setDetail(detail);
        problemDetail.setProperty("timestamp", Instant.now());
        problemDetail.setProperty("schemaValidationErrors", List.of());
        return problemDetail;
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    @ResponseBody
    public ResponseEntity<ProblemDetail> handleResourceNotFoundException(ResourceNotFoundException e, HttpServletRequest request) {
        logger.warn("Resource not found at {} {}: {}", request.getMethod(), request.getRequestURI(), e.getMessage());
        HttpStatus status = HttpStatus.NOT_FOUND;
        ProblemDetail detail = buildProblemDetail(e, status, request.getRequestURL().toString(), e.getMessage());
        return ResponseEntity.status(status).body(detail);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseBody
    public ResponseEntity<ProblemDetail> handleValidationException(MethodArgumentNotValidException e, HttpServletRequest request) {
        HttpStatus status = HttpStatus.BAD_REQUEST;
        ProblemDetail detail = buildProblemDetail(e, status, request.getRequestURL().toString(),
            "The request contains invalid or missing parameters");
        if (e.getBindingResult().hasErrors()) {
            List<ValidationMessageDto> errors = e.getBindingResult().getFieldErrors().stream()
                .map(fieldError -> {
                    String rejectedValue = Objects.toString(fieldError.getRejectedValue(), "null");
                    String defaultMessage = Objects.toString(fieldError.getDefaultMessage(), "Validation failed");
                    String message = "Field '%s' %s (rejected value: %s)".formatted(
                        fieldError.getField(), defaultMessage, rejectedValue);
                    return new ValidationMessageDto(message)
                        .putAdditionalProperty("field", fieldError.getField())
                        .putAdditionalProperty("rejectedValue", rejectedValue)
                        .putAdditionalProperty("defaultMessage", defaultMessage);
                })
                .toList();
            detail.setProperty("schemaValidationErrors", errors);
        }
        return ResponseEntity.status(status).body(detail);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    @ResponseBody
    public ResponseEntity<ProblemDetail> handleDataIntegrityViolation(DataIntegrityViolationException e, HttpServletRequest request) {
        logger.warn("Data integrity violation at {} {}: {}", request.getMethod(), request.getRequestURI(), e.getMessage());
        HttpStatus status = HttpStatus.CONFLICT;
        ProblemDetail detail = buildProblemDetail(e, status, request.getRequestURL().toString(),
            "Data constraint violation");
        return ResponseEntity.status(status).body(detail);
    }

    @ExceptionHandler(Exception.class)
    @ResponseBody
    public ResponseEntity<ProblemDetail> handleGeneralException(Exception e, HttpServletRequest request) {
        logger.error("Unexpected error at {} {}", request.getMethod(), request.getRequestURI(), e);
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        ProblemDetail detail = buildProblemDetail(e, status, request.getRequestURL().toString(),
            "An unexpected error occurred while processing your request");
        return ResponseEntity.status(status).body(detail);
    }
}
