package com.petclinic.vet.exception;

import java.net.URI;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
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
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
        problem.setTitle("Not Found");
        problem.setType(getRequestUri(request));
        problem.setProperty("timestamp", Instant.now());
        problem.setProperty("schemaValidationErrors", List.of());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(problem);
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex,
            HttpHeaders headers,
            HttpStatusCode status,
            WebRequest request) {
        List<Map<String, String>> validationErrors = ex.getBindingResult().getFieldErrors().stream()
            .map(fe -> Map.of("message", "[Path '/" + fe.getField() + "'] " + fe.getDefaultMessage()))
            .collect(Collectors.toList());

        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, "Validation failed");
        problem.setTitle("Bad Request");
        problem.setType(getRequestUri(request));
        problem.setProperty("timestamp", Instant.now());
        problem.setProperty("schemaValidationErrors", validationErrors);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(problem);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ProblemDetail> handleGeneral(Exception ex, WebRequest request) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage());
        problem.setTitle("Internal Server Error");
        problem.setType(getRequestUri(request));
        problem.setProperty("timestamp", Instant.now());
        problem.setProperty("schemaValidationErrors", List.of());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(problem);
    }

    private URI getRequestUri(WebRequest request) {
        if (request instanceof ServletWebRequest servletRequest) {
            return URI.create(servletRequest.getRequest().getRequestURI());
        }
        return URI.create("about:blank");
    }
}
