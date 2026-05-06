package com.petclinic.vet.mapper;

import com.petclinic.vet.dto.SpecialtyDto;
import com.petclinic.vet.dto.VetDto;
import com.petclinic.vet.entity.Specialty;
import com.petclinic.vet.entity.Vet;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
class VetMapperTest {

    @Autowired
    private VetMapper vetMapper;

    @Autowired
    private SpecialtyMapper specialtyMapper;

    @Test
    void toDto_mapsVetCorrectly() {
        Specialty spec = new Specialty(1, "radiology");
        Vet vet = new Vet(1, "James", "Carter");
        vet.setSpecialties(Set.of(spec));

        VetDto dto = vetMapper.toDto(vet);

        assertThat(dto.getId()).isEqualTo(1);
        assertThat(dto.getFirstName()).isEqualTo("James");
        assertThat(dto.getLastName()).isEqualTo("Carter");
        assertThat(dto.getSpecialties()).hasSize(1);
    }

    @Test
    void toDto_handlesEmptySpecialties() {
        Vet vet = new Vet(1, "James", "Carter");
        vet.setSpecialties(new HashSet<>());

        VetDto dto = vetMapper.toDto(vet);

        assertThat(dto.getSpecialties()).isEmpty();
    }

    @Test
    void toDto_handlesNullSpecialties() {
        Vet vet = new Vet(1, "James", "Carter");
        vet.setSpecialties(null);

        VetDto dto = vetMapper.toDto(vet);

        assertThat(dto.getSpecialties()).isEmpty();
    }

    @Test
    void toDtoList_mapsList() {
        Vet vet1 = new Vet(1, "James", "Carter");
        vet1.setSpecialties(new HashSet<>());
        Vet vet2 = new Vet(2, "Helen", "Leary");
        vet2.setSpecialties(new HashSet<>());

        List<VetDto> dtos = vetMapper.toDtoList(List.of(vet1, vet2));

        assertThat(dtos).hasSize(2);
    }

    @Test
    void toEntity_mapsFieldsIgnoringId() {
        VetDto dto = new VetDto(99, "James", "Carter", List.of());

        Vet entity = vetMapper.toEntity(dto);

        assertThat(entity.getId()).isNull();
        assertThat(entity.getFirstName()).isEqualTo("James");
        assertThat(entity.getLastName()).isEqualTo("Carter");
    }

    @Test
    void specialtyMapper_toDto() {
        Specialty entity = new Specialty(1, "radiology");

        SpecialtyDto dto = specialtyMapper.toDto(entity);

        assertThat(dto.getId()).isEqualTo(1);
        assertThat(dto.getName()).isEqualTo("radiology");
    }

    @Test
    void specialtyMapper_toEntity() {
        SpecialtyDto dto = new SpecialtyDto(99, "surgery");

        Specialty entity = specialtyMapper.toEntity(dto);

        assertThat(entity.getId()).isNull();
        assertThat(entity.getName()).isEqualTo("surgery");
    }

    @Test
    void specialtyMapper_toDtoList() {
        Specialty s1 = new Specialty(1, "radiology");
        Specialty s2 = new Specialty(2, "surgery");

        List<SpecialtyDto> dtos = specialtyMapper.toDtoList(List.of(s1, s2));

        assertThat(dtos).hasSize(2);
    }

    @Test
    void specialtyMapper_updateEntity() {
        Specialty existing = new Specialty(1, "old");
        SpecialtyDto dto = new SpecialtyDto(null, "updated");

        specialtyMapper.updateEntity(dto, existing);

        assertThat(existing.getId()).isEqualTo(1);
        assertThat(existing.getName()).isEqualTo("updated");
    }
}
