package com.petclinic.vet.controller;

import com.petclinic.vet.dto.ProblemDetailDto;
import com.petclinic.vet.exception.GlobalExceptionHandler;
import com.petclinic.vet.exception.ResourceNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;

import static org.assertj.core.api.Assertions.assertThat;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();
    private final MockHttpServletRequest request = new MockHttpServletRequest("GET", "/petclinic/api/vets/1");

    @Test
    void handleNotFound_returnsCorrectProblemDetail() {
        ResourceNotFoundException ex = new ResourceNotFoundException("Vet", 1);

        ResponseEntity<ProblemDetailDto> response = handler.handleNotFound(ex, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().status()).isEqualTo(404);
        assertThat(response.getBody().title()).isEqualTo("ResourceNotFoundException");
        assertThat(response.getBody().detail()).contains("Vet");
        assertThat(response.getBody().timestamp()).isNotNull();
        assertThat(response.getBody().schemaValidationErrors()).isEmpty();
    }

    @Test
    void handleBadRequest_returnsCorrectProblemDetail() {
        IllegalArgumentException ex = new IllegalArgumentException("Bad input");

        ResponseEntity<ProblemDetailDto> response = handler.handleBadRequest(ex, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().status()).isEqualTo(400);
        assertThat(response.getBody().detail()).isEqualTo("Bad input");
    }

    @Test
    void handleGeneric_returnsInternalServerError() {
        RuntimeException ex = new RuntimeException("Unexpected error");

        ResponseEntity<ProblemDetailDto> response = handler.handleGeneric(ex, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().status()).isEqualTo(500);
    }
}
