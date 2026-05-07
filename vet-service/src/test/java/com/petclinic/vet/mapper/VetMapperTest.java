package com.petclinic.vet.mapper;

import com.petclinic.vet.dto.SpecialtyResponseDto;
import com.petclinic.vet.dto.VetRequestDto;
import com.petclinic.vet.dto.VetResponseDto;
import com.petclinic.vet.entity.Specialty;
import com.petclinic.vet.entity.Vet;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class VetMapperTest {

    private final VetMapper mapper = Mappers.getMapper(VetMapper.class);

    @Test
    void toResponseDto_mapsFields() {
        Vet vet = new Vet(1, "James", "Carter");
        Specialty s = new Specialty(10, "radiology");
        vet.setSpecialties(Set.of(s));

        VetResponseDto dto = mapper.toResponseDto(vet);

        assertThat(dto.id()).isEqualTo(1);
        assertThat(dto.firstName()).isEqualTo("James");
        assertThat(dto.lastName()).isEqualTo("Carter");
        assertThat(dto.specialties()).hasSize(1);
        assertThat(dto.specialties().getFirst().name()).isEqualTo("radiology");
    }

    @Test
    void toResponseDto_nullEntity_returnsNull() {
        assertThat(mapper.toResponseDto(null)).isNull();
    }

    @Test
    void toResponseDto_nullSpecialties_returnsEmptyList() {
        Vet vet = new Vet(1, "James", "Carter");
        vet.setSpecialties(null);

        VetResponseDto dto = mapper.toResponseDto(vet);

        assertThat(dto.specialties()).isEmpty();
    }

    @Test
    void toResponseDtoList_mapsList() {
        Vet v1 = new Vet(1, "James", "Carter");
        v1.setSpecialties(Set.of());
        Vet v2 = new Vet(2, "Helen", "Leary");
        v2.setSpecialties(Set.of());

        List<VetResponseDto> result = mapper.toResponseDtoList(List.of(v1, v2));

        assertThat(result).hasSize(2);
    }

    @Test
    void toResponseDtoList_nullList_returnsNull() {
        assertThat(mapper.toResponseDtoList(null)).isNull();
    }

    @Test
    void toEntity_mapsNameFields() {
        VetRequestDto dto = new VetRequestDto("James", "Carter", List.of());

        Vet entity = mapper.toEntity(dto);

        assertThat(entity.getFirstName()).isEqualTo("James");
        assertThat(entity.getLastName()).isEqualTo("Carter");
        assertThat(entity.getId()).isNull();
        assertThat(entity.getSpecialties()).isEmpty();
    }

    @Test
    void toEntity_nullDto_returnsNull() {
        assertThat(mapper.toEntity(null)).isNull();
    }

    @Test
    void specialtiesToResponseDtoList_withSpecialties() {
        Specialty s1 = new Specialty(1, "radiology");
        Specialty s2 = new Specialty(2, "surgery");

        List<SpecialtyResponseDto> result = mapper.specialtiesToResponseDtoList(Set.of(s1, s2));

        assertThat(result).hasSize(2);
    }

    @Test
    void specialtiesToResponseDtoList_nullSet_returnsEmptyList() {
        List<SpecialtyResponseDto> result = mapper.specialtiesToResponseDtoList(null);

        assertThat(result).isEmpty();
    }
}
