package com.petclinic.vet.dto;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for ValidationMessageDto covering constructors, getters, and additional properties.
 */
class ValidationMessageDtoTest {

    @Test
    void defaultConstructor_andSetter() {
        ValidationMessageDto dto = new ValidationMessageDto();
        dto.setMessage("Field 'name' is required");
        assertThat(dto.getMessage()).isEqualTo("Field 'name' is required");
    }

    @Test
    void parameterizedConstructor() {
        ValidationMessageDto dto = new ValidationMessageDto("Validation error");
        assertThat(dto.getMessage()).isEqualTo("Validation error");
    }

    @Test
    void additionalProperties() {
        ValidationMessageDto dto = new ValidationMessageDto("error");
        dto.putAdditionalProperty("field", "firstName");
        dto.putAdditionalProperty("rejectedValue", "null");

        assertThat(dto.getAdditionalProperties()).containsEntry("field", "firstName");
        assertThat(dto.getAdditionalProperties()).containsEntry("rejectedValue", "null");
    }
}
