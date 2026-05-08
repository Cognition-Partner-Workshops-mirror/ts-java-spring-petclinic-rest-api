package com.petclinic.vet.exception;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for ResourceNotFoundException to cover all fields.
 */
class ResourceNotFoundExceptionTest {

    @Test
    void constructor_setsResourceNameAndId() {
        ResourceNotFoundException ex = new ResourceNotFoundException("Vet", 5);

        assertThat(ex.getResourceName()).isEqualTo("Vet");
        assertThat(ex.getResourceId()).isEqualTo(5);
        assertThat(ex.getMessage()).isEqualTo("Vet not found with id: 5");
    }

    @Test
    void constructor_setsSpecialtyNameAndId() {
        ResourceNotFoundException ex = new ResourceNotFoundException("Specialty", 10);

        assertThat(ex.getResourceName()).isEqualTo("Specialty");
        assertThat(ex.getResourceId()).isEqualTo(10);
    }
}
