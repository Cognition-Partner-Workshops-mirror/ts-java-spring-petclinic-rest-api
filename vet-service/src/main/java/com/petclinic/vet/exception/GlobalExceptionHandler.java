package com.petclinic.vet.exception;

import com.petclinic.vet.dto.ProblemDetailDto;
import com.petclinic.vet.dto.ValidationMessageDto;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final MediaType PROBLEM_JSON = MediaType.valueOf("application/problem+json");

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ProblemDetailDto> handleResourceNotFound(ResourceNotFoundException ex,
                                                                    HttpServletRequest request) {
        ProblemDetailDto problem = new ProblemDetailDto(
            request.getRequestURL().toString(),
            "Not Found",
            HttpStatus.NOT_FOUND.value(),
            ex.getMessage(),
            OffsetDateTime.now(),
            Collections.emptyList()
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .contentType(PROBLEM_JSON)
            .body(problem);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ProblemDetailDto> handleValidation(MethodArgumentNotValidException ex,
                                                              HttpServletRequest request) {
        List<ValidationMessageDto> errors = ex.getBindingResult().getFieldErrors().stream()
            .map(fe -> new ValidationMessageDto(
                "[Path '/" + fe.getField() + "'] " + fe.getDefaultMessage()))
            .toList();
        ProblemDetailDto problem = new ProblemDetailDto(
            request.getRequestURL().toString(),
            "Bad Request",
            HttpStatus.BAD_REQUEST.value(),
            "Validation failed",
            OffsetDateTime.now(),
            errors
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .contentType(PROBLEM_JSON)
            .body(problem);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ProblemDetailDto> handleConstraintViolation(ConstraintViolationException ex,
                                                                       HttpServletRequest request) {
        List<ValidationMessageDto> errors = ex.getConstraintViolations().stream()
            .map(cv -> new ValidationMessageDto(cv.getPropertyPath() + ": " + cv.getMessage()))
            .toList();
        ProblemDetailDto problem = new ProblemDetailDto(
            request.getRequestURL().toString(),
            "Bad Request",
            HttpStatus.BAD_REQUEST.value(),
            "Constraint violation",
            OffsetDateTime.now(),
            errors
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .contentType(PROBLEM_JSON)
            .body(problem);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ProblemDetailDto> handleGenericException(Exception ex,
                                                                    HttpServletRequest request) {
        ProblemDetailDto problem = new ProblemDetailDto(
            request.getRequestURL().toString(),
            "Internal Server Error",
            HttpStatus.INTERNAL_SERVER_ERROR.value(),
            ex.getMessage(),
            OffsetDateTime.now(),
            Collections.emptyList()
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .contentType(PROBLEM_JSON)
            .body(problem);
    }
}
