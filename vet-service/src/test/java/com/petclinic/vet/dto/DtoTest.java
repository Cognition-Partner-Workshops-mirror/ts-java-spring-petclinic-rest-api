package com.petclinic.vet.dto;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

// Unit tests for DTO constructors, getters, setters, and null-safe defaults
class DtoTest {

    // --- SpecialtyRequestDto ---

    @Test
    void specialtyRequestDto_defaultConstructor() {
        SpecialtyRequestDto dto = new SpecialtyRequestDto();
        assertThat(dto.getName()).isNull();
    }

    @Test
    void specialtyRequestDto_parameterizedConstructor() {
        SpecialtyRequestDto dto = new SpecialtyRequestDto("radiology");
        assertThat(dto.getName()).isEqualTo("radiology");
    }

    @Test
    void specialtyRequestDto_setName() {
        SpecialtyRequestDto dto = new SpecialtyRequestDto();
        dto.setName("surgery");
        assertThat(dto.getName()).isEqualTo("surgery");
    }

    // --- SpecialtyResponseDto ---

    @Test
    void specialtyResponseDto_defaultConstructor() {
        SpecialtyResponseDto dto = new SpecialtyResponseDto();
        assertThat(dto.getId()).isNull();
        assertThat(dto.getName()).isNull();
    }

    @Test
    void specialtyResponseDto_parameterizedConstructor() {
        SpecialtyResponseDto dto = new SpecialtyResponseDto(1, "radiology");
        assertThat(dto.getId()).isEqualTo(1);
        assertThat(dto.getName()).isEqualTo("radiology");
    }

    @Test
    void specialtyResponseDto_setters() {
        SpecialtyResponseDto dto = new SpecialtyResponseDto();
        dto.setId(2);
        dto.setName("surgery");
        assertThat(dto.getId()).isEqualTo(2);
        assertThat(dto.getName()).isEqualTo("surgery");
    }

    // --- VetRequestDto ---

    @Test
    void vetRequestDto_defaultConstructor() {
        VetRequestDto dto = new VetRequestDto();
        assertThat(dto.getFirstName()).isNull();
        assertThat(dto.getLastName()).isNull();
        assertThat(dto.getSpecialties()).isEmpty();
    }

    @Test
    void vetRequestDto_parameterizedConstructor() {
        List<SpecialtyResponseDto> specs = List.of(new SpecialtyResponseDto(1, "radiology"));
        VetRequestDto dto = new VetRequestDto("James", "Carter", specs);

        assertThat(dto.getFirstName()).isEqualTo("James");
        assertThat(dto.getLastName()).isEqualTo("Carter");
        assertThat(dto.getSpecialties()).hasSize(1);
    }

    @Test
    void vetRequestDto_parameterizedConstructor_nullSpecialties() {
        // Null specialties should default to empty list
        VetRequestDto dto = new VetRequestDto("James", "Carter", null);
        assertThat(dto.getSpecialties()).isEmpty();
    }

    @Test
    void vetRequestDto_setters() {
        VetRequestDto dto = new VetRequestDto();
        dto.setFirstName("Helen");
        dto.setLastName("Leary");
        dto.setSpecialties(List.of(new SpecialtyResponseDto(1, "radiology")));

        assertThat(dto.getFirstName()).isEqualTo("Helen");
        assertThat(dto.getLastName()).isEqualTo("Leary");
        assertThat(dto.getSpecialties()).hasSize(1);
    }

    @Test
    void vetRequestDto_setSpecialties_null_defaultsToEmptyList() {
        // Setting null specialties should default to empty list
        VetRequestDto dto = new VetRequestDto();
        dto.setSpecialties(null);
        assertThat(dto.getSpecialties()).isEmpty();
    }

    // --- VetResponseDto ---

    @Test
    void vetResponseDto_defaultConstructor() {
        VetResponseDto dto = new VetResponseDto();
        assertThat(dto.getId()).isNull();
        assertThat(dto.getFirstName()).isNull();
        assertThat(dto.getLastName()).isNull();
        assertThat(dto.getSpecialties()).isEmpty();
    }

    @Test
    void vetResponseDto_parameterizedConstructor() {
        List<SpecialtyResponseDto> specs = List.of(new SpecialtyResponseDto(1, "radiology"));
        VetResponseDto dto = new VetResponseDto(1, "James", "Carter", specs);

        assertThat(dto.getId()).isEqualTo(1);
        assertThat(dto.getFirstName()).isEqualTo("James");
        assertThat(dto.getLastName()).isEqualTo("Carter");
        assertThat(dto.getSpecialties()).hasSize(1);
    }

    @Test
    void vetResponseDto_parameterizedConstructor_nullSpecialties() {
        // Null specialties should default to empty list
        VetResponseDto dto = new VetResponseDto(1, "James", "Carter", null);
        assertThat(dto.getSpecialties()).isEmpty();
    }

    @Test
    void vetResponseDto_setters() {
        VetResponseDto dto = new VetResponseDto();
        dto.setId(2);
        dto.setFirstName("Helen");
        dto.setLastName("Leary");
        dto.setSpecialties(List.of(new SpecialtyResponseDto(1, "radiology")));

        assertThat(dto.getId()).isEqualTo(2);
        assertThat(dto.getFirstName()).isEqualTo("Helen");
        assertThat(dto.getLastName()).isEqualTo("Leary");
        assertThat(dto.getSpecialties()).hasSize(1);
    }

    @Test
    void vetResponseDto_setSpecialties_null_defaultsToEmptyList() {
        // Setting null specialties should default to empty list
        VetResponseDto dto = new VetResponseDto();
        dto.setSpecialties(null);
        assertThat(dto.getSpecialties()).isEmpty();
    }
}
