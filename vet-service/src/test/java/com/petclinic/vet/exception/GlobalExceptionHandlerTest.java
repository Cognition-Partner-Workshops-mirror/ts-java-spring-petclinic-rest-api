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

import java.lang.reflect.Method;

import static org.assertj.core.api.Assertions.assertThat;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    private MockHttpServletRequest createRequest() {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/vets/1");
        request.setServerName("localhost");
        request.setServerPort(8081);
        request.setScheme("http");
        return request;
    }

    @Test
    void handleNotFound_returns404WithProblemDetail() {
        ResourceNotFoundException ex = new ResourceNotFoundException("Vet", 1);
        MockHttpServletRequest request = createRequest();

        ResponseEntity<ProblemDetail> response = handler.handleNotFound(ex, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getTitle()).isEqualTo("ResourceNotFoundException");
        assertThat(response.getBody().getDetail()).contains("Vet");
    }

    @Test
    void handleDataIntegrity_returns409WithProblemDetail() {
        DataIntegrityViolationException ex = new DataIntegrityViolationException("constraint violation");
        MockHttpServletRequest request = createRequest();

        ResponseEntity<ProblemDetail> response = handler.handleDataIntegrity(ex, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getTitle()).isEqualTo("DataIntegrityViolationException");
    }

    @Test
    void handleGeneral_returns500WithProblemDetail() {
        RuntimeException ex = new RuntimeException("unexpected");
        MockHttpServletRequest request = createRequest();

        ResponseEntity<ProblemDetail> response = handler.handleGeneral(ex, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getDetail()).contains("unexpected error");
    }

    @Test
    void handleValidation_returns400WithValidationErrors() throws Exception {
        BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(new Object(), "vetRequestDto");
        bindingResult.addError(new FieldError("vetRequestDto", "firstName", null, false, null, null, "must not be empty"));

        Method method = GlobalExceptionHandlerTest.class.getDeclaredMethod("dummyMethod", String.class);
        MethodParameter methodParameter = new MethodParameter(method, 0);
        MethodArgumentNotValidException ex = new MethodArgumentNotValidException(methodParameter, bindingResult);

        MockHttpServletRequest request = createRequest();
        ResponseEntity<ProblemDetail> response = handler.handleValidation(ex, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getProperties()).containsKey("schemaValidationErrors");
    }

    @SuppressWarnings("unused")
    private void dummyMethod(String param) {
    }
}
