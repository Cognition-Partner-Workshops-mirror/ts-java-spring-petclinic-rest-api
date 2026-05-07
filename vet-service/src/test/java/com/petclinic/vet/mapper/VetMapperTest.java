package com.petclinic.vet.mapper;

import com.petclinic.vet.dto.VetResponseDto;
import com.petclinic.vet.entity.Specialty;
import com.petclinic.vet.entity.Vet;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
class VetMapperTest {

    @Autowired
    private VetMapper vetMapper;

    @Test
    void toResponseDto_mapsAllFieldsIncludingSpecialties() {
        Specialty radiology = new Specialty();
        radiology.setId(1);
        radiology.setName("radiology");

        Vet vet = new Vet();
        vet.setId(1);
        vet.setFirstName("James");
        vet.setLastName("Carter");
        vet.setSpecialties(new HashSet<>(Arrays.asList(radiology)));

        VetResponseDto dto = vetMapper.toResponseDto(vet);

        assertThat(dto.getId()).isEqualTo(1);
        assertThat(dto.getFirstName()).isEqualTo("James");
        assertThat(dto.getLastName()).isEqualTo("Carter");
        assertThat(dto.getSpecialties()).hasSize(1);
        assertThat(dto.getSpecialties().get(0).getName()).isEqualTo("radiology");
    }

    @Test
    void toResponseDto_withEmptySpecialties_returnsEmptyList() {
        Vet vet = new Vet();
        vet.setId(1);
        vet.setFirstName("James");
        vet.setLastName("Carter");

        VetResponseDto dto = vetMapper.toResponseDto(vet);

        assertThat(dto.getSpecialties()).isEmpty();
    }

    @Test
    void toResponseDtos_mapsList() {
        Vet v1 = new Vet();
        v1.setId(1);
        v1.setFirstName("James");
        v1.setLastName("Carter");

        Vet v2 = new Vet();
        v2.setId(2);
        v2.setFirstName("Helen");
        v2.setLastName("Leary");

        List<VetResponseDto> dtos = vetMapper.toResponseDtos(Arrays.asList(v1, v2));

        assertThat(dtos).hasSize(2);
        assertThat(dtos.get(0).getFirstName()).isEqualTo("James");
        assertThat(dtos.get(1).getFirstName()).isEqualTo("Helen");
    }

    @Test
    void toResponseDto_nullVet_returnsNull() {
        assertThat(vetMapper.toResponseDto(null)).isNull();
    }

    @Test
    void toResponseDtos_nullList_returnsNull() {
        assertThat(vetMapper.toResponseDtos(null)).isNull();
    }
}
