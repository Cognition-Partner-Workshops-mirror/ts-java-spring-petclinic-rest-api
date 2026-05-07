package com.petclinic.vet.exception;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ExceptionHandlerTest {

    @Test
    void resourceNotFoundException_storesFields() {
        ResourceNotFoundException ex = new ResourceNotFoundException("Vet", 42);

        assertThat(ex.getResourceName()).isEqualTo("Vet");
        assertThat(ex.getResourceId()).isEqualTo(42);
        assertThat(ex.getMessage()).isEqualTo("Vet not found with id 42");
    }

    @Test
    void validationError_storesMessage() {
        GlobalExceptionHandler.ValidationError error =
            new GlobalExceptionHandler.ValidationError("test message");

        assertThat(error.message()).isEqualTo("test message");
    }
}
