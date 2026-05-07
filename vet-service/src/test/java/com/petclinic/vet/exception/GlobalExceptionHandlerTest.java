package com.petclinic.vet.exception;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;

import static org.assertj.core.api.Assertions.assertThat;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    private WebRequest webRequest() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/api/vets/1");
        return new ServletWebRequest(request);
    }

    @Test
    void handleNotFound_returnsProblemDetail() {
        ResourceNotFoundException ex = new ResourceNotFoundException("Vet", 1);
        ResponseEntity<ProblemDetail> response = handler.handleNotFound(ex, webRequest());

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getTitle()).isEqualTo("Not Found");
        assertThat(response.getBody().getDetail()).contains("Vet not found with id: 1");
        assertThat(response.getBody().getProperties()).containsKey("timestamp");
        assertThat(response.getBody().getProperties()).containsKey("schemaValidationErrors");
    }

    @Test
    void handleGeneric_returnsProblemDetail() {
        Exception ex = new RuntimeException("Something went wrong");
        ResponseEntity<ProblemDetail> response = handler.handleGeneric(ex, webRequest());

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getTitle()).isEqualTo("Internal Server Error");
        assertThat(response.getBody().getDetail()).isEqualTo("Something went wrong");
    }
}
