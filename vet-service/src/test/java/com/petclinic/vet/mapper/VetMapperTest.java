package com.petclinic.vet.mapper;

import com.petclinic.vet.dto.SpecialtyResponseDto;
import com.petclinic.vet.entity.Specialty;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class VetMapperTest {

    private final VetMapper vetMapper = new VetMapper() {
        @Override
        public com.petclinic.vet.dto.VetResponseDto toResponseDto(com.petclinic.vet.entity.Vet vet) {
            return null;
        }

        @Override
        public List<com.petclinic.vet.dto.VetResponseDto> toResponseDtos(List<com.petclinic.vet.entity.Vet> vets) {
            return null;
        }

        @Override
        public com.petclinic.vet.entity.Vet toEntity(com.petclinic.vet.dto.VetRequestDto dto) {
            return null;
        }
    };

    @Test
    void sortedSpecialties_shouldReturnEmptyListForNull() {
        List<SpecialtyResponseDto> result = vetMapper.sortedSpecialties(null);
        assertThat(result).isEmpty();
    }

    @Test
    void sortedSpecialties_shouldReturnEmptyListForEmptySet() {
        List<SpecialtyResponseDto> result = vetMapper.sortedSpecialties(new HashSet<>());
        assertThat(result).isEmpty();
    }

    @Test
    void sortedSpecialties_shouldSortByNameCaseInsensitive() {
        Set<Specialty> specialties = new HashSet<>();
        specialties.add(new Specialty(1, "surgery"));
        specialties.add(new Specialty(2, "Dentistry"));
        specialties.add(new Specialty(3, "radiology"));

        List<SpecialtyResponseDto> result = vetMapper.sortedSpecialties(specialties);

        assertThat(result).hasSize(3);
        assertThat(result.get(0).getName()).isEqualTo("Dentistry");
        assertThat(result.get(1).getName()).isEqualTo("radiology");
        assertThat(result.get(2).getName()).isEqualTo("surgery");
    }

    @Test
    void sortedSpecialties_shouldMapIdAndName() {
        Set<Specialty> specialties = new HashSet<>();
        specialties.add(new Specialty(42, "radiology"));

        List<SpecialtyResponseDto> result = vetMapper.sortedSpecialties(specialties);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getId()).isEqualTo(42);
        assertThat(result.get(0).getName()).isEqualTo("radiology");
    }
}
