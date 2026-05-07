package com.petclinic.vet.exception;

import com.petclinic.vet.dto.ValidationMessageDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.net.URI;
import java.time.Instant;
import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ProblemDetail handleResourceNotFound(ResourceNotFoundException ex, WebRequest request) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
        problem.setTitle(ex.getResourceName() + " not found");
        problem.setType(URI.create(request.getDescription(false).replace("uri=", "")));
        problem.setProperty("timestamp", Instant.now());
        problem.setProperty("schemaValidationErrors", List.of());
        return problem;
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail handleValidation(MethodArgumentNotValidException ex, WebRequest request) {
        List<ValidationMessageDto> errors = ex.getBindingResult().getFieldErrors().stream()
            .map(fe -> new ValidationMessageDto("[Path '/" + fe.getField() + "'] " + fe.getDefaultMessage()))
            .toList();

        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, "Validation failed");
        problem.setTitle("Bad Request");
        problem.setType(URI.create(request.getDescription(false).replace("uri=", "")));
        problem.setProperty("timestamp", Instant.now());
        problem.setProperty("schemaValidationErrors", errors);
        return problem;
    }

    @ExceptionHandler(Exception.class)
    public ProblemDetail handleGeneral(Exception ex, WebRequest request) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
            HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage());
        problem.setTitle(ex.getClass().getSimpleName());
        problem.setType(URI.create(request.getDescription(false).replace("uri=", "")));
        problem.setProperty("timestamp", Instant.now());
        problem.setProperty("schemaValidationErrors", List.of());
        return problem;
    }
}
