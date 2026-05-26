package com.petclinic.vet.mapper;

import com.petclinic.vet.dto.SpecialtyResponseDto;
import com.petclinic.vet.dto.VetResponseDto;
import com.petclinic.vet.entity.Specialty;
import com.petclinic.vet.entity.Vet;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration tests for {@link VetMapper} to verify MapStruct-generated code works correctly.
 */
@SpringBootTest
class VetMapperTest {

    @Autowired
    private VetMapper vetMapper;

    @Test
    @DisplayName("toResponseDto maps vet entity with specialties correctly")
    void toResponseDto_mapsCorrectly() {
        // Create test specialties
        Specialty radiology = new Specialty();
        radiology.setId(1);
        radiology.setName("radiology");

        Specialty surgery = new Specialty();
        surgery.setId(2);
        surgery.setName("surgery");

        // Create test vet
        Vet vet = new Vet();
        vet.setId(1);
        vet.setFirstName("James");
        vet.setLastName("Carter");
        vet.setSpecialties(new HashSet<>(Set.of(radiology, surgery)));

        VetResponseDto dto = vetMapper.toResponseDto(vet);

        assertThat(dto.getId()).isEqualTo(1);
        assertThat(dto.getFirstName()).isEqualTo("James");
        assertThat(dto.getLastName()).isEqualTo("Carter");
        assertThat(dto.getSpecialties()).hasSize(2);
        // Verify sorted by name
        assertThat(dto.getSpecialties().get(0).getName()).isEqualTo("radiology");
        assertThat(dto.getSpecialties().get(1).getName()).isEqualTo("surgery");
    }

    @Test
    @DisplayName("toResponseDto handles empty specialties set")
    void toResponseDto_emptySpecialties() {
        Vet vet = new Vet();
        vet.setId(1);
        vet.setFirstName("James");
        vet.setLastName("Carter");
        vet.setSpecialties(new HashSet<>());

        VetResponseDto dto = vetMapper.toResponseDto(vet);

        assertThat(dto.getSpecialties()).isEmpty();
    }

    @Test
    @DisplayName("toResponseDto handles null specialties set")
    void toResponseDto_nullSpecialties() {
        Vet vet = new Vet();
        vet.setId(1);
        vet.setFirstName("James");
        vet.setLastName("Carter");
        vet.setSpecialties(null);

        VetResponseDto dto = vetMapper.toResponseDto(vet);

        assertThat(dto.getSpecialties()).isEmpty();
    }

    @Test
    @DisplayName("mapSpecialties returns sorted list")
    void mapSpecialties_sortsByName() {
        Specialty z = new Specialty();
        z.setId(1);
        z.setName("zoology");

        Specialty a = new Specialty();
        a.setId(2);
        a.setName("allergology");

        List<SpecialtyResponseDto> result = vetMapper.mapSpecialties(new HashSet<>(Set.of(z, a)));

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getName()).isEqualTo("allergology");
        assertThat(result.get(1).getName()).isEqualTo("zoology");
    }
}
