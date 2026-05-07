package com.petclinic.vet.mapper;

import com.petclinic.vet.dto.VetRequest;
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
    void toEntity_mapsFields() {
        VetRequest request = new VetRequest("James", "Carter", null);
        Vet vet = mapper.toEntity(request);
        assertThat(vet.getFirstName()).isEqualTo("James");
        assertThat(vet.getLastName()).isEqualTo("Carter");
        assertThat(vet.getId()).isNull();
    }

    @Test
    void toEntity_nullRequest_returnsNull() {
        assertThat(mapper.toEntity(null)).isNull();
    }

    @Test
    void toResponse_mapsFields() {
        Specialty s = new Specialty();
        s.setId(1);
        s.setName("radiology");

        Vet vet = new Vet();
        vet.setId(10);
        vet.setFirstName("James");
        vet.setLastName("Carter");
        vet.setSpecialties(Set.of(s));

        VetResponse response = mapper.toResponse(vet);
        assertThat(response.id()).isEqualTo(10);
        assertThat(response.firstName()).isEqualTo("James");
        assertThat(response.lastName()).isEqualTo("Carter");
        assertThat(response.specialties()).hasSize(1);
        assertThat(response.specialties().get(0).name()).isEqualTo("radiology");
    }

    @Test
    void toResponse_nullVet_returnsNull() {
        assertThat(mapper.toResponse(null)).isNull();
    }

    @Test
    void toResponse_sortsBySpecialtyName() {
        Specialty s1 = new Specialty();
        s1.setId(1);
        s1.setName("surgery");
        Specialty s2 = new Specialty();
        s2.setId(2);
        s2.setName("dentistry");

        Vet vet = new Vet();
        vet.setId(1);
        vet.setFirstName("A");
        vet.setLastName("B");
        vet.setSpecialties(Set.of(s1, s2));

        VetResponse response = mapper.toResponse(vet);
        assertThat(response.specialties().get(0).name()).isEqualTo("dentistry");
        assertThat(response.specialties().get(1).name()).isEqualTo("surgery");
    }

    @Test
    void toResponseList_mapsAll() {
        Vet vet = new Vet();
        vet.setId(1);
        vet.setFirstName("A");
        vet.setLastName("B");
        vet.setSpecialties(Set.of());

        List<VetResponse> responses = mapper.toResponseList(List.of(vet));
        assertThat(responses).hasSize(1);
    }

    @Test
    void toResponseList_nullList_returnsEmpty() {
        assertThat(mapper.toResponseList(null)).isEmpty();
    }
}
