package com.petclinic.vet.dto;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for DTO classes to ensure full coverage of getters/setters.
 */
class DtoTest {

    @Test
    void vetResponseDto_gettersAndSetters() {
        VetResponseDto dto = new VetResponseDto();
        dto.setId(1);
        dto.setFirstName("James");
        dto.setLastName("Carter");
        SpecialtyResponseDto specialtyDto = new SpecialtyResponseDto(1, "radiology");
        dto.setSpecialties(List.of(specialtyDto));

        assertThat(dto.getId()).isEqualTo(1);
        assertThat(dto.getFirstName()).isEqualTo("James");
        assertThat(dto.getLastName()).isEqualTo("Carter");
        assertThat(dto.getSpecialties()).hasSize(1);
    }

    @Test
    void vetResponseDto_constructorWithArgs() {
        SpecialtyResponseDto specialtyDto = new SpecialtyResponseDto(1, "radiology");
        VetResponseDto dto = new VetResponseDto(2, "Helen", "Leary", List.of(specialtyDto));

        assertThat(dto.getId()).isEqualTo(2);
        assertThat(dto.getFirstName()).isEqualTo("Helen");
        assertThat(dto.getLastName()).isEqualTo("Leary");
        assertThat(dto.getSpecialties()).hasSize(1);
    }

    @Test
    void vetRequestDto_gettersAndSetters() {
        VetRequestDto dto = new VetRequestDto();
        dto.setFirstName("James");
        dto.setLastName("Carter");
        dto.setSpecialties(List.of());

        assertThat(dto.getFirstName()).isEqualTo("James");
        assertThat(dto.getLastName()).isEqualTo("Carter");
        assertThat(dto.getSpecialties()).isEmpty();
    }

    @Test
    void specialtyResponseDto_gettersAndSetters() {
        SpecialtyResponseDto dto = new SpecialtyResponseDto();
        dto.setId(1);
        dto.setName("radiology");

        assertThat(dto.getId()).isEqualTo(1);
        assertThat(dto.getName()).isEqualTo("radiology");
    }

    @Test
    void specialtyRequestDto_gettersAndSetters() {
        SpecialtyRequestDto dto = new SpecialtyRequestDto();
        dto.setName("surgery");

        assertThat(dto.getName()).isEqualTo("surgery");
    }
}
