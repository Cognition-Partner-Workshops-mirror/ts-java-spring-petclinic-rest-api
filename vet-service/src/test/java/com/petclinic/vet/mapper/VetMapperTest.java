package com.petclinic.vet.mapper;

import com.petclinic.vet.dto.SpecialtyResponseDto;
import com.petclinic.vet.dto.VetRequestDto;
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

    private final VetMapper mapper = Mappers.getMapper(VetMapper.class);

    @Test
    void toResponseDto_mapsAllFieldsIncludingSpecialties() {
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
    void toResponseDto_nullInput_returnsNull() {
        assertThat(mapper.toResponseDto(null)).isNull();
    }

    @Test
    void toResponseDto_emptySpecialties() {
        Vet vet = new Vet(1, "James", "Carter");
        vet.setSpecialties(new HashSet<>());

        VetResponseDto dto = mapper.toResponseDto(vet);

        assertThat(dto.specialties()).isEmpty();
    }

    @Test
    void toResponseDto_nullSpecialties() {
        Vet vet = new Vet(1, "James", "Carter");
        vet.setSpecialties(null);

        VetResponseDto dto = mapper.toResponseDto(vet);

        assertThat(dto.specialties()).isEmpty();
    }

    @Test
    void toResponseDtoList_mapsAll() {
        Vet vet1 = new Vet(1, "James", "Carter");
        vet1.setSpecialties(new HashSet<>());
        Vet vet2 = new Vet(2, "Helen", "Leary");
        vet2.setSpecialties(new HashSet<>());

        List<VetResponseDto> dtos = mapper.toResponseDtoList(List.of(vet1, vet2));

        assertThat(dtos).hasSize(2);
        assertThat(dtos.get(0).firstName()).isEqualTo("James");
        assertThat(dtos.get(1).firstName()).isEqualTo("Helen");
    }

    @Test
    void toResponseDtoList_nullInput_returnsNull() {
        assertThat(mapper.toResponseDtoList(null)).isNull();
    }

    @Test
    void toEntity_mapsFieldsIgnoresIdAndSpecialties() {
        VetRequestDto dto = new VetRequestDto("James", "Carter",
            List.of(new SpecialtyResponseDto(1, "radiology")));

        Vet entity = mapper.toEntity(dto);

        assertThat(entity.getId()).isNull();
        assertThat(entity.getFirstName()).isEqualTo("James");
        assertThat(entity.getLastName()).isEqualTo("Carter");
        assertThat(entity.getSpecialties()).isEmpty();
        assertThat(entity.getCreatedAt()).isNull();
        assertThat(entity.getUpdatedAt()).isNull();
    }

    @Test
    void toEntity_nullInput_returnsNull() {
        assertThat(mapper.toEntity(null)).isNull();
    }

    @Test
    void updateEntity_updatesFieldsIgnoresIdAndSpecialties() {
        Vet entity = new Vet(1, "James", "Carter");
        entity.setSpecialties(new HashSet<>());
        VetRequestDto dto = new VetRequestDto("Updated", "Name", List.of());

        mapper.updateEntity(dto, entity);

        assertThat(entity.getId()).isEqualTo(1);
        assertThat(entity.getFirstName()).isEqualTo("Updated");
        assertThat(entity.getLastName()).isEqualTo("Name");
    }

    @Test
    void specialtiesToDtoList_convertsSetToList() {
        Set<Specialty> specialties = Set.of(
            new Specialty(1, "radiology"),
            new Specialty(2, "surgery")
        );

        List<SpecialtyResponseDto> dtos = mapper.specialtiesToDtoList(specialties);

        assertThat(dtos).hasSize(2);
    }

    @Test
    void specialtiesToDtoList_nullReturnsEmptyList() {
        List<SpecialtyResponseDto> dtos = mapper.specialtiesToDtoList(null);
        assertThat(dtos).isEmpty();
    }
}
