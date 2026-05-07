package com.petclinic.vet.exception;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ResourceNotFoundExceptionTest {

    @Test
    void constructorSetsFields() {
        ResourceNotFoundException ex = new ResourceNotFoundException("Vet", 42);

        assertThat(ex.getMessage()).isEqualTo("Vet not found with id 42");
        assertThat(ex.getResourceName()).isEqualTo("Vet");
        assertThat(ex.getResourceId()).isEqualTo(42);
    }
}
