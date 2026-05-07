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

    private MockHttpServletRequest createRequest() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/api/vets/1");
        request.setServerName("localhost");
        request.setServerPort(8081);
        request.setScheme("http");
        return request;
    }

    @Test
    void handleResourceNotFoundException_shouldReturn404() {
        ResourceNotFoundException ex = new ResourceNotFoundException("Vet", 99);
        MockHttpServletRequest request = createRequest();

        ResponseEntity<ProblemDetail> response = handler.handleResourceNotFoundException(ex, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getTitle()).isEqualTo("ResourceNotFoundException");
        assertThat(response.getBody().getDetail()).contains("Vet not found with id: 99");
    }

    @Test
    void handleDataIntegrityViolationException_shouldReturn409() {
        DataIntegrityViolationException ex = new DataIntegrityViolationException("constraint violation");
        MockHttpServletRequest request = createRequest();

        ResponseEntity<ProblemDetail> response = handler.handleDataIntegrityViolationException(ex, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getTitle()).isEqualTo("DataIntegrityViolationException");
    }

    @Test
    void handleGeneralException_shouldReturn500() {
        RuntimeException ex = new RuntimeException("unexpected error");
        MockHttpServletRequest request = createRequest();

        ResponseEntity<ProblemDetail> response = handler.handleGeneralException(ex, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getDetail()).contains("unexpected error");
    }

    @Test
    void handleMethodArgumentNotValidException_shouldReturn400() throws Exception {
        BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(new Object(), "vetRequestDto");
        bindingResult.addError(new FieldError("vetRequestDto", "firstName", null, false,
            null, null, "must not be empty"));

        MethodParameter methodParameter = new MethodParameter(
            this.getClass().getDeclaredMethod("handleMethodArgumentNotValidException_shouldReturn400"), -1);
        MethodArgumentNotValidException ex = new MethodArgumentNotValidException(methodParameter, bindingResult);

        MockHttpServletRequest request = createRequest();

        ResponseEntity<ProblemDetail> response = handler.handleMethodArgumentNotValidException(ex, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getProperties()).containsKey("schemaValidationErrors");
    }

    @Test
    void handleMethodArgumentNotValidException_noErrors_shouldReturn400() throws Exception {
        BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(new Object(), "vetRequestDto");

        MethodParameter methodParameter = new MethodParameter(
            this.getClass().getDeclaredMethod("handleMethodArgumentNotValidException_noErrors_shouldReturn400"), -1);
        MethodArgumentNotValidException ex = new MethodArgumentNotValidException(methodParameter, bindingResult);

        MockHttpServletRequest request = createRequest();

        ResponseEntity<ProblemDetail> response = handler.handleMethodArgumentNotValidException(ex, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }
}
