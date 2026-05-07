package com.petclinic.vet.mapper;

import com.petclinic.vet.dto.SpecialtyResponseDto;
import com.petclinic.vet.dto.VetResponseDto;
import com.petclinic.vet.entity.Specialty;
import com.petclinic.vet.entity.Vet;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class VetMapperTest {

    private final VetMapper vetMapper = Mappers.getMapper(VetMapper.class);

    private Specialty createSpecialty(int id, String name) {
        Specialty s = new Specialty();
        s.setId(id);
        s.setName(name);
        return s;
    }

    @Test
    void toResponseDto_mapsCorrectly() {
        Vet vet = new Vet();
        vet.setId(1);
        vet.setFirstName("James");
        vet.setLastName("Carter");
        vet.setSpecialties(new HashSet<>(Set.of(createSpecialty(1, "radiology"))));

        VetResponseDto dto = vetMapper.toResponseDto(vet);

        assertThat(dto.getId()).isEqualTo(1);
        assertThat(dto.getFirstName()).isEqualTo("James");
        assertThat(dto.getLastName()).isEqualTo("Carter");
        assertThat(dto.getSpecialties()).hasSize(1);
        assertThat(dto.getSpecialties().get(0).getName()).isEqualTo("radiology");
    }

    @Test
    void toResponseDtoList_mapsCorrectly() {
        Vet vet1 = new Vet();
        vet1.setId(1);
        vet1.setFirstName("James");
        vet1.setLastName("Carter");
        vet1.setSpecialties(new HashSet<>());

        Vet vet2 = new Vet();
        vet2.setId(2);
        vet2.setFirstName("Helen");
        vet2.setLastName("Leary");
        vet2.setSpecialties(new HashSet<>());

        List<VetResponseDto> dtos = vetMapper.toResponseDtoList(List.of(vet1, vet2));

        assertThat(dtos).hasSize(2);
    }

    @Test
    void toResponseDtoList_handlesNull() {
        List<VetResponseDto> dtos = vetMapper.toResponseDtoList(null);
        assertThat(dtos).isEmpty();
    }

    @Test
    void mapSpecialties_sortsByNameCaseInsensitive() {
        Set<Specialty> specialties = new HashSet<>(Set.of(
            createSpecialty(1, "surgery"),
            createSpecialty(2, "dentistry"),
            createSpecialty(3, "radiology")
        ));

        List<SpecialtyResponseDto> result = vetMapper.mapSpecialties(specialties);

        assertThat(result).hasSize(3);
        assertThat(result.get(0).getName()).isEqualTo("dentistry");
        assertThat(result.get(1).getName()).isEqualTo("radiology");
        assertThat(result.get(2).getName()).isEqualTo("surgery");
    }

    @Test
    void mapSpecialties_handlesNull() {
        List<SpecialtyResponseDto> result = vetMapper.mapSpecialties(null);
        assertThat(result).isEmpty();
    }

    @Test
    void mapSpecialties_handlesEmpty() {
        List<SpecialtyResponseDto> result = vetMapper.mapSpecialties(new HashSet<>());
        assertThat(result).isEmpty();
    }
}
