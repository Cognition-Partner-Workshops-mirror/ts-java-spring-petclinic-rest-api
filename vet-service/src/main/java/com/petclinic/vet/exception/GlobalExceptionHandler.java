package com.petclinic.vet.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.net.URI;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ProblemDetail handleResourceNotFound(ResourceNotFoundException ex, HttpServletRequest request) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
        problemDetail.setTitle("ResourceNotFoundException");
        problemDetail.setType(URI.create(request.getRequestURL().toString()));
        problemDetail.setProperty("timestamp", Instant.now());
        problemDetail.setProperty("schemaValidationErrors", List.of());
        return problemDetail;
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail handleValidationErrors(MethodArgumentNotValidException ex, HttpServletRequest request) {
        List<Map<String, String>> validationErrors = ex.getBindingResult().getFieldErrors().stream()
            .map(this::mapFieldError)
            .collect(Collectors.toList());

        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, "Validation failed");
        problemDetail.setTitle("MethodArgumentNotValidException");
        problemDetail.setType(URI.create(request.getRequestURL().toString()));
        problemDetail.setProperty("timestamp", Instant.now());
        problemDetail.setProperty("schemaValidationErrors", validationErrors);
        return problemDetail;
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ProblemDetail handleIllegalArgument(IllegalArgumentException ex, HttpServletRequest request) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, ex.getMessage());
        problemDetail.setTitle("IllegalArgumentException");
        problemDetail.setType(URI.create(request.getRequestURL().toString()));
        problemDetail.setProperty("timestamp", Instant.now());
        problemDetail.setProperty("schemaValidationErrors", List.of());
        return problemDetail;
    }

    @ExceptionHandler(Exception.class)
    public ProblemDetail handleGenericException(Exception ex, HttpServletRequest request) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage());
        problemDetail.setTitle(ex.getClass().getSimpleName());
        problemDetail.setType(URI.create(request.getRequestURL().toString()));
        problemDetail.setProperty("timestamp", Instant.now());
        problemDetail.setProperty("schemaValidationErrors", List.of());
        return problemDetail;
    }

    private Map<String, String> mapFieldError(FieldError fieldError) {
        return Map.of("message",
            String.format("[Path '/%s'] %s", fieldError.getField(), fieldError.getDefaultMessage()));
    }
}
