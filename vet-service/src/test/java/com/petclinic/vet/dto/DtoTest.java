package com.petclinic.vet.dto;

import java.util.List;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class DtoTest {

    @Test
    void specialtyDto_gettersSetters() {
        SpecialtyDto dto = new SpecialtyDto();
        dto.setId(1);
        dto.setName("radiology");

        assertThat(dto.getId()).isEqualTo(1);
        assertThat(dto.getName()).isEqualTo("radiology");
    }

    @Test
    void specialtyDto_constructor() {
        SpecialtyDto dto = new SpecialtyDto(2, "surgery");

        assertThat(dto.getId()).isEqualTo(2);
        assertThat(dto.getName()).isEqualTo("surgery");
    }

    @Test
    void vetDto_gettersSetters() {
        VetDto dto = new VetDto();
        dto.setId(1);
        dto.setFirstName("James");
        dto.setLastName("Carter");
        dto.setSpecialties(List.of(new SpecialtyDto(1, "radiology")));

        assertThat(dto.getId()).isEqualTo(1);
        assertThat(dto.getFirstName()).isEqualTo("James");
        assertThat(dto.getLastName()).isEqualTo("Carter");
        assertThat(dto.getSpecialties()).hasSize(1);
    }

    @Test
    void vetDto_constructor() {
        SpecialtyDto spec = new SpecialtyDto(1, "radiology");
        VetDto dto = new VetDto(1, "James", "Carter", List.of(spec));

        assertThat(dto.getId()).isEqualTo(1);
        assertThat(dto.getFirstName()).isEqualTo("James");
        assertThat(dto.getLastName()).isEqualTo("Carter");
        assertThat(dto.getSpecialties()).hasSize(1);
    }

    @Test
    void vetDto_defaultSpecialtiesList() {
        VetDto dto = new VetDto();
        assertThat(dto.getSpecialties()).isNull();
    }
}
