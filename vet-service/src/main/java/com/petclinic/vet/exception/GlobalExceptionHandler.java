package com.petclinic.vet.exception;

import jakarta.servlet.http.HttpServletRequest;
import java.net.URI;
import java.time.Instant;
import java.util.List;
import java.util.Objects;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    @ResponseBody
    public ResponseEntity<ProblemDetail> handleResourceNotFound(ResourceNotFoundException ex, HttpServletRequest request) {
        ProblemDetail detail = buildProblemDetail(ex, HttpStatus.NOT_FOUND, request, ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(detail);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseBody
    public ResponseEntity<ProblemDetail> handleValidationException(MethodArgumentNotValidException ex, HttpServletRequest request) {
        ProblemDetail detail = buildProblemDetail(ex, HttpStatus.BAD_REQUEST, request, "The request contains invalid or missing parameters");
        List<ValidationMessage> errors = ex.getBindingResult().getFieldErrors().stream()
            .map(fieldError -> {
                String rejectedValue = Objects.toString(fieldError.getRejectedValue(), "null");
                String defaultMessage = Objects.toString(fieldError.getDefaultMessage(), "Validation failed");
                String message = "Field '%s' %s (rejected value: %s)".formatted(
                    fieldError.getField(), defaultMessage, rejectedValue);
                return new ValidationMessage(message);
            })
            .toList();
        detail.setProperty("schemaValidationErrors", errors);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(detail);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    @ResponseBody
    public ResponseEntity<ProblemDetail> handleDataIntegrityViolation(DataIntegrityViolationException ex, HttpServletRequest request) {
        ProblemDetail detail = buildProblemDetail(ex, HttpStatus.BAD_REQUEST, request,
            "The requested resource could not be processed due to a data constraint violation");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(detail);
    }

    @ExceptionHandler(Exception.class)
    @ResponseBody
    public ResponseEntity<ProblemDetail> handleGeneral(Exception ex, HttpServletRequest request) {
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

    public record ValidationMessage(String message) {}
}
