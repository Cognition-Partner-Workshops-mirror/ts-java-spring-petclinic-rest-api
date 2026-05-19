package com.petclinic.vet.exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for ResourceNotFoundException.
 */
class ResourceNotFoundExceptionTest {

    @Test
    @DisplayName("Exception contains resource name and ID")
    void exceptionContainsResourceDetails() {
        ResourceNotFoundException ex = new ResourceNotFoundException("Vet", 42);

        assertThat(ex.getResourceName()).isEqualTo("Vet");
        assertThat(ex.getResourceId()).isEqualTo(42);
        assertThat(ex.getMessage()).isEqualTo("Vet not found with id: 42");
    }
}
