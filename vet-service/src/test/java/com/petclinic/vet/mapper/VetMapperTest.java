package com.petclinic.vet.mapper;

import com.petclinic.vet.dto.VetResponse;
import com.petclinic.vet.entity.Specialty;
import com.petclinic.vet.entity.Vet;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class VetMapperTest {

    private final SpecialtyMapper specialtyMapper = new SpecialtyMapper();
    private final VetMapper mapper = new VetMapper(specialtyMapper);

    @Test
    void toResponse_mapsAllFields() {
        Specialty radiology = new Specialty();
        radiology.setId(1);
        radiology.setName("radiology");

        Specialty surgery = new Specialty();
        surgery.setId(2);
        surgery.setName("surgery");

        Vet vet = new Vet();
        vet.setId(1);
        vet.setFirstName("James");
        vet.setLastName("Carter");
        vet.setSpecialties(new HashSet<>(Set.of(radiology, surgery)));

        VetResponse response = mapper.toResponse(vet);

        assertThat(response.id()).isEqualTo(1);
        assertThat(response.firstName()).isEqualTo("James");
        assertThat(response.lastName()).isEqualTo("Carter");
        assertThat(response.specialties()).hasSize(2);
        assertThat(response.specialties().get(0).id()).isEqualTo(1);
        assertThat(response.specialties().get(1).id()).isEqualTo(2);
    }

    @Test
    void toResponse_withEmptySpecialties() {
        Vet vet = new Vet();
        vet.setId(1);
        vet.setFirstName("James");
        vet.setLastName("Carter");
        vet.setSpecialties(new HashSet<>());

        VetResponse response = mapper.toResponse(vet);

        assertThat(response.specialties()).isEmpty();
    }
}
