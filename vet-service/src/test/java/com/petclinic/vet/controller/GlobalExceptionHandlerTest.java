package com.petclinic.vet.controller;

import com.petclinic.vet.exception.GlobalExceptionHandler;
import com.petclinic.vet.exception.ResourceNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.http.ProblemDetail;
import org.springframework.mock.web.MockHttpServletRequest;

import static org.assertj.core.api.Assertions.assertThat;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();
    private final MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/vets/999");

    @Test
    void handleResourceNotFound_returns404ProblemDetail() {
        ResourceNotFoundException ex = new ResourceNotFoundException("Vet", 999);

        ProblemDetail result = handler.handleResourceNotFound(ex, request);

        assertThat(result.getStatus()).isEqualTo(404);
        assertThat(result.getTitle()).isEqualTo("Not Found");
        assertThat(result.getDetail()).contains("Vet");
        assertThat(result.getProperties()).containsKey("timestamp");
        assertThat(result.getProperties()).containsKey("schemaValidationErrors");
    }

    @Test
    void handleIllegalArgument_returns400ProblemDetail() {
        IllegalArgumentException ex = new IllegalArgumentException("Invalid input");

        ProblemDetail result = handler.handleIllegalArgument(ex, request);

        assertThat(result.getStatus()).isEqualTo(400);
        assertThat(result.getTitle()).isEqualTo("Bad Request");
        assertThat(result.getDetail()).isEqualTo("Invalid input");
    }

    @Test
    void handleGeneral_returns500ProblemDetail() {
        RuntimeException ex = new RuntimeException("Unexpected error");

        ProblemDetail result = handler.handleGeneral(ex, request);

        assertThat(result.getStatus()).isEqualTo(500);
        assertThat(result.getTitle()).isEqualTo("Internal Server Error");
        assertThat(result.getDetail()).isEqualTo("Unexpected error");
    }

    @Test
    void resourceNotFoundException_exposesFields() {
        ResourceNotFoundException ex = new ResourceNotFoundException("Vet", 42);

        assertThat(ex.getResourceName()).isEqualTo("Vet");
        assertThat(ex.getResourceId()).isEqualTo(42);
        assertThat(ex.getMessage()).contains("Vet").contains("42");
    }
}
