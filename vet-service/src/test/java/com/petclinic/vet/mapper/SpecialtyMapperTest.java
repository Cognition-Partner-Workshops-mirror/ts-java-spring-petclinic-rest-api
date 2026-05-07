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
    void toResponse_mapsFields() {
        Specialty s = new Specialty();
        s.setId(1);
        s.setName("radiology");
        SpecialtyResponse response = mapper.toResponse(s);
        assertThat(response.id()).isEqualTo(1);
        assertThat(response.name()).isEqualTo("radiology");
    }

    @Test
    void toResponse_null_returnsNull() {
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
        List<SpecialtyResponse> result = mapper.toResponseList(List.of(s1, s2));
        assertThat(result).hasSize(2);
        assertThat(result.get(0).name()).isEqualTo("radiology");
    }

    @Test
    void toResponseList_null_returnsNull() {
        assertThat(mapper.toResponseList(null)).isNull();
    }

    @Test
    void toEntity_mapsRequest() {
        SpecialtyRequest req = new SpecialtyRequest("dentistry");
        Specialty entity = mapper.toEntity(req);
        assertThat(entity.getName()).isEqualTo("dentistry");
        assertThat(entity.getId()).isNull();
    }

    @Test
    void toEntity_null_returnsNull() {
        assertThat(mapper.toEntity(null)).isNull();
    }

    @Test
    void updateEntity_updatesFields() {
        Specialty entity = new Specialty();
        entity.setId(5);
        entity.setName("old");
        mapper.updateEntity(new SpecialtyRequest("new"), entity);
        assertThat(entity.getName()).isEqualTo("new");
        assertThat(entity.getId()).isEqualTo(5);
    }

    @Test
    void updateEntity_null_doesNothing() {
        Specialty entity = new Specialty();
        entity.setName("unchanged");
        mapper.updateEntity(null, entity);
        assertThat(entity.getName()).isEqualTo("unchanged");
    }
}
