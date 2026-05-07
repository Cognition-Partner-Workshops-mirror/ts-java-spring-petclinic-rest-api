package com.petclinic.vet.mapper;

import com.petclinic.vet.dto.SpecialtyResponseDto;
import com.petclinic.vet.dto.VetRequestDto;
import com.petclinic.vet.dto.VetResponseDto;
import com.petclinic.vet.entity.Specialty;
import com.petclinic.vet.entity.Vet;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import static org.assertj.core.api.Assertions.assertThat;

class VetMapperTest {

    private final VetMapper mapper = Mappers.getMapper(VetMapper.class);

    @Test
    void toResponseDto_mapsAllFields() {
        Specialty radiology = new Specialty();
        radiology.setId(1);
        radiology.setName("radiology");

        Vet vet = new Vet();
        vet.setId(1);
        vet.setFirstName("James");
        vet.setLastName("Carter");
        vet.setSpecialties(Set.of(radiology));

        VetResponseDto dto = mapper.toResponseDto(vet);

        assertThat(dto.id()).isEqualTo(1);
        assertThat(dto.firstName()).isEqualTo("James");
        assertThat(dto.lastName()).isEqualTo("Carter");
        assertThat(dto.specialties()).hasSize(1);
        assertThat(dto.specialties().get(0).name()).isEqualTo("radiology");
    }

    @Test
    void toResponseDto_nullEntity_returnsNull() {
        VetResponseDto dto = mapper.toResponseDto(null);
        assertThat(dto).isNull();
    }

    @Test
    void toResponseDto_emptySpecialties_returnsEmptyList() {
        Vet vet = new Vet();
        vet.setId(1);
        vet.setFirstName("James");
        vet.setLastName("Carter");
        vet.setSpecialties(new HashSet<>());

        VetResponseDto dto = mapper.toResponseDto(vet);

        assertThat(dto.specialties()).isEmpty();
    }

    @Test
    void toResponseDto_nullSpecialties_returnsEmptyList() {
        Vet vet = new Vet();
        vet.setId(1);
        vet.setFirstName("James");
        vet.setLastName("Carter");
        vet.setSpecialties(null);

        VetResponseDto dto = mapper.toResponseDto(vet);

        assertThat(dto.specialties()).isEmpty();
    }

    @Test
    void toEntity_mapsFields() {
        VetRequestDto dto = new VetRequestDto("James", "Carter", List.of());

        Vet vet = mapper.toEntity(dto);

        assertThat(vet.getFirstName()).isEqualTo("James");
        assertThat(vet.getLastName()).isEqualTo("Carter");
        assertThat(vet.getId()).isNull();
    }

    @Test
    void toEntity_nullDto_returnsNull() {
        Vet vet = mapper.toEntity(null);
        assertThat(vet).isNull();
    }

    @Test
    void specialtiesToDtoList_mapsMultipleSpecialties() {
        Specialty s1 = new Specialty();
        s1.setId(1);
        s1.setName("radiology");
        Specialty s2 = new Specialty();
        s2.setId(2);
        s2.setName("surgery");

        List<SpecialtyResponseDto> result = mapper.specialtiesToDtoList(Set.of(s1, s2));

        assertThat(result).hasSize(2);
    }

    @Test
    void specialtiesToDtoList_nullInput_returnsEmptyList() {
        List<SpecialtyResponseDto> result = mapper.specialtiesToDtoList(null);
        assertThat(result).isEmpty();
    }
}
