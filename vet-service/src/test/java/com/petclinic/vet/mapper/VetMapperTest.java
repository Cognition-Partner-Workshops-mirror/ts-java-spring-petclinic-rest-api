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

/**
 * Tests for {@link VetMapper} MapStruct-generated implementation.
 */
@SpringBootTest
@ActiveProfiles("test")
class VetMapperTest {

    @Autowired
    private VetMapper vetMapper;

    @Test
    void shouldMapVetToResponseDto() {
        Specialty radiology = new Specialty("radiology");
        radiology.setId(1);

        Vet vet = new Vet("James", "Carter");
        vet.setId(1);
        vet.setSpecialties(Set.of(radiology));

        VetResponseDto dto = vetMapper.toResponseDto(vet);

        assertThat(dto.getId()).isEqualTo(1);
        assertThat(dto.getFirstName()).isEqualTo("James");
        assertThat(dto.getLastName()).isEqualTo("Carter");
        assertThat(dto.getSpecialties()).hasSize(1);
        assertThat(dto.getSpecialties().get(0).getName()).isEqualTo("radiology");
    }

    @Test
    void shouldMapVetListToResponseDtoList() {
        Vet vet1 = new Vet("James", "Carter");
        vet1.setId(1);
        vet1.setSpecialties(new HashSet<>());

        Vet vet2 = new Vet("Helen", "Leary");
        vet2.setId(2);
        vet2.setSpecialties(new HashSet<>());

        List<VetResponseDto> dtos = vetMapper.toResponseDtoList(List.of(vet1, vet2));

        assertThat(dtos).hasSize(2);
    }

    @Test
    void shouldHandleNullSpecialties() {
        Vet vet = new Vet("James", "Carter");
        vet.setId(1);
        vet.setSpecialties(null);

        VetResponseDto dto = vetMapper.toResponseDto(vet);

        assertThat(dto.getSpecialties()).isEmpty();
    }

    @Test
    void shouldSortSpecialtiesById() {
        Specialty surgery = new Specialty("surgery");
        surgery.setId(2);
        Specialty radiology = new Specialty("radiology");
        radiology.setId(1);

        Vet vet = new Vet("James", "Carter");
        vet.setId(1);
        Set<Specialty> specs = new HashSet<>();
        specs.add(surgery);
        specs.add(radiology);
        vet.setSpecialties(specs);

        VetResponseDto dto = vetMapper.toResponseDto(vet);

        assertThat(dto.getSpecialties()).hasSize(2);
        assertThat(dto.getSpecialties().get(0).getId()).isEqualTo(1);
        assertThat(dto.getSpecialties().get(1).getId()).isEqualTo(2);
    }

    @Test
    void shouldMapSpecialtyToResponseDto() {
        Specialty specialty = new Specialty("radiology");
        specialty.setId(1);

        SpecialtyResponseDto dto = vetMapper.toSpecialtyResponseDto(specialty);

        assertThat(dto.getId()).isEqualTo(1);
        assertThat(dto.getName()).isEqualTo("radiology");
    }
}
