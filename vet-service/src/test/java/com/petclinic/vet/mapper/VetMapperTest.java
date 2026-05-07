package com.petclinic.vet.mapper;

import com.petclinic.vet.dto.SpecialtyResponseDto;
import com.petclinic.vet.dto.VetRequestDto;
import com.petclinic.vet.dto.VetResponseDto;
import com.petclinic.vet.entity.Specialty;
import com.petclinic.vet.entity.Vet;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
class VetMapperTest {

    @Autowired
    private VetMapper vetMapper;

    @Test
    void toResponseDto_mapsCorrectly() {
        Specialty specialty = new Specialty();
        specialty.setId(1);
        specialty.setName("radiology");

        Vet vet = new Vet();
        vet.setId(1);
        vet.setFirstName("James");
        vet.setLastName("Carter");
        vet.setSpecialties(List.of(specialty));

        VetResponseDto dto = vetMapper.toResponseDto(vet);

        assertThat(dto.getId()).isEqualTo(1);
        assertThat(dto.getFirstName()).isEqualTo("James");
        assertThat(dto.getLastName()).isEqualTo("Carter");
        assertThat(dto.getSpecialties()).hasSize(1);
        assertThat(dto.getSpecialties().get(0).getName()).isEqualTo("radiology");
    }

    @Test
    void toEntity_mapsCorrectly() {
        VetRequestDto dto = new VetRequestDto("James", "Carter", new ArrayList<>());

        Vet vet = vetMapper.toEntity(dto);

        assertThat(vet.getFirstName()).isEqualTo("James");
        assertThat(vet.getLastName()).isEqualTo("Carter");
        assertThat(vet.getId()).isNull();
    }

    @Test
    void updateEntity_mapsCorrectly() {
        Vet vet = new Vet();
        vet.setId(1);
        vet.setFirstName("James");
        vet.setLastName("Carter");

        VetRequestDto dto = new VetRequestDto("Helen", "Leary", new ArrayList<>());

        vetMapper.updateEntity(dto, vet);

        assertThat(vet.getId()).isEqualTo(1);
        assertThat(vet.getFirstName()).isEqualTo("Helen");
        assertThat(vet.getLastName()).isEqualTo("Leary");
    }

    @Test
    void specialtyToDto_mapsCorrectly() {
        Specialty specialty = new Specialty();
        specialty.setId(3);
        specialty.setName("dentistry");

        SpecialtyResponseDto dto = vetMapper.specialtyToDto(specialty);

        assertThat(dto.getId()).isEqualTo(3);
        assertThat(dto.getName()).isEqualTo("dentistry");
    }
}
