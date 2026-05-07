package com.petclinic.vet.mapper;

import com.petclinic.vet.dto.request.VetRequestDto;
import com.petclinic.vet.dto.response.VetResponseDto;
import com.petclinic.vet.entity.Specialty;
import com.petclinic.vet.entity.Vet;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.Instant;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class VetMapperTest {

    @Autowired
    private VetMapper vetMapper;

    @Test
    void toResponseDto_mapsAllFields() {
        Specialty specialty = new Specialty();
        specialty.setId(1);
        specialty.setName("radiology");

        Vet vet = new Vet();
        vet.setId(10);
        vet.setFirstName("James");
        vet.setLastName("Carter");
        vet.setSpecialties(Set.of(specialty));

        VetResponseDto dto = vetMapper.toResponseDto(vet);

        assertThat(dto.id()).isEqualTo(10);
        assertThat(dto.firstName()).isEqualTo("James");
        assertThat(dto.lastName()).isEqualTo("Carter");
        assertThat(dto.specialties()).hasSize(1);
        assertThat(dto.specialties().get(0).name()).isEqualTo("radiology");
    }

    @Test
    void toResponseDto_handlesNullInput() {
        VetResponseDto dto = vetMapper.toResponseDto(null);
        assertThat(dto).isNull();
    }

    @Test
    void toResponseDto_handlesEmptySpecialties() {
        Vet vet = new Vet();
        vet.setId(1);
        vet.setFirstName("James");
        vet.setLastName("Carter");
        vet.setSpecialties(new HashSet<>());

        VetResponseDto dto = vetMapper.toResponseDto(vet);

        assertThat(dto.specialties()).isEmpty();
    }

    @Test
    void toResponseDtos_mapsCollection() {
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

        List<VetResponseDto> dtos = vetMapper.toResponseDtos(List.of(vet1, vet2));

        assertThat(dtos).hasSize(2);
        assertThat(dtos.get(0).firstName()).isEqualTo("James");
        assertThat(dtos.get(1).firstName()).isEqualTo("Helen");
    }

    @Test
    void toResponseDtos_handlesNullInput() {
        List<VetResponseDto> dtos = vetMapper.toResponseDtos(null);
        assertThat(dtos).isNull();
    }

    @Test
    void toEntity_mapsRequestFields() {
        VetRequestDto request = new VetRequestDto("James", "Carter", List.of());
        Vet vet = vetMapper.toEntity(request);

        assertThat(vet.getFirstName()).isEqualTo("James");
        assertThat(vet.getLastName()).isEqualTo("Carter");
        assertThat(vet.getId()).isNull();
    }

    @Test
    void toEntity_handlesNullInput() {
        Vet vet = vetMapper.toEntity(null);
        assertThat(vet).isNull();
    }
}
