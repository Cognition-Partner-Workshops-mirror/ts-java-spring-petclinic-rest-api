package com.petclinic.vet.exception;

import com.petclinic.vet.dto.ProblemDetailDto;
import com.petclinic.vet.dto.ValidationMessageDto;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.List;

/**
 * Global exception handler producing RFC 7807 Problem Details responses.
 * Handles not-found errors, validation failures, and unexpected exceptions.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /** Map ResourceNotFoundException to 404 Not Found. */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ProblemDetailDto> handleNotFound(ResourceNotFoundException ex,
                                                           HttpServletRequest request) {
        ProblemDetailDto problem = new ProblemDetailDto(
            request.getRequestURL().toString(),
            "Not Found",
            HttpStatus.NOT_FOUND.value(),
            ex.getMessage(),
            Instant.now(),
            List.of()
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(problem);
    }

    /** Map bean validation failures to 400 Bad Request with field-level error details. */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ProblemDetailDto> handleValidation(MethodArgumentNotValidException ex,
                                                              HttpServletRequest request) {
        List<ValidationMessageDto> errors = ex.getBindingResult().getFieldErrors().stream()
            .map(fe -> new ValidationMessageDto(
                "[Path '/" + fe.getField() + "'] " + fe.getDefaultMessage()))
            .toList();

        ProblemDetailDto problem = new ProblemDetailDto(
            request.getRequestURL().toString(),
            "Validation Error",
            HttpStatus.BAD_REQUEST.value(),
            "Request body validation failed",
            Instant.now(),
            errors
        );
        return ResponseEntity.badRequest().body(problem);
    }

    /** Catch-all for unhandled exceptions, returning 500 Internal Server Error. */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ProblemDetailDto> handleGeneral(Exception ex,
                                                          HttpServletRequest request) {
        ProblemDetailDto problem = new ProblemDetailDto(
            request.getRequestURL().toString(),
            ex.getClass().getSimpleName(),
            HttpStatus.INTERNAL_SERVER_ERROR.value(),
            ex.getMessage(),
            Instant.now(),
            List.of()
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(problem);
    }
}
