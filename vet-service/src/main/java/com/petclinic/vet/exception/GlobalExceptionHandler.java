package com.petclinic.vet.exception;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.net.URI;
import java.time.Instant;
import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ProblemDetailResponse> handleResourceNotFound(
            ResourceNotFoundException ex, WebRequest request) {
        ProblemDetailResponse problem = new ProblemDetailResponse(
            URI.create(getRequestUri(request)),
            "ResourceNotFoundException",
            HttpStatus.NOT_FOUND.value(),
            ex.getMessage(),
            Instant.now(),
            null
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(problem);
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex,
            HttpHeaders headers,
            HttpStatusCode status,
            WebRequest request) {
        List<ValidationMessage> errors = ex.getBindingResult().getFieldErrors().stream()
            .map(fieldError -> new ValidationMessage(
                String.format("[Path '/%s'] %s", fieldError.getField(), fieldError.getDefaultMessage())))
            .toList();

        ProblemDetailResponse problem = new ProblemDetailResponse(
            URI.create(getRequestUri(request)),
            "MethodArgumentNotValidException",
            HttpStatus.BAD_REQUEST.value(),
            "Validation failed",
            Instant.now(),
            errors
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(problem);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ProblemDetailResponse> handleGenericException(
            Exception ex, WebRequest request) {
        ProblemDetailResponse problem = new ProblemDetailResponse(
            URI.create(getRequestUri(request)),
            ex.getClass().getSimpleName(),
            HttpStatus.INTERNAL_SERVER_ERROR.value(),
            ex.getMessage(),
            Instant.now(),
            null
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(problem);
    }

    private String getRequestUri(WebRequest request) {
        if (request instanceof ServletWebRequest servletRequest) {
            return servletRequest.getRequest().getRequestURI();
        }
        return "about:blank";
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record ProblemDetailResponse(
        URI type,
        String title,
        int status,
        String detail,
        Instant timestamp,
        List<ValidationMessage> schemaValidationErrors
    ) {}

    public record ValidationMessage(String message) {}
}
