package com.petclinic.vet.exception;

import org.junit.jupiter.api.Test;
import org.springframework.http.ProblemDetail;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.core.MethodParameter;

import java.util.List;
import java.util.Map;

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
        assertThat(result.getDetail()).contains("Vet not found with id 1");
        assertThat(result.getProperties()).containsKey("timestamp");
        assertThat(result.getProperties()).containsKey("schemaValidationErrors");
    }

    @Test
    void handleGeneral_returnsProblemDetail() {
        RuntimeException ex = new RuntimeException("Something went wrong");

        ProblemDetail result = handler.handleGeneral(ex, request);

        assertThat(result.getStatus()).isEqualTo(500);
        assertThat(result.getTitle()).isEqualTo("Internal Server Error");
        assertThat(result.getDetail()).isEqualTo("Something went wrong");
    }

    @Test
    void handleValidation_returnsProblemDetailWithErrors() throws Exception {
        BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(new Object(), "vetRequest");
        bindingResult.addError(new FieldError("vetRequest", "firstName", "must not be blank"));

        MethodParameter methodParameter = new MethodParameter(
            this.getClass().getDeclaredMethod("handleValidation_returnsProblemDetailWithErrors"), -1);
        MethodArgumentNotValidException ex = new MethodArgumentNotValidException(methodParameter, bindingResult);

        ProblemDetail result = handler.handleValidation(ex, request);

        assertThat(result.getStatus()).isEqualTo(400);
        assertThat(result.getTitle()).isEqualTo("Bad Request");
        @SuppressWarnings("unchecked")
        List<Map<String, String>> errors = (List<Map<String, String>>) result.getProperties().get("schemaValidationErrors");
        assertThat(errors).hasSize(1);
        assertThat(errors.get(0).get("message")).contains("firstName");
    }
}
