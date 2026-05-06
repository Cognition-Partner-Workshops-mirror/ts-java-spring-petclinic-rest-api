package com.petclinic.vet.exception;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.ServletWebRequest;

import static org.assertj.core.api.Assertions.assertThat;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void handleResourceNotFound_returnsProblemDetail() {
        ResourceNotFoundException ex = new ResourceNotFoundException("Vet", 42);
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/api/vets/42");
        ServletWebRequest webRequest = new ServletWebRequest(request);

        ResponseEntity<ProblemDetail> response = handler.handleResourceNotFound(ex, webRequest);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getTitle()).isEqualTo("Not Found");
        assertThat(response.getBody().getDetail()).contains("Vet not found with id: 42");
    }

    @Test
    void handleGenericException_returnsProblemDetail() {
        RuntimeException ex = new RuntimeException("Something went wrong");
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/api/vets");
        ServletWebRequest webRequest = new ServletWebRequest(request);

        ResponseEntity<ProblemDetail> response = handler.handleGenericException(ex, webRequest);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getTitle()).isEqualTo("RuntimeException");
        assertThat(response.getBody().getDetail()).isEqualTo("Something went wrong");
    }
}
