package com.petclinic.vet.exception;

import java.net.URI;
import java.time.Instant;
import java.util.List;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ProblemDetail> handleNotFound(ResourceNotFoundException ex, WebRequest request) {
        ProblemDetail detail = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
        detail.setTitle(ex.getResourceName() + " not found");
        detail.setType(URI.create(requestUri(request)));
        detail.setProperty("timestamp", Instant.now());
        detail.setProperty("schemaValidationErrors", List.of());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(detail);
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex,
            HttpHeaders headers,
            HttpStatusCode status,
            WebRequest request) {

        List<ValidationError> errors = ex.getBindingResult().getFieldErrors().stream()
            .map(fe -> new ValidationError("[Path '/" + fe.getField() + "'] " + fe.getDefaultMessage()))
            .toList();

        ProblemDetail detail = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, "Validation failed");
        detail.setTitle("Bad Request");
        detail.setType(URI.create(requestUri(request)));
        detail.setProperty("timestamp", Instant.now());
        detail.setProperty("schemaValidationErrors", errors);
        return ResponseEntity.badRequest().body(detail);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ProblemDetail> handleGeneral(Exception ex, WebRequest request) {
        ProblemDetail detail = ProblemDetail.forStatusAndDetail(
            HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage());
        detail.setTitle("Internal Server Error");
        detail.setType(URI.create(requestUri(request)));
        detail.setProperty("timestamp", Instant.now());
        detail.setProperty("schemaValidationErrors", List.of());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(detail);
    }

    private String requestUri(WebRequest request) {
        if (request instanceof ServletWebRequest swr) {
            return swr.getRequest().getRequestURI();
        }
        return "about:blank";
    }

    record ValidationError(String message) {}
}
