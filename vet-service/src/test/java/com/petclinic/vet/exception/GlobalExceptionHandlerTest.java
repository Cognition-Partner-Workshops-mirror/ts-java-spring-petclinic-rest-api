package com.petclinic.vet.exception;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class GlobalExceptionHandlerTest {

    @Test
    void resourceNotFoundException_hasCorrectMessage() {
        ResourceNotFoundException ex = new ResourceNotFoundException("Vet", 42);
        assertThat(ex.getMessage()).isEqualTo("Vet not found with id: 42");
    }

    @Test
    void resourceNotFoundException_differentResource() {
        ResourceNotFoundException ex = new ResourceNotFoundException("Specialty", 1);
        assertThat(ex.getMessage()).isEqualTo("Specialty not found with id: 1");
    }
}
