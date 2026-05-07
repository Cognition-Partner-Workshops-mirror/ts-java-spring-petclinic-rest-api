package com.petclinic.vet.mapper;

import com.petclinic.vet.dto.SpecialtyResponseDto;
import com.petclinic.vet.dto.VetRequestDto;
import com.petclinic.vet.dto.VetResponseDto;
import com.petclinic.vet.entity.Specialty;
import com.petclinic.vet.entity.Vet;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class VetMapperTest {

    @Autowired
    private VetMapper vetMapper;

    @Test
    void toEntity_mapsFieldsCorrectly() {
        VetRequestDto dto = new VetRequestDto("James", "Carter", new ArrayList<>());

        Vet entity = vetMapper.toEntity(dto);

        assertThat(entity.getFirstName()).isEqualTo("James");
        assertThat(entity.getLastName()).isEqualTo("Carter");
        assertThat(entity.getId()).isNull();
        assertThat(entity.getSpecialties()).isEmpty();
    }

    @Test
    void toEntity_withSpecialties_mapsCorrectly() {
        List<SpecialtyResponseDto> specs = List.of(
            new SpecialtyResponseDto(1, "radiology"),
            new SpecialtyResponseDto(2, "surgery")
        );
        VetRequestDto dto = new VetRequestDto("Helen", "Leary", specs);

        Vet entity = vetMapper.toEntity(dto);

        assertThat(entity.getFirstName()).isEqualTo("Helen");
        assertThat(entity.getSpecialties()).hasSize(2);
    }

    @Test
    void toResponseDto_mapsFieldsCorrectly() {
        Vet vet = new Vet("James", "Carter");
        vet.setId(1);

        VetResponseDto dto = vetMapper.toResponseDto(vet);

        assertThat(dto.getId()).isEqualTo(1);
        assertThat(dto.getFirstName()).isEqualTo("James");
        assertThat(dto.getLastName()).isEqualTo("Carter");
        assertThat(dto.getSpecialties()).isEmpty();
    }

    @Test
    void toResponseDto_withSpecialties_mapsCorrectly() {
        Vet vet = new Vet("Helen", "Leary");
        vet.setId(2);
        Specialty radiology = new Specialty("radiology");
        radiology.setId(1);
        vet.addSpecialty(radiology);

        VetResponseDto dto = vetMapper.toResponseDto(vet);

        assertThat(dto.getSpecialties()).hasSize(1);
        assertThat(dto.getSpecialties().get(0).getName()).isEqualTo("radiology");
    }

    @Test
    void toResponseDtos_mapsList() {
        Vet v1 = new Vet("James", "Carter");
        v1.setId(1);
        Vet v2 = new Vet("Helen", "Leary");
        v2.setId(2);

        List<VetResponseDto> dtos = vetMapper.toResponseDtos(List.of(v1, v2));

        assertThat(dtos).hasSize(2);
        assertThat(dtos.get(0).getFirstName()).isEqualTo("James");
        assertThat(dtos.get(1).getFirstName()).isEqualTo("Helen");
    }

    @Test
    void toResponseDtos_emptyList_returnsEmpty() {
        List<VetResponseDto> dtos = vetMapper.toResponseDtos(new ArrayList<>());
        assertThat(dtos).isEmpty();
    }

    @Test
    void dtoListToEntitySet_nullInput_returnsEmptySet() {
        Set<Specialty> result = vetMapper.dtoListToEntitySet(null);
        assertThat(result).isEmpty();
    }

    @Test
    void dtoListToEntitySet_emptyList_returnsEmptySet() {
        Set<Specialty> result = vetMapper.dtoListToEntitySet(new ArrayList<>());
        assertThat(result).isEmpty();
    }

    @Test
    void dtoListToEntitySet_withDtos_convertsCorrectly() {
        List<SpecialtyResponseDto> dtos = List.of(
            new SpecialtyResponseDto(1, "radiology"),
            new SpecialtyResponseDto(2, "surgery")
        );

        Set<Specialty> result = vetMapper.dtoListToEntitySet(dtos);

        assertThat(result).hasSize(2);
    }

    @Test
    void entitySetToDtoList_nullInput_returnsEmptyList() {
        List<SpecialtyResponseDto> result = vetMapper.entitySetToDtoList(null);
        assertThat(result).isEmpty();
    }

    @Test
    void entitySetToDtoList_emptySet_returnsEmptyList() {
        List<SpecialtyResponseDto> result = vetMapper.entitySetToDtoList(new HashSet<>());
        assertThat(result).isEmpty();
    }

    @Test
    void entitySetToDtoList_sortsByNameAlphabetically() {
        Specialty surgery = new Specialty("surgery");
        surgery.setId(1);
        Specialty dentistry = new Specialty("dentistry");
        dentistry.setId(2);
        Specialty radiology = new Specialty("radiology");
        radiology.setId(3);

        Set<Specialty> entities = new HashSet<>();
        entities.add(surgery);
        entities.add(dentistry);
        entities.add(radiology);

        List<SpecialtyResponseDto> result = vetMapper.entitySetToDtoList(entities);

        assertThat(result).hasSize(3);
        assertThat(result.get(0).getName()).isEqualTo("dentistry");
        assertThat(result.get(1).getName()).isEqualTo("radiology");
        assertThat(result.get(2).getName()).isEqualTo("surgery");
    }

    @Test
    void entitySetToDtoList_handlesNullNames() {
        Specialty withName = new Specialty("radiology");
        withName.setId(1);
        Specialty withoutName = new Specialty();
        withoutName.setId(2);

        Set<Specialty> entities = new HashSet<>();
        entities.add(withName);
        entities.add(withoutName);

        List<SpecialtyResponseDto> result = vetMapper.entitySetToDtoList(entities);

        assertThat(result).hasSize(2);
    }
}
