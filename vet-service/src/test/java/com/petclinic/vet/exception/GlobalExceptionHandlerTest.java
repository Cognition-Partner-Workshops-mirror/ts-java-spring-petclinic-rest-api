package com.petclinic.vet.exception;

import org.junit.jupiter.api.Test;
import org.springframework.http.ProblemDetail;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.core.MethodParameter;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();
    private final MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/vets/1");

    @Test
    void handleNotFound_returnsProblemDetail() {
        ResourceNotFoundException ex = new ResourceNotFoundException("Vet", 1);

        ProblemDetail result = handler.handleNotFound(ex, request);

        assertThat(result.getStatus()).isEqualTo(404);
        assertThat(result.getTitle()).isEqualTo("Not Found");
        assertThat(result.getDetail()).contains("Vet");
        assertThat(result.getProperties()).containsKey("timestamp");
        assertThat(result.getProperties()).containsKey("schemaValidationErrors");
    }

    @Test
    void handleGeneral_returnsProblemDetail() {
        RuntimeException ex = new RuntimeException("unexpected error");

        ProblemDetail result = handler.handleGeneral(ex, request);

        assertThat(result.getStatus()).isEqualTo(500);
        assertThat(result.getTitle()).isEqualTo("Internal Server Error");
        assertThat(result.getDetail()).isEqualTo("unexpected error");
    }

    @Test
    void handleValidation_returnsProblemDetailWithErrors() throws Exception {
        BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(new Object(), "vetRequestDto");
        bindingResult.addError(new FieldError("vetRequestDto", "firstName", "must not be blank"));

        MethodParameter methodParameter = new MethodParameter(
            this.getClass().getDeclaredMethod("dummyMethod", String.class), 0);
        MethodArgumentNotValidException ex = new MethodArgumentNotValidException(methodParameter, bindingResult);

        ProblemDetail result = handler.handleValidation(ex, request);

        assertThat(result.getStatus()).isEqualTo(400);
        assertThat(result.getTitle()).isEqualTo("Bad Request");
        assertThat(result.getProperties()).containsKey("schemaValidationErrors");
        @SuppressWarnings("unchecked")
        List<?> errors = (List<?>) result.getProperties().get("schemaValidationErrors");
        assertThat(errors).hasSize(1);
    }

    @SuppressWarnings("unused")
    private void dummyMethod(String param) {
    }
}
