package com.petclinic.vet.mapper;

import com.petclinic.vet.dto.SpecialtyRequestDto;
import com.petclinic.vet.dto.SpecialtyResponseDto;
import com.petclinic.vet.dto.VetRequestDto;
import com.petclinic.vet.dto.VetResponseDto;
import com.petclinic.vet.entity.Specialty;
import com.petclinic.vet.entity.Vet;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
class MapperTest {

    @Autowired
    private SpecialtyMapper specialtyMapper;

    @Autowired
    private VetMapper vetMapper;

    @Test
    void specialtyMapper_toEntity() {
        SpecialtyRequestDto dto = new SpecialtyRequestDto("radiology");
        Specialty entity = specialtyMapper.toEntity(dto);
        assertThat(entity.getId()).isNull();
        assertThat(entity.getName()).isEqualTo("radiology");
    }

    @Test
    void specialtyMapper_toResponseDto() {
        Specialty entity = new Specialty();
        entity.setId(1);
        entity.setName("radiology");
        SpecialtyResponseDto dto = specialtyMapper.toResponseDto(entity);
        assertThat(dto.getId()).isEqualTo(1);
        assertThat(dto.getName()).isEqualTo("radiology");
    }

    @Test
    void specialtyMapper_toResponseDtos() {
        Specialty s1 = new Specialty();
        s1.setId(1);
        s1.setName("radiology");
        Specialty s2 = new Specialty();
        s2.setId(2);
        s2.setName("surgery");
        List<SpecialtyResponseDto> dtos = specialtyMapper.toResponseDtos(List.of(s1, s2));
        assertThat(dtos).hasSize(2);
    }

    @Test
    void specialtyMapper_updateEntity() {
        Specialty entity = new Specialty();
        entity.setId(1);
        entity.setName("radiology");
        SpecialtyRequestDto dto = new SpecialtyRequestDto("updated");
        specialtyMapper.updateEntity(dto, entity);
        assertThat(entity.getName()).isEqualTo("updated");
        assertThat(entity.getId()).isEqualTo(1);
    }

    @Test
    void vetMapper_toEntity() {
        VetRequestDto dto = new VetRequestDto("James", "Carter", List.of());
        Vet entity = vetMapper.toEntity(dto);
        assertThat(entity.getId()).isNull();
        assertThat(entity.getFirstName()).isEqualTo("James");
        assertThat(entity.getLastName()).isEqualTo("Carter");
    }

    @Test
    void vetMapper_toResponseDto() {
        Specialty spec = new Specialty();
        spec.setId(1);
        spec.setName("radiology");
        Vet vet = new Vet();
        vet.setId(1);
        vet.setFirstName("James");
        vet.setLastName("Carter");
        vet.setSpecialties(Set.of(spec));
        VetResponseDto dto = vetMapper.toResponseDto(vet);
        assertThat(dto.getId()).isEqualTo(1);
        assertThat(dto.getFirstName()).isEqualTo("James");
        assertThat(dto.getSpecialties()).hasSize(1);
        assertThat(dto.getSpecialties().get(0).getName()).isEqualTo("radiology");
    }

    @Test
    void vetMapper_toResponseDto_nullSpecialties() {
        Vet vet = new Vet();
        vet.setId(1);
        vet.setFirstName("James");
        vet.setLastName("Carter");
        vet.setSpecialties(null);
        VetResponseDto dto = vetMapper.toResponseDto(vet);
        assertThat(dto.getSpecialties()).isEmpty();
    }

    @Test
    void vetMapper_toResponseDtos() {
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
    }

    @Test
    void vetMapper_mapSortedSpecialties_sortsByName() {
        Specialty surgery = new Specialty();
        surgery.setId(1);
        surgery.setName("surgery");
        Specialty dentistry = new Specialty();
        dentistry.setId(2);
        dentistry.setName("dentistry");
        Set<Specialty> specialties = new HashSet<>();
        specialties.add(surgery);
        specialties.add(dentistry);
        List<SpecialtyResponseDto> result = vetMapper.mapSortedSpecialties(specialties);
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getName()).isEqualTo("dentistry");
        assertThat(result.get(1).getName()).isEqualTo("surgery");
    }

    @Test
    void vetMapper_mapSortedSpecialties_nullReturnsEmpty() {
        List<SpecialtyResponseDto> result = vetMapper.mapSortedSpecialties(null);
        assertThat(result).isEmpty();
    }
}
