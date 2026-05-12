package com.petclinic.vet.dto;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for DTO constructors and getters/setters.
 * Ensures all DTO fields are accessible.
 */
class DtoTest {

    @Test
    void specialtyRequestDto_gettersSetters() {
        SpecialtyRequestDto dto = new SpecialtyRequestDto();
        dto.setName("radiology");
        assertThat(dto.getName()).isEqualTo("radiology");
    }

    @Test
    void specialtyResponseDto_gettersSetters() {
        SpecialtyResponseDto dto = new SpecialtyResponseDto();
        dto.setId(1);
        dto.setName("radiology");
        assertThat(dto.getId()).isEqualTo(1);
        assertThat(dto.getName()).isEqualTo("radiology");
    }

    @Test
    void vetRequestDto_gettersSetters() {
        VetRequestDto dto = new VetRequestDto();
        SpecialtyResponseDto specialty = new SpecialtyResponseDto(1, "radiology");
        dto.setFirstName("James");
        dto.setLastName("Carter");
        dto.setSpecialties(List.of(specialty));
        assertThat(dto.getFirstName()).isEqualTo("James");
        assertThat(dto.getLastName()).isEqualTo("Carter");
        assertThat(dto.getSpecialties()).hasSize(1);
    }

    @Test
    void vetResponseDto_gettersSetters() {
        VetResponseDto dto = new VetResponseDto();
        SpecialtyResponseDto specialty = new SpecialtyResponseDto(1, "radiology");
        dto.setId(1);
        dto.setFirstName("James");
        dto.setLastName("Carter");
        dto.setSpecialties(List.of(specialty));
        assertThat(dto.getId()).isEqualTo(1);
        assertThat(dto.getFirstName()).isEqualTo("James");
        assertThat(dto.getLastName()).isEqualTo("Carter");
        assertThat(dto.getSpecialties()).hasSize(1);
    }

    @Test
    void specialtyRequestDto_allArgsConstructor() {
        SpecialtyRequestDto dto = new SpecialtyRequestDto("radiology");
        assertThat(dto.getName()).isEqualTo("radiology");
    }

    @Test
    void specialtyResponseDto_allArgsConstructor() {
        SpecialtyResponseDto dto = new SpecialtyResponseDto(1, "radiology");
        assertThat(dto.getId()).isEqualTo(1);
        assertThat(dto.getName()).isEqualTo("radiology");
    }

    @Test
    void vetRequestDto_allArgsConstructor() {
        VetRequestDto dto = new VetRequestDto("James", "Carter", List.of());
        assertThat(dto.getFirstName()).isEqualTo("James");
        assertThat(dto.getLastName()).isEqualTo("Carter");
        assertThat(dto.getSpecialties()).isEmpty();
    }

    @Test
    void vetResponseDto_allArgsConstructor() {
        VetResponseDto dto = new VetResponseDto(1, "James", "Carter", List.of());
        assertThat(dto.getId()).isEqualTo(1);
        assertThat(dto.getFirstName()).isEqualTo("James");
        assertThat(dto.getLastName()).isEqualTo("Carter");
        assertThat(dto.getSpecialties()).isEmpty();
    }
}
