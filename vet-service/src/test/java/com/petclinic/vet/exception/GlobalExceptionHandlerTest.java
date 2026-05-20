package com.petclinic.vet.exception;

import org.junit.jupiter.api.Test;
import org.springframework.core.MethodParameter;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Unit tests for GlobalExceptionHandler to verify RFC 7807 Problem Details responses.
 */
class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();
    private final MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/vets/1");

    @Test
    void handleNotFound_shouldReturn404WithProblemDetail() {
        ResourceNotFoundException ex = new ResourceNotFoundException("Vet not found with id: 1");
        request.setServerName("localhost");
        request.setServerPort(8080);

        ResponseEntity<ProblemDetail> response = handler.handleNotFound(ex, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getTitle()).isEqualTo("ResourceNotFoundException");
        assertThat(response.getBody().getDetail()).isEqualTo("Vet not found with id: 1");
        assertThat(response.getBody().getStatus()).isEqualTo(404);
        assertThat(response.getBody().getProperties()).containsKey("timestamp");
        assertThat(response.getBody().getProperties()).containsKey("schemaValidationErrors");
    }

    @Test
    void handleValidation_shouldReturn400WithValidationErrors() throws Exception {
        BindingResult bindingResult = mock(BindingResult.class);
        FieldError fieldError = new FieldError("vetRequestDto", "firstName", "must not be empty");
        when(bindingResult.getFieldErrors()).thenReturn(List.of(fieldError));

        // Use a real MethodParameter to avoid NPE in getMessage()
        MethodParameter methodParam = new MethodParameter(
            GlobalExceptionHandlerTest.class.getDeclaredMethod("handleValidation_shouldReturn400WithValidationErrors"), -1);
        MethodArgumentNotValidException ex = new MethodArgumentNotValidException(methodParam, bindingResult);
        request.setServerName("localhost");
        request.setServerPort(8080);

        ResponseEntity<ProblemDetail> response = handler.handleValidation(ex, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getDetail()).contains("invalid or missing parameters");
    }

    @Test
    void handleDataIntegrity_shouldReturn400() {
        DataIntegrityViolationException ex = new DataIntegrityViolationException("Constraint violation");
        request.setServerName("localhost");
        request.setServerPort(8080);

        ResponseEntity<ProblemDetail> response = handler.handleDataIntegrity(ex, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getTitle()).isEqualTo("DataIntegrityViolationException");
        assertThat(response.getBody().getDetail()).contains("Data constraint violation");
    }

    @Test
    void handleGeneral_shouldReturn500() {
        RuntimeException ex = new RuntimeException("Something went wrong");
        request.setServerName("localhost");
        request.setServerPort(8080);

        ResponseEntity<ProblemDetail> response = handler.handleGeneral(ex, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getTitle()).isEqualTo("RuntimeException");
        assertThat(response.getBody().getDetail()).contains("unexpected error");
    }
}
