package com.petclinic.vet.exception;

import org.junit.jupiter.api.Test;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.core.MethodParameter;

import static org.assertj.core.api.Assertions.assertThat;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    private MockHttpServletRequest mockRequest() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/api/vets");
        request.setServerName("localhost");
        request.setServerPort(8081);
        request.setScheme("http");
        return request;
    }

    @Test
    void handleResourceNotFoundException_returns404() {
        ResourceNotFoundException ex = new ResourceNotFoundException("Vet", 99);
        ResponseEntity<ProblemDetail> response = handler.handleResourceNotFoundException(ex, mockRequest());
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getTitle()).isEqualTo("ResourceNotFoundException");
        assertThat(response.getBody().getDetail()).contains("Vet not found with id: 99");
    }

    @Test
    void handleDataIntegrityViolation_returns409() {
        DataIntegrityViolationException ex = new DataIntegrityViolationException("constraint violation");
        ResponseEntity<ProblemDetail> response = handler.handleDataIntegrityViolation(ex, mockRequest());
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getDetail()).contains("Data constraint violation");
    }

    @Test
    void handleGeneralException_returns500() {
        RuntimeException ex = new RuntimeException("unexpected");
        ResponseEntity<ProblemDetail> response = handler.handleGeneralException(ex, mockRequest());
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getDetail()).contains("unexpected error");
    }

    @Test
    void handleValidationException_returns400_withErrors() throws Exception {
        BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(new Object(), "vetRequestDto");
        bindingResult.addError(new FieldError("vetRequestDto", "firstName", null, false, null, null, "must not be empty"));

        MethodParameter methodParameter = new MethodParameter(
            this.getClass().getDeclaredMethod("handleValidationException_returns400_withErrors"), -1);
        MethodArgumentNotValidException ex = new MethodArgumentNotValidException(methodParameter, bindingResult);

        ResponseEntity<ProblemDetail> response = handler.handleValidationException(ex, mockRequest());
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getProperties()).containsKey("schemaValidationErrors");
    }

    @Test
    void handleValidationException_returns400_withoutFieldErrors() throws Exception {
        BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(new Object(), "vetRequestDto");

        MethodParameter methodParameter = new MethodParameter(
            this.getClass().getDeclaredMethod("handleValidationException_returns400_withoutFieldErrors"), -1);
        MethodArgumentNotValidException ex = new MethodArgumentNotValidException(methodParameter, bindingResult);

        ResponseEntity<ProblemDetail> response = handler.handleValidationException(ex, mockRequest());
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }
}
