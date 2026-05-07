package com.petclinic.vet.mapper;

import com.petclinic.vet.dto.SpecialtyRequest;
import com.petclinic.vet.dto.SpecialtyResponse;
import com.petclinic.vet.entity.Specialty;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class SpecialtyMapperTest {

    private final SpecialtyMapper mapper = new SpecialtyMapper();

    @Test
    void toResponse_mapsFields() {
        Specialty entity = new Specialty();
        entity.setId(1);
        entity.setName("radiology");

        SpecialtyResponse response = mapper.toResponse(entity);

        assertThat(response.id()).isEqualTo(1);
        assertThat(response.name()).isEqualTo("radiology");
    }

    @Test
    void toEntity_mapsFields() {
        SpecialtyRequest request = new SpecialtyRequest("surgery");

        Specialty entity = mapper.toEntity(request);

        assertThat(entity.getName()).isEqualTo("surgery");
        assertThat(entity.getId()).isNull();
    }

    @Test
    void updateEntity_updatesName() {
        Specialty entity = new Specialty();
        entity.setId(1);
        entity.setName("radiology");

        mapper.updateEntity(entity, new SpecialtyRequest("dentistry"));

        assertThat(entity.getName()).isEqualTo("dentistry");
        assertThat(entity.getId()).isEqualTo(1);
    }
}
