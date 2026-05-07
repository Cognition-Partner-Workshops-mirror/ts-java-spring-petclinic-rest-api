package com.petclinic.vet.exception;

import java.net.URI;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.context.request.ServletWebRequest;

import static org.assertj.core.api.Assertions.assertThat;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void handleNotFound_returnsProblemDetail() {
        ResourceNotFoundException ex = new ResourceNotFoundException("Vet", 1);
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/vets/1");
        ServletWebRequest webRequest = new ServletWebRequest(request);

        ResponseEntity<ProblemDetail> response = handler.handleNotFound(ex, webRequest);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getTitle()).isEqualTo("Not Found");
        assertThat(response.getBody().getDetail()).contains("Vet not found with id: 1");
        assertThat(response.getBody().getType()).isEqualTo(URI.create("/vets/1"));
    }

    @Test
    void handleGeneral_returnsProblemDetail() {
        Exception ex = new RuntimeException("Unexpected error");
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/vets");
        ServletWebRequest webRequest = new ServletWebRequest(request);

        ResponseEntity<ProblemDetail> response = handler.handleGeneral(ex, webRequest);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getTitle()).isEqualTo("Internal Server Error");
        assertThat(response.getBody().getDetail()).isEqualTo("Unexpected error");
    }

    @Test
    void resourceNotFoundException_message() {
        ResourceNotFoundException ex = new ResourceNotFoundException("Specialty", 42);
        assertThat(ex.getMessage()).isEqualTo("Specialty not found with id: 42");
    }
}
