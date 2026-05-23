package com.petclinic.vet.mapper;

import com.petclinic.vet.dto.VetRequestDto;
import com.petclinic.vet.dto.VetResponseDto;
import com.petclinic.vet.entity.Specialty;
import com.petclinic.vet.entity.Vet;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for VetMapper verifying entity-to-DTO and DTO-to-entity conversions.
 */
@SpringBootTest
class VetMapperTest {

    @Autowired
    private VetMapper vetMapper;

    @Test
    void toEntity_mapsFieldsCorrectly() {
        VetRequestDto dto = new VetRequestDto("James", "Carter", Collections.emptyList());
        Vet vet = vetMapper.toEntity(dto);
        assertThat(vet.getFirstName()).isEqualTo("James");
        assertThat(vet.getLastName()).isEqualTo("Carter");
        // Specialties are resolved in the service layer, so mapper ignores them
        assertThat(vet.getId()).isNull();
    }

    @Test
    void toResponseDto_mapsAllFields() {
        Vet vet = new Vet(1, "James", "Carter");
        Specialty radiology = new Specialty(1, "radiology");
        vet.setSpecialties(Set.of(radiology));
        LocalDateTime now = LocalDateTime.now();
        vet.setCreatedAt(now);
        vet.setUpdatedAt(now);

        VetResponseDto dto = vetMapper.toResponseDto(vet);
        assertThat(dto.getId()).isEqualTo(1);
        assertThat(dto.getFirstName()).isEqualTo("James");
        assertThat(dto.getLastName()).isEqualTo("Carter");
        assertThat(dto.getSpecialties()).hasSize(1);
        assertThat(dto.getSpecialties().get(0).getName()).isEqualTo("radiology");
        assertThat(dto.getCreatedAt()).isEqualTo(now);
        assertThat(dto.getUpdatedAt()).isEqualTo(now);
    }

    @Test
    void toResponseDtos_mapsList() {
        Vet vet1 = new Vet(1, "James", "Carter");
        Vet vet2 = new Vet(2, "Helen", "Leary");
        List<VetResponseDto> dtos = vetMapper.toResponseDtos(List.of(vet1, vet2));
        assertThat(dtos).hasSize(2);
        assertThat(dtos.get(0).getFirstName()).isEqualTo("James");
        assertThat(dtos.get(1).getFirstName()).isEqualTo("Helen");
    }

    @Test
    void toEntity_nullDto_returnsNull() {
        assertThat(vetMapper.toEntity(null)).isNull();
    }

    @Test
    void toResponseDto_nullEntity_returnsNull() {
        assertThat(vetMapper.toResponseDto(null)).isNull();
    }

    @Test
    void toResponseDtos_nullCollection_returnsNull() {
        assertThat(vetMapper.toResponseDtos(null)).isNull();
    }
}
