package com.petclinic.vet.mapper;

import com.petclinic.vet.dto.SpecialtyDto;
import com.petclinic.vet.dto.SpecialtyRequestDto;
import com.petclinic.vet.entity.Specialty;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration tests for SpecialtyMapper using the real MapStruct-generated implementation.
 * Verifies entity/DTO/request conversions and field-level mapping correctness.
 */
@SpringBootTest
class SpecialtyMapperTest {

    @Autowired
    private SpecialtyMapper specialtyMapper;

    @Test
    void toDto_mapsAllFields() {
        Specialty specialty = new Specialty();
        specialty.setId(1);
        specialty.setName("radiology");
        specialty.setCreatedAt(Instant.now());
        specialty.setUpdatedAt(Instant.now());

        SpecialtyDto dto = specialtyMapper.toDto(specialty);

        assertThat(dto.getId()).isEqualTo(1);
        assertThat(dto.getName()).isEqualTo("radiology");
    }

    @Test
    void toDtoList_convertsMultipleSpecialties() {
        Specialty s1 = new Specialty();
        s1.setId(1);
        s1.setName("radiology");
        s1.setCreatedAt(Instant.now());
        s1.setUpdatedAt(Instant.now());

        Specialty s2 = new Specialty();
        s2.setId(2);
        s2.setName("surgery");
        s2.setCreatedAt(Instant.now());
        s2.setUpdatedAt(Instant.now());

        List<SpecialtyDto> dtos = specialtyMapper.toDtoList(List.of(s1, s2));

        assertThat(dtos).hasSize(2);
        assertThat(dtos.get(0).getName()).isEqualTo("radiology");
        assertThat(dtos.get(1).getName()).isEqualTo("surgery");
    }

    @Test
    void toDtoList_emptyCollection_returnsEmptyList() {
        List<SpecialtyDto> dtos = specialtyMapper.toDtoList(List.of());

        assertThat(dtos).isEmpty();
    }

    @Test
    void toEntity_mapsNameAndIgnoresAuditFields() {
        SpecialtyRequestDto request = new SpecialtyRequestDto("cardiology");

        Specialty entity = specialtyMapper.toEntity(request);

        // Name should be mapped from the request
        assertThat(entity.getName()).isEqualTo("cardiology");
        // Id and audit fields should be null (ignored by mapper)
        assertThat(entity.getId()).isNull();
        assertThat(entity.getCreatedAt()).isNull();
        assertThat(entity.getUpdatedAt()).isNull();
    }

    @Test
    void updateEntity_updatesNameOnly() {
        Specialty existing = new Specialty();
        existing.setId(1);
        existing.setName("old-name");
        existing.setCreatedAt(Instant.now());
        existing.setUpdatedAt(Instant.now());

        SpecialtyRequestDto request = new SpecialtyRequestDto("new-name");

        specialtyMapper.updateEntity(request, existing);

        // Name should be updated from the request
        assertThat(existing.getName()).isEqualTo("new-name");
        // Id and audit fields should remain unchanged
        assertThat(existing.getId()).isEqualTo(1);
        assertThat(existing.getCreatedAt()).isNotNull();
        assertThat(existing.getUpdatedAt()).isNotNull();
    }
}
