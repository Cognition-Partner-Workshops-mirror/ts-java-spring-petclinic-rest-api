package com.petclinic.vet.dto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for DTO getters/setters and constructors.
 */
class DtoTest {

    @Test
    @DisplayName("VetResponseDto getters and setters work correctly")
    void vetResponseDto_gettersSetters() {
        VetResponseDto dto = new VetResponseDto();
        List<SpecialtyResponseDto> specialties = List.of(
            new SpecialtyResponseDto(1, "radiology"));

        dto.setId(1);
        dto.setFirstName("James");
        dto.setLastName("Carter");
        dto.setSpecialties(specialties);

        assertThat(dto.getId()).isEqualTo(1);
        assertThat(dto.getFirstName()).isEqualTo("James");
        assertThat(dto.getLastName()).isEqualTo("Carter");
        assertThat(dto.getSpecialties()).hasSize(1);
    }

    @Test
    @DisplayName("SpecialtyResponseDto getters and setters work correctly")
    void specialtyResponseDto_gettersSetters() {
        SpecialtyResponseDto dto = new SpecialtyResponseDto();

        dto.setId(1);
        dto.setName("surgery");

        assertThat(dto.getId()).isEqualTo(1);
        assertThat(dto.getName()).isEqualTo("surgery");
    }

    @Test
    @DisplayName("VetRequestDto getters and setters work correctly")
    void vetRequestDto_gettersSetters() {
        VetRequestDto dto = new VetRequestDto();

        dto.setFirstName("James");
        dto.setLastName("Carter");
        dto.setSpecialtyIds(List.of(1, 2));

        assertThat(dto.getFirstName()).isEqualTo("James");
        assertThat(dto.getLastName()).isEqualTo("Carter");
        assertThat(dto.getSpecialtyIds()).containsExactly(1, 2);
    }

    @Test
    @DisplayName("SpecialtyRequestDto getters and setters work correctly")
    void specialtyRequestDto_gettersSetters() {
        SpecialtyRequestDto dto = new SpecialtyRequestDto();

        dto.setName("dentistry");

        assertThat(dto.getName()).isEqualTo("dentistry");
    }

    @Test
    @DisplayName("VetResponseDto parameterized constructor works")
    void vetResponseDto_constructor() {
        List<SpecialtyResponseDto> specialties = List.of(
            new SpecialtyResponseDto(1, "radiology"));
        VetResponseDto dto = new VetResponseDto(1, "James", "Carter", specialties);

        assertThat(dto.getId()).isEqualTo(1);
        assertThat(dto.getSpecialties()).hasSize(1);
    }

    @Test
    @DisplayName("VetRequestDto parameterized constructor works")
    void vetRequestDto_constructor() {
        VetRequestDto dto = new VetRequestDto("James", "Carter", List.of(1));

        assertThat(dto.getFirstName()).isEqualTo("James");
        assertThat(dto.getSpecialtyIds()).containsExactly(1);
    }
}
