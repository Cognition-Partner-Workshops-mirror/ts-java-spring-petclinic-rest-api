package com.petclinic.vet.exception;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.core.MethodParameter;

import static org.assertj.core.api.Assertions.assertThat;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();
    private final WebRequest webRequest = new ServletWebRequest(new MockHttpServletRequest());

    @Test
    void handleResourceNotFound_returns404() {
        ResourceNotFoundException ex = new ResourceNotFoundException("Vet", 99);
        ProblemDetail result = handler.handleResourceNotFound(ex, webRequest);

        assertThat(result.getStatus()).isEqualTo(HttpStatus.NOT_FOUND.value());
        assertThat(result.getTitle()).isEqualTo("Resource Not Found");
        assertThat(result.getDetail()).contains("99");
        assertThat(result.getProperties()).containsKey("timestamp");
    }

    @Test
    void handleBadRequest_returns400() {
        BadRequestException ex = new BadRequestException("Invalid input");
        ProblemDetail result = handler.handleBadRequest(ex, webRequest);

        assertThat(result.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(result.getTitle()).isEqualTo("Bad Request");
        assertThat(result.getDetail()).isEqualTo("Invalid input");
        assertThat(result.getProperties()).containsKey("timestamp");
    }

    @Test
    void handleGenericException_returns500() {
        Exception ex = new RuntimeException("Something went wrong");
        ProblemDetail result = handler.handleGenericException(ex, webRequest);

        assertThat(result.getStatus()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
        assertThat(result.getTitle()).isEqualTo("Internal Server Error");
        assertThat(result.getDetail()).isEqualTo("An unexpected error occurred");
        assertThat(result.getProperties()).containsKey("timestamp");
    }

    @Test
    void handleValidationErrors_returns400WithFieldErrors() throws Exception {
        BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(new Object(), "target");
        bindingResult.addError(new FieldError("target", "firstName", "must not be blank"));
        bindingResult.addError(new FieldError("target", "lastName", "must not be blank"));

        MethodParameter methodParameter = new MethodParameter(
            this.getClass().getDeclaredMethod("handleValidationErrors_returns400WithFieldErrors"), -1);

        MethodArgumentNotValidException ex = new MethodArgumentNotValidException(methodParameter, bindingResult);

        ProblemDetail result = handler.handleValidationErrors(ex);

        assertThat(result.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(result.getTitle()).isEqualTo("Validation Error");
        assertThat(result.getDetail()).isEqualTo("Validation failed");
        assertThat(result.getProperties()).containsKey("schemaValidationErrors");
        assertThat(result.getProperties()).containsKey("timestamp");
    }
}
