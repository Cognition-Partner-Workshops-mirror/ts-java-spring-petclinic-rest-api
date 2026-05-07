package com.petclinic.vet.exception;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ExceptionTest {

    @Test
    void resourceNotFoundException_properties() {
        ResourceNotFoundException ex = new ResourceNotFoundException("Vet", 42);
        assertThat(ex.getResourceName()).isEqualTo("Vet");
        assertThat(ex.getResourceId()).isEqualTo(42);
        assertThat(ex.getMessage()).contains("Vet").contains("42");
    }

    @Test
    void duplicateResourceException_properties() {
        DuplicateResourceException ex = new DuplicateResourceException("Specialty", "name", "radiology");
        assertThat(ex.getResourceName()).isEqualTo("Specialty");
        assertThat(ex.getFieldName()).isEqualTo("name");
        assertThat(ex.getFieldValue()).isEqualTo("radiology");
        assertThat(ex.getMessage()).contains("Specialty").contains("radiology");
    }
}
