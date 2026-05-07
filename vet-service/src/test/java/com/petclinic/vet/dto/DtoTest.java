package com.petclinic.vet.dto;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for DTO getters/setters and constructors to ensure full coverage.
 */
class DtoTest {

    @Test
    void specialtyRequestDto_defaultConstructorAndSetters() {
        SpecialtyRequestDto dto = new SpecialtyRequestDto();
        dto.setName("radiology");
        assertThat(dto.getName()).isEqualTo("radiology");
    }

    @Test
    void specialtyResponseDto_defaultConstructorAndSetters() {
        SpecialtyResponseDto dto = new SpecialtyResponseDto();
        dto.setId(1);
        dto.setName("surgery");
        assertThat(dto.getId()).isEqualTo(1);
        assertThat(dto.getName()).isEqualTo("surgery");
    }

    @Test
    void specialtyResponseDto_parameterizedConstructor() {
        SpecialtyResponseDto dto = new SpecialtyResponseDto(2, "dentistry");
        assertThat(dto.getId()).isEqualTo(2);
        assertThat(dto.getName()).isEqualTo("dentistry");
    }

    @Test
    void vetRequestDto_defaultConstructorAndSetters() {
        VetRequestDto dto = new VetRequestDto();
        dto.setFirstName("James");
        dto.setLastName("Carter");
        dto.setSpecialtyIds(List.of(1, 2));
        assertThat(dto.getFirstName()).isEqualTo("James");
        assertThat(dto.getLastName()).isEqualTo("Carter");
        assertThat(dto.getSpecialtyIds()).containsExactly(1, 2);
    }

    @Test
    void vetResponseDto_defaultConstructorAndSetters() {
        VetResponseDto dto = new VetResponseDto();
        dto.setId(1);
        dto.setFirstName("James");
        dto.setLastName("Carter");
        List<SpecialtyResponseDto> specialties = List.of(
            new SpecialtyResponseDto(1, "radiology"));
        dto.setSpecialties(specialties);

        assertThat(dto.getId()).isEqualTo(1);
        assertThat(dto.getFirstName()).isEqualTo("James");
        assertThat(dto.getLastName()).isEqualTo("Carter");
        assertThat(dto.getSpecialties()).hasSize(1);
    }

    @Test
    void vetResponseDto_parameterizedConstructor() {
        VetResponseDto dto = new VetResponseDto(1, "James", "Carter",
            List.of(new SpecialtyResponseDto(1, "radiology")));
        assertThat(dto.getId()).isEqualTo(1);
        assertThat(dto.getFirstName()).isEqualTo("James");
    }
}
