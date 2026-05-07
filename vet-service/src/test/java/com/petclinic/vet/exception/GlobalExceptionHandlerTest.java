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

    private ServletWebRequest webRequest() {
        return new ServletWebRequest(new MockHttpServletRequest("GET", "/api/vets/999"));
    }

    @Test
    void handleNotFound_returnsProblemDetail() {
        ResourceNotFoundException ex = new ResourceNotFoundException("Vet", 999);

        ResponseEntity<ProblemDetail> response = handler.handleNotFound(ex, webRequest());

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getDetail()).contains("Vet not found");
    }

    @Test
    void handleGeneral_returnsProblemDetail() {
        RuntimeException ex = new RuntimeException("something went wrong");

        ResponseEntity<ProblemDetail> response = handler.handleGeneral(ex, webRequest());

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getDetail()).contains("unexpected error");
    }
}
