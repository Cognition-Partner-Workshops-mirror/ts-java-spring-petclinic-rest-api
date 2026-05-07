package com.petclinic.vet.mapper;

import com.petclinic.vet.dto.SpecialtyResponseDto;
import com.petclinic.vet.dto.VetRequestDto;
import com.petclinic.vet.dto.VetResponseDto;
import com.petclinic.vet.entity.Specialty;
import com.petclinic.vet.entity.Vet;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class VetMapperTest {

    @Autowired
    private VetMapper vetMapper;

    @Test
    void toResponseDto_mapsFieldsAndSpecialties() {
        Specialty s = new Specialty();
        s.setId(1);
        s.setName("surgery");

        Vet vet = new Vet();
        vet.setId(10);
        vet.setFirstName("James");
        vet.setLastName("Carter");
        vet.addSpecialty(s);

        VetResponseDto dto = vetMapper.toResponseDto(vet);

        assertThat(dto.getId()).isEqualTo(10);
        assertThat(dto.getFirstName()).isEqualTo("James");
        assertThat(dto.getLastName()).isEqualTo("Carter");
        assertThat(dto.getSpecialties()).hasSize(1);
        assertThat(dto.getSpecialties().get(0).getName()).isEqualTo("surgery");
    }

    @Test
    void toResponseDtos_mapsList() {
        Vet vet = new Vet();
        vet.setId(1);
        vet.setFirstName("A");
        vet.setLastName("B");

        List<VetResponseDto> dtos = vetMapper.toResponseDtos(List.of(vet));
        assertThat(dtos).hasSize(1);
    }

    @Test
    void toEntity_mapsRequestDto() {
        VetRequestDto req = new VetRequestDto();
        req.setFirstName("Linda");
        req.setLastName("Douglas");

        Vet vet = vetMapper.toEntity(req);

        assertThat(vet.getFirstName()).isEqualTo("Linda");
        assertThat(vet.getLastName()).isEqualTo("Douglas");
        assertThat(vet.getId()).isNull();
        assertThat(vet.getSpecialties()).isEmpty();
    }
}
