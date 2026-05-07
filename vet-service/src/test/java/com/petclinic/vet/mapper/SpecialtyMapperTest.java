package com.petclinic.vet.mapper;

import com.petclinic.vet.dto.SpecialtyRequest;
import com.petclinic.vet.dto.SpecialtyResponse;
import com.petclinic.vet.entity.Specialty;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class SpecialtyMapperTest {

    private final SpecialtyMapper mapper = new SpecialtyMapperImpl();

    @Test
    void toEntity_mapsFields() {
        SpecialtyRequest request = new SpecialtyRequest("radiology");
        Specialty entity = mapper.toEntity(request);
        assertThat(entity.getName()).isEqualTo("radiology");
        assertThat(entity.getId()).isNull();
    }

    @Test
    void toEntity_nullRequest_returnsNull() {
        assertThat(mapper.toEntity(null)).isNull();
    }

    @Test
    void toResponse_mapsFields() {
        Specialty entity = new Specialty();
        entity.setId(1);
        entity.setName("surgery");

        SpecialtyResponse response = mapper.toResponse(entity);
        assertThat(response.id()).isEqualTo(1);
        assertThat(response.name()).isEqualTo("surgery");
    }

    @Test
    void toResponse_nullEntity_returnsNull() {
        assertThat(mapper.toResponse(null)).isNull();
    }

    @Test
    void toResponseList_mapsAll() {
        Specialty s1 = new Specialty();
        s1.setId(1);
        s1.setName("radiology");
        Specialty s2 = new Specialty();
        s2.setId(2);
        s2.setName("surgery");

        List<SpecialtyResponse> responses = mapper.toResponseList(List.of(s1, s2));
        assertThat(responses).hasSize(2);
        assertThat(responses.get(0).name()).isEqualTo("radiology");
    }

    @Test
    void toResponseList_nullList_returnsNull() {
        assertThat(mapper.toResponseList(null)).isNull();
    }

    @Test
    void updateEntity_updatesName() {
        Specialty entity = new Specialty();
        entity.setId(1);
        entity.setName("old");

        mapper.updateEntity(new SpecialtyRequest("new"), entity);
        assertThat(entity.getName()).isEqualTo("new");
        assertThat(entity.getId()).isEqualTo(1);
    }

    @Test
    void updateEntity_nullRequest_noOp() {
        Specialty entity = new Specialty();
        entity.setName("unchanged");
        mapper.updateEntity(null, entity);
        assertThat(entity.getName()).isEqualTo("unchanged");
    }
}
