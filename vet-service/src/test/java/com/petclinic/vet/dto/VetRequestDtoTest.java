package com.petclinic.vet.dto;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class VetRequestDtoTest {

    @Test
    void defaultConstructor_shouldCreateEmptyDto() {
        VetRequestDto dto = new VetRequestDto();
        assertThat(dto.getFirstName()).isNull();
        assertThat(dto.getLastName()).isNull();
        assertThat(dto.getSpecialties()).isEmpty();
    }

    @Test
    void parameterizedConstructor_shouldSetAllFields() {
        List<SpecialtyResponseDto> specialties = List.of(new SpecialtyResponseDto(1, "radiology"));
        VetRequestDto dto = new VetRequestDto("James", "Carter", specialties);
        assertThat(dto.getFirstName()).isEqualTo("James");
        assertThat(dto.getLastName()).isEqualTo("Carter");
        assertThat(dto.getSpecialties()).hasSize(1);
    }

    @Test
    void parameterizedConstructor_shouldHandleNullSpecialties() {
        VetRequestDto dto = new VetRequestDto("James", "Carter", null);
        assertThat(dto.getSpecialties()).isEmpty();
    }

    @Test
    void setSpecialties_shouldHandleNull() {
        VetRequestDto dto = new VetRequestDto();
        dto.setSpecialties(null);
        assertThat(dto.getSpecialties()).isEmpty();
    }
}
