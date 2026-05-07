package com.petclinic.vet.mapper;

import com.petclinic.vet.dto.SpecialtyRequestDto;
import com.petclinic.vet.dto.SpecialtyResponseDto;
import com.petclinic.vet.entity.Specialty;
import java.time.Instant;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
class SpecialtyMapperTest {

    @Autowired
    private SpecialtyMapper mapper;

    @Test
    void toResponse_mapsCorrectly() {
        Specialty entity = new Specialty();
        entity.setId(1);
        entity.setName("radiology");
        entity.setCreatedAt(Instant.now());
        entity.setUpdatedAt(Instant.now());

        SpecialtyResponseDto dto = mapper.toResponse(entity);
        assertThat(dto.id()).isEqualTo(1);
        assertThat(dto.name()).isEqualTo("radiology");
    }

    @Test
    void toResponse_null() {
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

        List<SpecialtyResponseDto> result = mapper.toResponseList(List.of(s1, s2));
        assertThat(result).hasSize(2);
        assertThat(result.get(0).name()).isEqualTo("radiology");
    }

    @Test
    void toResponseList_null() {
        assertThat(mapper.toResponseList(null)).isNull();
    }

    @Test
    void toEntity_mapsCorrectly() {
        SpecialtyRequestDto dto = new SpecialtyRequestDto("radiology");
        Specialty entity = mapper.toEntity(dto);
        assertThat(entity.getName()).isEqualTo("radiology");
        assertThat(entity.getId()).isNull();
    }

    @Test
    void toEntity_null() {
        assertThat(mapper.toEntity(null)).isNull();
    }

    @Test
    void updateEntity_updatesFields() {
        Specialty entity = new Specialty();
        entity.setId(1);
        entity.setName("old");

        SpecialtyRequestDto dto = new SpecialtyRequestDto("updated");
        mapper.updateEntity(dto, entity);

        assertThat(entity.getName()).isEqualTo("updated");
        assertThat(entity.getId()).isEqualTo(1);
    }

    @Test
    void updateEntity_nullDto() {
        Specialty entity = new Specialty();
        entity.setName("unchanged");
        mapper.updateEntity(null, entity);
        assertThat(entity.getName()).isEqualTo("unchanged");
    }
}
