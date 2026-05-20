package com.petclinic.vet.mapper;

import com.petclinic.vet.dto.VetRequestDto;
import com.petclinic.vet.dto.VetResponseDto;
import com.petclinic.vet.entity.Specialty;
import com.petclinic.vet.entity.Vet;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for the MapStruct-generated VetMapper implementation.
 * Uses @SpringBootTest to load the Spring context and inject the generated mapper.
 */
@SpringBootTest
@ActiveProfiles("test")
class VetMapperTest {

    @Autowired
    private VetMapper vetMapper;

    @Test
    void toEntity_shouldMapRequestDtoToEntity() {
        VetRequestDto dto = new VetRequestDto("James", "Carter", List.of());

        Vet entity = vetMapper.toEntity(dto);

        assertThat(entity.getFirstName()).isEqualTo("James");
        assertThat(entity.getLastName()).isEqualTo("Carter");
        assertThat(entity.getId()).isNull();
    }

    @Test
    void toResponseDto_shouldMapEntityToResponseDto() {
        Specialty radiology = new Specialty();
        radiology.setId(1);
        radiology.setName("radiology");

        Vet entity = new Vet();
        entity.setId(1);
        entity.setFirstName("James");
        entity.setLastName("Carter");
        entity.setSpecialties(Set.of(radiology));

        VetResponseDto dto = vetMapper.toResponseDto(entity);

        assertThat(dto.getId()).isEqualTo(1);
        assertThat(dto.getFirstName()).isEqualTo("James");
        assertThat(dto.getLastName()).isEqualTo("Carter");
        assertThat(dto.getSpecialties()).hasSize(1);
        assertThat(dto.getSpecialties().get(0).getName()).isEqualTo("radiology");
    }

    @Test
    void toResponseDtos_shouldMapListOfEntitiesToDtos() {
        Vet v1 = new Vet();
        v1.setId(1);
        v1.setFirstName("James");
        v1.setLastName("Carter");

        Vet v2 = new Vet();
        v2.setId(2);
        v2.setFirstName("Helen");
        v2.setLastName("Leary");

        List<VetResponseDto> dtos = vetMapper.toResponseDtos(List.of(v1, v2));

        assertThat(dtos).hasSize(2);
        assertThat(dtos.get(0).getFirstName()).isEqualTo("James");
        assertThat(dtos.get(1).getFirstName()).isEqualTo("Helen");
    }

    @Test
    void toResponseDtos_shouldReturnNullForNullInput() {
        List<VetResponseDto> dtos = vetMapper.toResponseDtos(null);
        assertThat(dtos).isNull();
    }

    @Test
    void toResponseDto_withEmptySpecialties_shouldReturnEmptyList() {
        Vet entity = new Vet();
        entity.setId(1);
        entity.setFirstName("James");
        entity.setLastName("Carter");

        VetResponseDto dto = vetMapper.toResponseDto(entity);

        assertThat(dto.getSpecialties()).isEmpty();
    }

    @Test
    void toResponseDto_withMultipleSpecialties_shouldSortAlphabetically() {
        Specialty surgery = new Specialty();
        surgery.setId(1);
        surgery.setName("surgery");
        Specialty dentistry = new Specialty();
        dentistry.setId(2);
        dentistry.setName("dentistry");

        Vet entity = new Vet();
        entity.setId(1);
        entity.setFirstName("Linda");
        entity.setLastName("Douglas");
        entity.setSpecialties(Set.of(surgery, dentistry));

        VetResponseDto dto = vetMapper.toResponseDto(entity);

        assertThat(dto.getSpecialties()).hasSize(2);
        // Sorted alphabetically via getSortedSpecialties()
        assertThat(dto.getSpecialties().get(0).getName()).isEqualTo("dentistry");
        assertThat(dto.getSpecialties().get(1).getName()).isEqualTo("surgery");
    }
}
