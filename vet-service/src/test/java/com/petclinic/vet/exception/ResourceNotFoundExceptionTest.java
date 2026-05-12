package com.petclinic.vet.exception;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for ResourceNotFoundException to verify message formatting.
 */
class ResourceNotFoundExceptionTest {

    @Test
    void constructor_setsFieldsCorrectly() {
        ResourceNotFoundException ex = new ResourceNotFoundException("Vet", 42);

        assertThat(ex.getResourceName()).isEqualTo("Vet");
        assertThat(ex.getResourceId()).isEqualTo(42);
        assertThat(ex.getMessage()).isEqualTo("Vet not found with id: 42");
    }
}
