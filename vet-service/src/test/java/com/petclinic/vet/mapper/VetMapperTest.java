package com.petclinic.vet.mapper;

import com.petclinic.vet.dto.SpecialtyResponseDto;
import com.petclinic.vet.dto.VetRequestDto;
import com.petclinic.vet.dto.VetResponseDto;
import com.petclinic.vet.entity.Specialty;
import com.petclinic.vet.entity.Vet;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class VetMapperTest {

    private VetMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new VetMapperImpl();
    }

    @Test
    void toResponseDto_mapsCorrectly() {
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
    void toResponseDto_nullInput() {
        assertThat(mapper.toResponseDto(null)).isNull();
    }

    @Test
    void toResponseDto_emptySpecialties() {
        Vet vet = new Vet();
        vet.setId(1);
        vet.setFirstName("James");
        vet.setLastName("Carter");
        vet.setSpecialties(new HashSet<>());

        VetResponseDto dto = mapper.toResponseDto(vet);

        assertThat(dto.specialties()).isEmpty();
    }

    @Test
    void toResponseDto_nullSpecialties() {
        Vet vet = new Vet();
        vet.setId(1);
        vet.setFirstName("James");
        vet.setLastName("Carter");
        vet.setSpecialties(null);

        VetResponseDto dto = mapper.toResponseDto(vet);

        assertThat(dto.specialties()).isEmpty();
    }

    @Test
    void toResponseDtos_mapsCorrectly() {
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

        List<VetResponseDto> dtos = mapper.toResponseDtos(List.of(vet1, vet2));

        assertThat(dtos).hasSize(2);
    }

    @Test
    void toResponseDtos_nullInput() {
        assertThat(mapper.toResponseDtos(null)).isNull();
    }

    @Test
    void toEntity_mapsCorrectly() {
        VetRequestDto dto = new VetRequestDto("James", "Carter", List.of());

        Vet entity = mapper.toEntity(dto);

        assertThat(entity.getId()).isNull();
        assertThat(entity.getFirstName()).isEqualTo("James");
        assertThat(entity.getLastName()).isEqualTo("Carter");
    }

    @Test
    void toEntity_nullInput() {
        assertThat(mapper.toEntity(null)).isNull();
    }

    @Test
    void mapSpecialties_sortsAlphabetically() {
        Specialty surgery = new Specialty();
        surgery.setId(2);
        surgery.setName("surgery");
        Specialty dentistry = new Specialty();
        dentistry.setId(3);
        dentistry.setName("dentistry");

        List<SpecialtyResponseDto> result = mapper.mapSpecialties(Set.of(surgery, dentistry));

        assertThat(result).hasSize(2);
        assertThat(result.get(0).name()).isEqualTo("dentistry");
        assertThat(result.get(1).name()).isEqualTo("surgery");
    }

    @Test
    void mapSpecialties_nullInput() {
        assertThat(mapper.mapSpecialties(null)).isEmpty();
    }
}
