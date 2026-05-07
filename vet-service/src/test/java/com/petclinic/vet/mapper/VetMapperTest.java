package com.petclinic.vet.mapper;

import com.petclinic.vet.dto.SpecialtyResponseDto;
import com.petclinic.vet.dto.VetRequestDto;
import com.petclinic.vet.dto.VetResponseDto;
import com.petclinic.vet.entity.Specialty;
import com.petclinic.vet.entity.Vet;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class VetMapperTest {

    private final VetMapper mapper = Mappers.getMapper(VetMapper.class);

    @Test
    void toResponseDto_mapsCorrectly() {
        Specialty specialty = new Specialty(1, "radiology");
        Vet vet = new Vet(1, "James", "Carter");
        vet.setSpecialties(Set.of(specialty));

        VetResponseDto dto = mapper.toResponseDto(vet);

        assertThat(dto.id()).isEqualTo(1);
        assertThat(dto.firstName()).isEqualTo("James");
        assertThat(dto.lastName()).isEqualTo("Carter");
        assertThat(dto.specialties()).hasSize(1);
        assertThat(dto.specialties().get(0).name()).isEqualTo("radiology");
    }

    @Test
    void toResponseDto_handlesNullSpecialties() {
        Vet vet = new Vet(1, "James", "Carter");
        vet.setSpecialties(null);

        VetResponseDto dto = mapper.toResponseDto(vet);

        assertThat(dto.specialties()).isEmpty();
    }

    @Test
    void toEntity_mapsCorrectly() {
        VetRequestDto requestDto = new VetRequestDto("James", "Carter", List.of());

        Vet vet = mapper.toEntity(requestDto);

        assertThat(vet.getFirstName()).isEqualTo("James");
        assertThat(vet.getLastName()).isEqualTo("Carter");
        assertThat(vet.getId()).isNull();
    }

    @Test
    void toResponseDtoList_mapsCorrectly() {
        Vet vet1 = new Vet(1, "James", "Carter");
        vet1.setSpecialties(new LinkedHashSet<>());
        Vet vet2 = new Vet(2, "Helen", "Leary");
        vet2.setSpecialties(new LinkedHashSet<>());

        List<VetResponseDto> dtos = mapper.toResponseDtoList(List.of(vet1, vet2));

        assertThat(dtos).hasSize(2);
    }

    @Test
    void specialtySetToList_handlesNull() {
        List<SpecialtyResponseDto> result = mapper.specialtySetToList(null);

        assertThat(result).isEmpty();
    }
}
