package com.petclinic.vet.mapper;

import com.petclinic.vet.dto.SpecialtyRequest;
import com.petclinic.vet.dto.SpecialtyResponse;
import com.petclinic.vet.entity.Specialty;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class SpecialtyMapperTest {

    @Autowired
    private SpecialtyMapper mapper;

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
    void toResponse_handlesNull() {
        SpecialtyResponse response = mapper.toResponse(null);
        assertThat(response).isNull();
    }

    @Test
    void toEntity_mapsFields() {
        SpecialtyRequest request = new SpecialtyRequest("surgery");

        Specialty entity = mapper.toEntity(request);

        assertThat(entity.getName()).isEqualTo("surgery");
        assertThat(entity.getId()).isNull();
    }

    @Test
    void toEntity_handlesNull() {
        Specialty entity = mapper.toEntity(null);
        assertThat(entity).isNull();
    }

    @Test
    void updateEntity_updatesFields() {
        Specialty entity = new Specialty();
        entity.setId(1);
        entity.setName("old");

        SpecialtyRequest request = new SpecialtyRequest("new");
        mapper.updateEntity(request, entity);

        assertThat(entity.getName()).isEqualTo("new");
        assertThat(entity.getId()).isEqualTo(1);
    }

    @Test
    void updateEntity_handlesNullRequest() {
        Specialty entity = new Specialty();
        entity.setName("original");
        mapper.updateEntity(null, entity);
        assertThat(entity.getName()).isEqualTo("original");
    }
}
