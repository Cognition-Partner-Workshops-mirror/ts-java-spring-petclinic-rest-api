package com.petclinic.vet.dto;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class DtoTest {

    @Test
    void vetResponseDto_defaultConstructor() {
        VetResponseDto dto = new VetResponseDto();
        assertThat(dto.getId()).isNull();
        assertThat(dto.getFirstName()).isNull();
        assertThat(dto.getLastName()).isNull();
        assertThat(dto.getSpecialties()).isEmpty();
    }

    @Test
    void vetResponseDto_settersAndGetters() {
        VetResponseDto dto = new VetResponseDto();
        dto.setId(1);
        dto.setFirstName("James");
        dto.setLastName("Carter");
        List<SpecialtyResponseDto> specs = new ArrayList<>();
        specs.add(new SpecialtyResponseDto(1, "radiology"));
        dto.setSpecialties(specs);

        assertThat(dto.getId()).isEqualTo(1);
        assertThat(dto.getFirstName()).isEqualTo("James");
        assertThat(dto.getLastName()).isEqualTo("Carter");
        assertThat(dto.getSpecialties()).hasSize(1);
    }

    @Test
    void vetRequestDto_defaultConstructor() {
        VetRequestDto dto = new VetRequestDto();
        assertThat(dto.getFirstName()).isNull();
        assertThat(dto.getLastName()).isNull();
        assertThat(dto.getSpecialties()).isEmpty();
    }

    @Test
    void specialtyRequestDto_defaultConstructor() {
        SpecialtyRequestDto dto = new SpecialtyRequestDto();
        assertThat(dto.getName()).isNull();
    }

    @Test
    void specialtyResponseDto_defaultConstructor() {
        SpecialtyResponseDto dto = new SpecialtyResponseDto();
        assertThat(dto.getId()).isNull();
        assertThat(dto.getName()).isNull();
    }
}
