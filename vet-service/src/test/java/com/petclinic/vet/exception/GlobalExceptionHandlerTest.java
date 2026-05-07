package com.petclinic.vet.exception;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    private WebRequest webRequest() {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/test");
        return new ServletWebRequest(request);
    }

    @Test
    void handleNotFound_returnsProblemDetail() {
        ResourceNotFoundException ex = new ResourceNotFoundException("Vet", 42);

        ResponseEntity<ProblemDetail> response = handler.handleNotFound(ex, webRequest());

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        ProblemDetail body = response.getBody();
        assertThat(body).isNotNull();
        assertThat(body.getTitle()).isEqualTo("Vet not found");
        assertThat(body.getDetail()).contains("42");
        assertThat(body.getStatus()).isEqualTo(404);
    }

    @Test
    void handleGeneral_returnsInternalServerError() {
        Exception ex = new RuntimeException("unexpected error");

        ResponseEntity<ProblemDetail> response = handler.handleGeneral(ex, webRequest());

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        ProblemDetail body = response.getBody();
        assertThat(body).isNotNull();
        assertThat(body.getTitle()).isEqualTo("Internal Server Error");
        assertThat(body.getDetail()).isEqualTo("unexpected error");
    }

    @Test
    void resourceNotFoundException_fieldsAccessible() {
        ResourceNotFoundException ex = new ResourceNotFoundException("Specialty", 5);
        assertThat(ex.getResourceName()).isEqualTo("Specialty");
        assertThat(ex.getResourceId()).isEqualTo(5);
        assertThat(ex.getMessage()).isEqualTo("Specialty not found with id: 5");
    }

    @Test
    void handleNotFound_nonServletWebRequest_usesAboutBlank() {
        ResourceNotFoundException ex = new ResourceNotFoundException("Vet", 1);
        WebRequest nonServlet = mock(NativeWebRequest.class);
        ResponseEntity<ProblemDetail> response = handler.handleNotFound(ex, nonServlet);
        assertThat(response.getBody().getType()).hasToString("about:blank");
    }
}
