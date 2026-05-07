package com.petclinic.vet.mapper;

import com.petclinic.vet.dto.SpecialtyResponseDto;
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

    private final VetMapper vetMapper = Mappers.getMapper(VetMapper.class);

    @Test
    void toResponseDto_mapsAllFields() {
        Specialty radiology = new Specialty("radiology");
        radiology.setId(1);
        Specialty surgery = new Specialty("surgery");
        surgery.setId(2);

        Vet vet = new Vet("James", "Carter");
        vet.setId(1);
        vet.setSpecialties(Set.of(radiology, surgery));

        VetResponseDto dto = vetMapper.toResponseDto(vet);

        assertThat(dto.id()).isEqualTo(1);
        assertThat(dto.firstName()).isEqualTo("James");
        assertThat(dto.lastName()).isEqualTo("Carter");
        assertThat(dto.specialties()).hasSize(2);
        assertThat(dto.specialties().get(0).id()).isEqualTo(1);
        assertThat(dto.specialties().get(1).id()).isEqualTo(2);
    }

    @Test
    void toResponseDto_emptySpecialties() {
        Vet vet = new Vet("James", "Carter");
        vet.setId(1);
        vet.setSpecialties(new HashSet<>());

        VetResponseDto dto = vetMapper.toResponseDto(vet);

        assertThat(dto.specialties()).isEmpty();
    }

    @Test
    void toResponseDto_nullSpecialties() {
        Vet vet = new Vet("James", "Carter");
        vet.setId(1);
        vet.setSpecialties(null);

        VetResponseDto dto = vetMapper.toResponseDto(vet);

        assertThat(dto.specialties()).isEmpty();
    }

    @Test
    void mapSpecialties_sortsByIdAscending() {
        Specialty s3 = new Specialty("dentistry");
        s3.setId(3);
        Specialty s1 = new Specialty("radiology");
        s1.setId(1);

        List<SpecialtyResponseDto> result = vetMapper.mapSpecialties(Set.of(s3, s1));

        assertThat(result.get(0).id()).isEqualTo(1);
        assertThat(result.get(1).id()).isEqualTo(3);
    }
}
