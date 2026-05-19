package com.petclinic.vet.mapper;

import com.petclinic.vet.dto.SpecialtyResponseDto;
import com.petclinic.vet.dto.VetResponseDto;
import com.petclinic.vet.entity.Specialty;
import com.petclinic.vet.entity.Vet;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for VetMapper interface default methods.
 */
class VetMapperTest {

    private final VetMapper vetMapper = Mappers.getMapper(VetMapper.class);

    @Test
    @DisplayName("toResponseDto maps vet entity to response DTO")
    void toResponseDto_mapsCorrectly() {
        Specialty radiology = new Specialty(1, "radiology");
        Specialty surgery = new Specialty(2, "surgery");
        Vet vet = new Vet(1, "James", "Carter");
        vet.setSpecialties(new HashSet<>(Set.of(radiology, surgery)));

        VetResponseDto dto = vetMapper.toResponseDto(vet);

        assertThat(dto.getId()).isEqualTo(1);
        assertThat(dto.getFirstName()).isEqualTo("James");
        assertThat(dto.getLastName()).isEqualTo("Carter");
        assertThat(dto.getSpecialties()).hasSize(2);
    }

    @Test
    @DisplayName("mapSpecialties returns sorted list")
    void mapSpecialties_sortedById() {
        Specialty surgery = new Specialty(2, "surgery");
        Specialty radiology = new Specialty(1, "radiology");

        List<SpecialtyResponseDto> result = vetMapper.mapSpecialties(
            new HashSet<>(Set.of(surgery, radiology)));

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getId()).isEqualTo(1);
        assertThat(result.get(1).getId()).isEqualTo(2);
    }

    @Test
    @DisplayName("mapSpecialties returns empty list for null input")
    void mapSpecialties_nullReturnsEmpty() {
        List<SpecialtyResponseDto> result = vetMapper.mapSpecialties(null);
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("mapSpecialties returns empty list for empty set")
    void mapSpecialties_emptySet() {
        List<SpecialtyResponseDto> result = vetMapper.mapSpecialties(new HashSet<>());
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("specialtyToDto maps correctly")
    void specialtyToDto_mapsCorrectly() {
        Specialty specialty = new Specialty(1, "radiology");

        SpecialtyResponseDto dto = vetMapper.specialtyToDto(specialty);

        assertThat(dto.getId()).isEqualTo(1);
        assertThat(dto.getName()).isEqualTo("radiology");
    }
}
