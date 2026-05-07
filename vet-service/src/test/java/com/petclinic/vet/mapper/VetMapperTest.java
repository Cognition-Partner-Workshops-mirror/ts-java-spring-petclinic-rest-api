package com.petclinic.vet.mapper;

import com.petclinic.vet.dto.SpecialtyResponseDto;
import com.petclinic.vet.dto.VetResponseDto;
import com.petclinic.vet.entity.Specialty;
import com.petclinic.vet.entity.Vet;
import java.time.Instant;
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
    private VetMapper mapper;

    @Test
    void toResponse_mapsCorrectly() {
        Specialty s = new Specialty();
        s.setId(1);
        s.setName("radiology");
        s.setCreatedAt(Instant.now());
        s.setUpdatedAt(Instant.now());

        Vet vet = new Vet();
        vet.setId(1);
        vet.setFirstName("James");
        vet.setLastName("Carter");
        vet.setSpecialties(Set.of(s));
        vet.setCreatedAt(Instant.now());
        vet.setUpdatedAt(Instant.now());

        VetResponseDto dto = mapper.toResponse(vet);
        assertThat(dto.id()).isEqualTo(1);
        assertThat(dto.firstName()).isEqualTo("James");
        assertThat(dto.specialties()).hasSize(1);
        assertThat(dto.specialties().get(0).name()).isEqualTo("radiology");
    }

    @Test
    void toResponse_null() {
        assertThat(mapper.toResponse(null)).isNull();
    }

    @Test
    void mapSpecialties_null() {
        assertThat(mapper.mapSpecialties(null)).isEmpty();
    }

    @Test
    void mapSpecialties_sortedById() {
        Specialty s1 = new Specialty();
        s1.setId(2);
        s1.setName("surgery");

        Specialty s2 = new Specialty();
        s2.setId(1);
        s2.setName("radiology");

        Set<Specialty> set = new HashSet<>();
        set.add(s1);
        set.add(s2);

        List<SpecialtyResponseDto> result = mapper.mapSpecialties(set);
        assertThat(result).hasSize(2);
        assertThat(result.get(0).id()).isEqualTo(1);
        assertThat(result.get(1).id()).isEqualTo(2);
    }

    @Test
    void toResponseList_null() {
        assertThat(mapper.toResponseList(null)).isEmpty();
    }

    @Test
    void toResponseList_mapsAll() {
        Vet vet = new Vet();
        vet.setId(1);
        vet.setFirstName("James");
        vet.setLastName("Carter");
        vet.setSpecialties(new HashSet<>());
        vet.setCreatedAt(Instant.now());
        vet.setUpdatedAt(Instant.now());

        List<VetResponseDto> result = mapper.toResponseList(List.of(vet));
        assertThat(result).hasSize(1);
    }
}
