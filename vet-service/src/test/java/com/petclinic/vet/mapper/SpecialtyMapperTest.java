package com.petclinic.vet.mapper;

import com.petclinic.vet.dto.SpecialtyRequest;
import com.petclinic.vet.dto.SpecialtyResponse;
import com.petclinic.vet.entity.Specialty;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class SpecialtyMapperTest {

    @Autowired
    private SpecialtyMapper specialtyMapper;

    @Test
    void toResponse_mapsAllFields() {
        Specialty entity = new Specialty();
        entity.setId(1);
        entity.setName("radiology");
        entity.setCreatedAt(Instant.now());
        entity.setUpdatedAt(Instant.now());

        SpecialtyResponse response = specialtyMapper.toResponse(entity);

        assertThat(response.id()).isEqualTo(1);
        assertThat(response.name()).isEqualTo("radiology");
    }

    @Test
    void toResponseList_mapsCollection() {
        Specialty s1 = new Specialty();
        s1.setId(1);
        s1.setName("radiology");

        Specialty s2 = new Specialty();
        s2.setId(2);
        s2.setName("surgery");

        List<SpecialtyResponse> responses = specialtyMapper.toResponseList(List.of(s1, s2));

        assertThat(responses).hasSize(2);
    }

    @Test
    void toEntity_mapsRequestFields() {
        SpecialtyRequest request = new SpecialtyRequest("oncology");

        Specialty entity = specialtyMapper.toEntity(request);

        assertThat(entity.getName()).isEqualTo("oncology");
        assertThat(entity.getId()).isNull();
        assertThat(entity.getCreatedAt()).isNull();
        assertThat(entity.getUpdatedAt()).isNull();
    }

    @Test
    void updateEntity_updatesExistingEntity() {
        Specialty entity = new Specialty();
        entity.setId(1);
        entity.setName("radiology");
        entity.setCreatedAt(Instant.now());
        entity.setUpdatedAt(Instant.now());

        SpecialtyRequest request = new SpecialtyRequest("oncology");
        specialtyMapper.updateEntity(request, entity);

        assertThat(entity.getId()).isEqualTo(1);
        assertThat(entity.getName()).isEqualTo("oncology");
    }

    @Test
    void toResponse_null_returnsNull() {
        SpecialtyResponse result = specialtyMapper.toResponse(null);
        assertThat(result).isNull();
    }

    @Test
    void toResponseList_null_returnsNull() {
        List<SpecialtyResponse> result = specialtyMapper.toResponseList(null);
        assertThat(result).isNull();
    }

    @Test
    void toEntity_null_returnsNull() {
        Specialty result = specialtyMapper.toEntity(null);
        assertThat(result).isNull();
    }
}
