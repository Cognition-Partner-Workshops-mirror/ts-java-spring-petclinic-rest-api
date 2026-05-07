package com.petclinic.vet.dto;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ValidationMessageDtoTest {

    @Test
    void defaultConstructor() {
        ValidationMessageDto dto = new ValidationMessageDto();
        assertThat(dto.getMessage()).isNull();
        assertThat(dto.getAdditionalProperties()).isEmpty();
    }

    @Test
    void parameterizedConstructor() {
        ValidationMessageDto dto = new ValidationMessageDto("error message");
        assertThat(dto.getMessage()).isEqualTo("error message");
    }

    @Test
    void setMessage() {
        ValidationMessageDto dto = new ValidationMessageDto();
        dto.setMessage("new message");
        assertThat(dto.getMessage()).isEqualTo("new message");
    }

    @Test
    void putAdditionalProperty_returnsThis() {
        ValidationMessageDto dto = new ValidationMessageDto("msg")
            .putAdditionalProperty("field", "firstName")
            .putAdditionalProperty("rejectedValue", "");
        assertThat(dto.getAdditionalProperties()).hasSize(2);
        assertThat(dto.getAdditionalProperties().get("field")).isEqualTo("firstName");
    }
}
