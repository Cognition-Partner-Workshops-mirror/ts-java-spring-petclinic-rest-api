package com.petclinic.vet.service;

import com.petclinic.vet.dto.SpecialtyResponseDto;
import com.petclinic.vet.dto.VetResponseDto;
import java.util.List;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class DtoTest {

    @Test
    void vetResponseDto_gettersAndSetters() {
        VetResponseDto dto = new VetResponseDto();
        dto.setId(1);
        dto.setFirstName("James");
        dto.setLastName("Carter");
        dto.setSpecialties(List.of(new SpecialtyResponseDto(1, "radiology")));

        assertThat(dto.getId()).isEqualTo(1);
        assertThat(dto.getFirstName()).isEqualTo("James");
        assertThat(dto.getLastName()).isEqualTo("Carter");
        assertThat(dto.getSpecialties()).hasSize(1);
    }

    @Test
    void vetResponseDto_allArgsConstructor() {
        VetResponseDto dto = new VetResponseDto(2, "Helen", "Leary",
            List.of(new SpecialtyResponseDto(1, "radiology")));

        assertThat(dto.getId()).isEqualTo(2);
        assertThat(dto.getFirstName()).isEqualTo("Helen");
        assertThat(dto.getLastName()).isEqualTo("Leary");
        assertThat(dto.getSpecialties()).hasSize(1);
    }
}
