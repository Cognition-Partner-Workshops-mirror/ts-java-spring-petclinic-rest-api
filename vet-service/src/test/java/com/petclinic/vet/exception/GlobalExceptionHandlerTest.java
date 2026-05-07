package com.petclinic.vet.exception;

import com.petclinic.vet.dto.ProblemDetailDto;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.core.MethodParameter;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();
    private final MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/test");

    @Test
    void handleNotFound_returnsProblemDetail() {
        ResourceNotFoundException ex = new ResourceNotFoundException("Vet", 42);

        ResponseEntity<ProblemDetailDto> response = handler.handleNotFound(ex, request);

        assertThat(response.getStatusCode().value()).isEqualTo(404);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().title()).isEqualTo("Not Found");
        assertThat(response.getBody().detail()).contains("Vet");
        assertThat(response.getBody().detail()).contains("42");
        assertThat(response.getBody().timestamp()).isNotNull();
    }

    @Test
    void handleValidation_returnsProblemDetailWithErrors() throws Exception {
        BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(new Object(), "target");
        bindingResult.addError(new FieldError("target", "name", "must not be blank"));

        MethodParameter param = new MethodParameter(
            this.getClass().getDeclaredMethod("handleValidation_returnsProblemDetailWithErrors"), -1);
        MethodArgumentNotValidException ex = new MethodArgumentNotValidException(param, bindingResult);

        ResponseEntity<ProblemDetailDto> response = handler.handleValidation(ex, request);

        assertThat(response.getStatusCode().value()).isEqualTo(400);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().title()).isEqualTo("Validation Error");
        assertThat(response.getBody().schemaValidationErrors()).hasSize(1);
        assertThat(response.getBody().schemaValidationErrors().get(0).message()).contains("name");
    }

    @Test
    void handleGeneral_returnsProblemDetail() {
        RuntimeException ex = new RuntimeException("something went wrong");

        ResponseEntity<ProblemDetailDto> response = handler.handleGeneral(ex, request);

        assertThat(response.getStatusCode().value()).isEqualTo(500);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().title()).isEqualTo("RuntimeException");
        assertThat(response.getBody().detail()).isEqualTo("something went wrong");
    }

    @Test
    void resourceNotFoundException_getters() {
        ResourceNotFoundException ex = new ResourceNotFoundException("Specialty", 10);
        assertThat(ex.getResourceName()).isEqualTo("Specialty");
        assertThat(ex.getResourceId()).isEqualTo(10);
        assertThat(ex.getMessage()).isEqualTo("Specialty not found with id: 10");
    }
}
