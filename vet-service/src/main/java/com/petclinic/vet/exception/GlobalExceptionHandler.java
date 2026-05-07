package com.petclinic.vet.exception;

import com.petclinic.vet.dto.ProblemDetailDto;
import com.petclinic.vet.dto.ValidationMessageDto;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.net.URI;
import java.time.Instant;
import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ProblemDetailDto> handleNotFound(
            ResourceNotFoundException ex, HttpServletRequest request) {
        ProblemDetailDto problem = new ProblemDetailDto(
            URI.create(request.getRequestURL().toString()),
            "ResourceNotFoundException",
            HttpStatus.NOT_FOUND.value(),
            ex.getMessage(),
            Instant.now(),
            List.of()
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(problem);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ProblemDetailDto> handleValidation(
            MethodArgumentNotValidException ex, HttpServletRequest request) {
        List<ValidationMessageDto> errors = ex.getBindingResult().getFieldErrors().stream()
            .map(fe -> new ValidationMessageDto(
                "[Path '/" + fe.getField() + "'] " + fe.getDefaultMessage()))
            .toList();
        ProblemDetailDto problem = new ProblemDetailDto(
            URI.create(request.getRequestURL().toString()),
            "MethodArgumentNotValidException",
            HttpStatus.BAD_REQUEST.value(),
            "Validation failed",
            Instant.now(),
            errors
        );
        return ResponseEntity.badRequest().body(problem);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ProblemDetailDto> handleBadRequest(
            IllegalArgumentException ex, HttpServletRequest request) {
        ProblemDetailDto problem = new ProblemDetailDto(
            URI.create(request.getRequestURL().toString()),
            "IllegalArgumentException",
            HttpStatus.BAD_REQUEST.value(),
            ex.getMessage(),
            Instant.now(),
            List.of()
        );
        return ResponseEntity.badRequest().body(problem);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ProblemDetailDto> handleGeneric(
            Exception ex, HttpServletRequest request) {
        ProblemDetailDto problem = new ProblemDetailDto(
            URI.create(request.getRequestURL().toString()),
            ex.getClass().getSimpleName(),
            HttpStatus.INTERNAL_SERVER_ERROR.value(),
            ex.getMessage(),
            Instant.now(),
            List.of()
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(problem);
    }
}
