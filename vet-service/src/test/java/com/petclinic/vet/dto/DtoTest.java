package com.petclinic.vet.dto;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class DtoTest {

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

    @Test
    void vetRequestDto_defaultConstructor() {
        VetRequestDto dto = new VetRequestDto();
        assertThat(dto.getFirstName()).isNull();
        assertThat(dto.getLastName()).isNull();
        assertThat(dto.getSpecialties()).isEmpty();
    }

    @Test
    void vetRequestDto_parameterizedConstructor() {
        SpecialtyResponseDto spec = new SpecialtyResponseDto(1, "radiology");
        VetRequestDto dto = new VetRequestDto("James", "Carter", List.of(spec));
        assertThat(dto.getFirstName()).isEqualTo("James");
        assertThat(dto.getLastName()).isEqualTo("Carter");
        assertThat(dto.getSpecialties()).hasSize(1);
    }

    @Test
    void vetRequestDto_parameterizedConstructor_nullSpecialties() {
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
    void vetResponseDto_defaultConstructor() {
        VetResponseDto dto = new VetResponseDto();
        assertThat(dto.getId()).isNull();
        assertThat(dto.getFirstName()).isNull();
        assertThat(dto.getLastName()).isNull();
        assertThat(dto.getSpecialties()).isEmpty();
    }

    @Test
    void vetResponseDto_parameterizedConstructor() {
        SpecialtyResponseDto spec = new SpecialtyResponseDto(1, "radiology");
        VetResponseDto dto = new VetResponseDto(1, "James", "Carter", List.of(spec));
        assertThat(dto.getId()).isEqualTo(1);
        assertThat(dto.getFirstName()).isEqualTo("James");
        assertThat(dto.getLastName()).isEqualTo("Carter");
        assertThat(dto.getSpecialties()).hasSize(1);
    }

    @Test
    void vetResponseDto_parameterizedConstructor_nullSpecialties() {
        VetResponseDto dto = new VetResponseDto(1, "James", "Carter", null);
        assertThat(dto.getSpecialties()).isEmpty();
    }

    @Test
    void vetResponseDto_setters() {
        VetResponseDto dto = new VetResponseDto();
        dto.setId(1);
        dto.setFirstName("James");
        dto.setLastName("Carter");
        dto.setSpecialties(new ArrayList<>());
        assertThat(dto.getId()).isEqualTo(1);
        assertThat(dto.getFirstName()).isEqualTo("James");
        assertThat(dto.getLastName()).isEqualTo("Carter");
        assertThat(dto.getSpecialties()).isEmpty();
    }
}
