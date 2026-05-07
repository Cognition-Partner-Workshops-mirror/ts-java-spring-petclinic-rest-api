package com.petclinic.vet.mapper;

import com.petclinic.vet.dto.SpecialtyResponseDto;
import com.petclinic.vet.dto.VetResponseDto;
import com.petclinic.vet.entity.Specialty;
import com.petclinic.vet.entity.Vet;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
class VetMapperTest {

    @Autowired
    private VetMapper vetMapper;

    @Test
    void toResponseDto_mapsFields() {
        Specialty radiology = new Specialty(1, "radiology");
        Vet vet = new Vet(1, "James", "Carter");
        vet.setSpecialties(Set.of(radiology));

        VetResponseDto dto = vetMapper.toResponseDto(vet);

        assertThat(dto.id()).isEqualTo(1);
        assertThat(dto.firstName()).isEqualTo("James");
        assertThat(dto.lastName()).isEqualTo("Carter");
        assertThat(dto.specialties()).hasSize(1);
        assertThat(dto.specialties().get(0).name()).isEqualTo("radiology");
    }

    @Test
    void toResponseDtoList_mapsMultiple() {
        Vet vet1 = new Vet(1, "James", "Carter");
        vet1.setSpecialties(new HashSet<>());
        Vet vet2 = new Vet(2, "Helen", "Leary");
        vet2.setSpecialties(new HashSet<>());

        List<VetResponseDto> dtos = vetMapper.toResponseDtoList(List.of(vet1, vet2));

        assertThat(dtos).hasSize(2);
    }

    @Test
    void specialtySetToList_nullReturnsEmpty() {
        List<SpecialtyResponseDto> result = vetMapper.specialtySetToList(null);
        assertThat(result).isEmpty();
    }

    @Test
    void specialtySetToList_emptyReturnsEmpty() {
        List<SpecialtyResponseDto> result = vetMapper.specialtySetToList(new HashSet<>());
        assertThat(result).isEmpty();
    }

    @Test
    void specialtyToDto_mapsFields() {
        Specialty specialty = new Specialty(3, "dentistry");

        SpecialtyResponseDto dto = vetMapper.specialtyToDto(specialty);

        assertThat(dto.id()).isEqualTo(3);
        assertThat(dto.name()).isEqualTo("dentistry");
    }
}
