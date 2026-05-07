package com.petclinic.vet.exception;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;

import static org.assertj.core.api.Assertions.assertThat;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();
    private final MockHttpServletRequest request = new MockHttpServletRequest("GET", "/vets/1");

    @Test
    void handleNotFound_returns404() {
        request.setServerName("localhost");
        request.setServerPort(8081);
        request.setScheme("http");

        ResourceNotFoundException ex = new ResourceNotFoundException("Vet", 1);
        ResponseEntity<ProblemDetail> response = handler.handleNotFound(ex, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getTitle()).isEqualTo("Not Found");
        assertThat(response.getBody().getDetail()).contains("Vet not found with id: 1");
    }

    @Test
    void handleTypeMismatch_returns400() {
        request.setServerName("localhost");
        request.setServerPort(8081);
        request.setScheme("http");

        org.springframework.web.method.annotation.MethodArgumentTypeMismatchException ex =
            new org.springframework.web.method.annotation.MethodArgumentTypeMismatchException(
                "abc", Integer.class, "vetId", null, new NumberFormatException("abc"));

        ResponseEntity<ProblemDetail> response = handler.handleTypeMismatch(ex, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getTitle()).isEqualTo("Bad Request");
    }

    @Test
    void handleGeneral_returns500() {
        request.setServerName("localhost");
        request.setServerPort(8081);
        request.setScheme("http");

        Exception ex = new RuntimeException("unexpected error");
        ResponseEntity<ProblemDetail> response = handler.handleGeneral(ex, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getTitle()).isEqualTo("Internal Server Error");
    }

    @Test
    void resourceNotFoundException_message() {
        ResourceNotFoundException ex = new ResourceNotFoundException("Specialty", 42);
        assertThat(ex.getMessage()).isEqualTo("Specialty not found with id: 42");
    }

    @Test
    void validationMessage_record() {
        GlobalExceptionHandler.ValidationMessage vm = new GlobalExceptionHandler.ValidationMessage("test message");
        assertThat(vm.message()).isEqualTo("test message");
    }
}
