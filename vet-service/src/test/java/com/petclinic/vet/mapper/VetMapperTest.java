package com.petclinic.vet.mapper;

import com.petclinic.vet.dto.SpecialtyResponseDto;
import com.petclinic.vet.entity.Specialty;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class VetMapperTest {

    @Test
    void specialtiesToDtoList_shouldMapCorrectly() {
        VetMapper mapper = new VetMapperImpl();

        Specialty s1 = new Specialty();
        s1.setId(1);
        s1.setName("radiology");

        Specialty s2 = new Specialty();
        s2.setId(2);
        s2.setName("surgery");

        List<SpecialtyResponseDto> result = mapper.specialtiesToDtoList(Set.of(s1, s2));

        assertThat(result).hasSize(2);
        assertThat(result).extracting(SpecialtyResponseDto::name)
            .containsExactlyInAnyOrder("radiology", "surgery");
    }

    @Test
    void specialtiesToDtoList_shouldReturnEmptyForNull() {
        VetMapper mapper = new VetMapperImpl();
        List<SpecialtyResponseDto> result = mapper.specialtiesToDtoList(null);
        assertThat(result).isEmpty();
    }
}
