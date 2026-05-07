package com.petclinic.vet.mapper;

import com.petclinic.vet.dto.VetResponse;
import com.petclinic.vet.entity.Specialty;
import com.petclinic.vet.entity.Vet;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class VetMapperTest {

    @Autowired
    private VetMapper mapper;

    @Test
    void toResponse_mapsAllFields() {
        Specialty s = new Specialty();
        s.setId(1);
        s.setName("radiology");

        Vet vet = new Vet();
        vet.setId(1);
        vet.setFirstName("James");
        vet.setLastName("Carter");
        Set<Specialty> specialties = new HashSet<>();
        specialties.add(s);
        vet.setSpecialties(specialties);

        VetResponse response = mapper.toResponse(vet);

        assertThat(response.id()).isEqualTo(1);
        assertThat(response.firstName()).isEqualTo("James");
        assertThat(response.lastName()).isEqualTo("Carter");
        assertThat(response.specialties()).hasSize(1);
        assertThat(response.specialties().get(0).name()).isEqualTo("radiology");
    }

    @Test
    void toResponse_handlesNull() {
        VetResponse response = mapper.toResponse(null);
        assertThat(response).isNull();
    }

    @Test
    void toResponse_handlesEmptySpecialties() {
        Vet vet = new Vet();
        vet.setId(2);
        vet.setFirstName("Helen");
        vet.setLastName("Leary");

        VetResponse response = mapper.toResponse(vet);

        assertThat(response.specialties()).isEmpty();
    }

    @Test
    void toResponse_handlesNullSpecialties() {
        Vet vet = new Vet();
        vet.setId(3);
        vet.setFirstName("Test");
        vet.setLastName("Vet");
        vet.setSpecialties(null);

        VetResponse response = mapper.toResponse(vet);

        assertThat(response.specialties()).isNull();
    }
}
