package com.petclinic.vet.exception;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ResourceNotFoundExceptionTest {

    @Test
    void constructor_setsMessage() {
        ResourceNotFoundException ex = new ResourceNotFoundException("Vet", 42);
        assertThat(ex.getMessage()).isEqualTo("Vet not found with id: 42");
    }

    @Test
    void constructor_withDifferentResource() {
        ResourceNotFoundException ex = new ResourceNotFoundException("Specialty", 1);
        assertThat(ex.getMessage()).isEqualTo("Specialty not found with id: 1");
    }
}
