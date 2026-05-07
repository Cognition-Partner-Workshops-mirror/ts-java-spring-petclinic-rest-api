package com.petclinic.vet.dto;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class VetResponseDtoTest {

    @Test
    void defaultConstructor_shouldCreateEmptyDto() {
        VetResponseDto dto = new VetResponseDto();
        assertThat(dto.getId()).isNull();
        assertThat(dto.getFirstName()).isNull();
        assertThat(dto.getLastName()).isNull();
        assertThat(dto.getSpecialties()).isEmpty();
    }

    @Test
    void parameterizedConstructor_shouldSetAllFields() {
        List<SpecialtyResponseDto> specialties = List.of(new SpecialtyResponseDto(1, "radiology"));
        VetResponseDto dto = new VetResponseDto(1, "James", "Carter", specialties);
        assertThat(dto.getId()).isEqualTo(1);
        assertThat(dto.getFirstName()).isEqualTo("James");
        assertThat(dto.getLastName()).isEqualTo("Carter");
        assertThat(dto.getSpecialties()).hasSize(1);
    }

    @Test
    void parameterizedConstructor_shouldHandleNullSpecialties() {
        VetResponseDto dto = new VetResponseDto(1, "James", "Carter", null);
        assertThat(dto.getSpecialties()).isEmpty();
    }

    @Test
    void setters_shouldUpdateFields() {
        VetResponseDto dto = new VetResponseDto();
        dto.setId(2);
        dto.setFirstName("Helen");
        dto.setLastName("Leary");
        dto.setSpecialties(List.of(new SpecialtyResponseDto(1, "surgery")));

        assertThat(dto.getId()).isEqualTo(2);
        assertThat(dto.getFirstName()).isEqualTo("Helen");
        assertThat(dto.getLastName()).isEqualTo("Leary");
        assertThat(dto.getSpecialties()).hasSize(1);
    }

    @Test
    void setSpecialties_shouldHandleNull() {
        VetResponseDto dto = new VetResponseDto();
        dto.setSpecialties(null);
        assertThat(dto.getSpecialties()).isEmpty();
    }
}
