package com.petclinic.vet.mapper;

import com.petclinic.vet.dto.SpecialtyResponse;
import com.petclinic.vet.dto.VetResponse;
import com.petclinic.vet.entity.Specialty;
import com.petclinic.vet.entity.Vet;
import org.junit.jupiter.api.Test;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class VetMapperTest {

    private final VetMapper mapper = new VetMapperImpl();

    @Test
    void toResponse_mapsVetWithSpecialties() {
        Specialty s1 = new Specialty();
        s1.setId(2);
        s1.setName("surgery");

        Specialty s2 = new Specialty();
        s2.setId(1);
        s2.setName("radiology");

        Vet vet = new Vet();
        vet.setId(1);
        vet.setFirstName("James");
        vet.setLastName("Carter");
        vet.setSpecialties(Set.of(s1, s2));

        VetResponse response = mapper.toResponse(vet);

        assertThat(response.id()).isEqualTo(1);
        assertThat(response.firstName()).isEqualTo("James");
        assertThat(response.specialties()).hasSize(2);
        assertThat(response.specialties().get(0).id()).isEqualTo(1);
    }

    @Test
    void toResponse_null_returnsNull() {
        assertThat(mapper.toResponse(null)).isNull();
    }

    @Test
    void toResponseList_mapsCorrectly() {
        Vet vet = new Vet();
        vet.setId(1);
        vet.setFirstName("James");
        vet.setLastName("Carter");

        List<VetResponse> result = mapper.toResponseList(List.of(vet));
        assertThat(result).hasSize(1);
    }

    @Test
    void toResponseList_null_returnsEmpty() {
        assertThat(mapper.toResponseList(null)).isEmpty();
    }

    @Test
    void mapSpecialties_null_returnsEmpty() {
        List<SpecialtyResponse> result = mapper.mapSpecialties(null);
        assertThat(result).isEmpty();
    }
}
