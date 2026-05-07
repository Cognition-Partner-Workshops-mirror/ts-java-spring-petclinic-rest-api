package com.petclinic.vet.exception;

import jakarta.servlet.http.HttpServletRequest;
import java.net.URI;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ProblemDetail> handleNotFound(ResourceNotFoundException ex, HttpServletRequest request) {
        ProblemDetail detail = buildProblemDetail(ex, HttpStatus.NOT_FOUND, request, ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(detail);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ProblemDetail> handleValidation(MethodArgumentNotValidException ex, HttpServletRequest request) {
        ProblemDetail detail = buildProblemDetail(ex, HttpStatus.BAD_REQUEST, request,
            "The request contains invalid or missing parameters");
        List<Map<String, String>> errors = ex.getBindingResult().getFieldErrors().stream()
            .map(fe -> {
                String rejected = Objects.toString(fe.getRejectedValue(), "null");
                String defaultMsg = Objects.toString(fe.getDefaultMessage(), "Validation failed");
                return Map.of(
                    "message", "Field '%s' %s (rejected value: %s)".formatted(fe.getField(), defaultMsg, rejected),
                    "field", fe.getField(),
                    "rejectedValue", rejected,
                    "defaultMessage", defaultMsg
                );
            })
            .toList();
        detail.setProperty("schemaValidationErrors", errors);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(detail);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ProblemDetail> handleDataIntegrity(DataIntegrityViolationException ex, HttpServletRequest request) {
        ProblemDetail detail = buildProblemDetail(ex, HttpStatus.CONFLICT, request,
            "The requested resource could not be processed due to a data constraint violation");
        return ResponseEntity.status(HttpStatus.CONFLICT).body(detail);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ProblemDetail> handleGeneral(Exception ex, HttpServletRequest request) {
        ProblemDetail detail = buildProblemDetail(ex, HttpStatus.INTERNAL_SERVER_ERROR, request,
            "An unexpected error occurred while processing your request");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(detail);
    }

    private ProblemDetail buildProblemDetail(Exception ex, HttpStatus status, HttpServletRequest request, String detailMsg) {
        ProblemDetail problem = ProblemDetail.forStatus(status);
        problem.setType(URI.create(request.getRequestURL().toString()));
        problem.setTitle(ex.getClass().getSimpleName());
        problem.setDetail(detailMsg);
        problem.setProperty("timestamp", Instant.now());
        problem.setProperty("schemaValidationErrors", List.of());
        return problem;
    }
}
