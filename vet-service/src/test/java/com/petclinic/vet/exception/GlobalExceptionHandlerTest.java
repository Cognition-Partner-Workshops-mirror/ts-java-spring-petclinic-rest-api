package com.petclinic.vet.exception;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.mock.web.MockHttpServletRequest;

import static org.assertj.core.api.Assertions.assertThat;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();
    private final MockHttpServletRequest request = new MockHttpServletRequest("GET", "/vets/99");

    @Test
    void handleResourceNotFound_returns404() {
        ResourceNotFoundException ex = new ResourceNotFoundException("Vet", 99);

        ProblemDetail problem = handler.handleResourceNotFound(ex, request);

        assertThat(problem.getStatus()).isEqualTo(HttpStatus.NOT_FOUND.value());
        assertThat(problem.getDetail()).isEqualTo("Vet not found with id 99");
        assertThat(problem.getTitle()).isEqualTo("Not Found");
        assertThat(problem.getProperties()).containsKey("timestamp");
        assertThat(problem.getProperties()).containsKey("schemaValidationErrors");
    }

    @Test
    void handleGeneral_returns500() {
        RuntimeException ex = new RuntimeException("Something went wrong");

        ProblemDetail problem = handler.handleGeneral(ex, request);

        assertThat(problem.getStatus()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
        assertThat(problem.getDetail()).isEqualTo("Something went wrong");
        assertThat(problem.getTitle()).isEqualTo("RuntimeException");
        assertThat(problem.getProperties()).containsKey("timestamp");
    }
}
