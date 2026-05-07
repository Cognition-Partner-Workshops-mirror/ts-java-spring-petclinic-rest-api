package com.petclinic.vet.service;

import com.petclinic.vet.dto.SpecialtyRequestDto;
import com.petclinic.vet.dto.SpecialtyResponseDto;
import com.petclinic.vet.dto.VetRequestDto;
import com.petclinic.vet.dto.VetResponseDto;
import com.petclinic.vet.entity.Specialty;
import com.petclinic.vet.entity.Vet;
import com.petclinic.vet.mapper.SpecialtyMapper;
import com.petclinic.vet.mapper.VetMapper;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class MapperTest {

    private final SpecialtyMapper specialtyMapper = new SpecialtyMapper();
    private final VetMapper vetMapper = new VetMapper(specialtyMapper);

    @Test
    void specialtyMapper_toResponseDto() {
        Specialty entity = new Specialty();
        entity.setSpecialtyId(1);
        entity.setName("radiology");

        SpecialtyResponseDto dto = specialtyMapper.toResponseDto(entity);

        assertThat(dto.getId()).isEqualTo(1);
        assertThat(dto.getName()).isEqualTo("radiology");
    }

    @Test
    void specialtyMapper_toEntity() {
        SpecialtyRequestDto dto = new SpecialtyRequestDto("oncology");

        Specialty entity = specialtyMapper.toEntity(dto);

        assertThat(entity.getSpecialtyId()).isNull();
        assertThat(entity.getName()).isEqualTo("oncology");
    }

    @Test
    void specialtyMapper_updateEntity() {
        Specialty entity = new Specialty();
        entity.setSpecialtyId(1);
        entity.setName("old");

        SpecialtyRequestDto dto = new SpecialtyRequestDto("new");
        specialtyMapper.updateEntity(entity, dto);

        assertThat(entity.getName()).isEqualTo("new");
        assertThat(entity.getSpecialtyId()).isEqualTo(1);
    }

    @Test
    void vetMapper_toResponseDto() {
        Specialty radiology = new Specialty();
        radiology.setSpecialtyId(1);
        radiology.setName("radiology");

        Specialty surgery = new Specialty();
        surgery.setSpecialtyId(2);
        surgery.setName("surgery");

        Vet vet = new Vet();
        vet.setVetId(1);
        vet.setFirstName("James");
        vet.setLastName("Carter");
        vet.setSpecialties(new HashSet<>(Set.of(radiology, surgery)));

        VetResponseDto dto = vetMapper.toResponseDto(vet);

        assertThat(dto.getId()).isEqualTo(1);
        assertThat(dto.getFirstName()).isEqualTo("James");
        assertThat(dto.getLastName()).isEqualTo("Carter");
        assertThat(dto.getSpecialties()).hasSize(2);
        assertThat(dto.getSpecialties().get(0).getName()).isEqualTo("radiology");
        assertThat(dto.getSpecialties().get(1).getName()).isEqualTo("surgery");
    }

    @Test
    void vetMapper_toEntity() {
        Specialty radiology = new Specialty();
        radiology.setSpecialtyId(1);
        radiology.setName("radiology");

        VetRequestDto dto = new VetRequestDto("John", "Doe",
            List.of(new SpecialtyResponseDto(1, "radiology")));

        Vet entity = vetMapper.toEntity(dto, new HashSet<>(Set.of(radiology)));

        assertThat(entity.getFirstName()).isEqualTo("John");
        assertThat(entity.getLastName()).isEqualTo("Doe");
        assertThat(entity.getSpecialties()).hasSize(1);
    }

    @Test
    void vetMapper_updateEntity() {
        Vet entity = new Vet();
        entity.setVetId(1);
        entity.setFirstName("Old");
        entity.setLastName("Name");
        entity.setSpecialties(new HashSet<>());

        Specialty surgery = new Specialty();
        surgery.setSpecialtyId(2);
        surgery.setName("surgery");

        VetRequestDto dto = new VetRequestDto("New", "Name",
            List.of(new SpecialtyResponseDto(2, "surgery")));

        vetMapper.updateEntity(entity, dto, new HashSet<>(Set.of(surgery)));

        assertThat(entity.getFirstName()).isEqualTo("New");
        assertThat(entity.getSpecialties()).hasSize(1);
    }

    @Test
    void vetMapper_toResponseDto_emptySpecialties() {
        Vet vet = new Vet();
        vet.setVetId(1);
        vet.setFirstName("James");
        vet.setLastName("Carter");
        vet.setSpecialties(new HashSet<>());

        VetResponseDto dto = vetMapper.toResponseDto(vet);

        assertThat(dto.getSpecialties()).isEmpty();
    }
}
