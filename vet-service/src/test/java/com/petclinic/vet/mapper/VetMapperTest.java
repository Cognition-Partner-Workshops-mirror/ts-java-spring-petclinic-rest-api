package com.petclinic.vet.mapper;

import com.petclinic.vet.dto.SpecialtyResponse;
import com.petclinic.vet.dto.VetRequest;
import com.petclinic.vet.dto.VetResponse;
import com.petclinic.vet.entity.Specialty;
import com.petclinic.vet.entity.Vet;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.Instant;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class VetMapperTest {

    @Autowired
    private VetMapper vetMapper;

    @Test
    void toResponse_mapsAllFields() {
        Specialty s = new Specialty();
        s.setId(1);
        s.setName("radiology");
        s.setCreatedAt(Instant.now());
        s.setUpdatedAt(Instant.now());

        Vet vet = new Vet();
        vet.setId(1);
        vet.setFirstName("James");
        vet.setLastName("Carter");
        vet.setSpecialties(new HashSet<>(Set.of(s)));
        vet.setCreatedAt(Instant.now());
        vet.setUpdatedAt(Instant.now());

        VetResponse response = vetMapper.toResponse(vet);

        assertThat(response.id()).isEqualTo(1);
        assertThat(response.firstName()).isEqualTo("James");
        assertThat(response.lastName()).isEqualTo("Carter");
        assertThat(response.specialties()).hasSize(1);
        assertThat(response.specialties().getFirst().name()).isEqualTo("radiology");
    }

    @Test
    void toResponse_emptySpecialties() {
        Vet vet = new Vet();
        vet.setId(2);
        vet.setFirstName("Helen");
        vet.setLastName("Leary");
        vet.setSpecialties(new HashSet<>());

        VetResponse response = vetMapper.toResponse(vet);

        assertThat(response.specialties()).isEmpty();
    }

    @Test
    void toResponse_nullSpecialties() {
        Vet vet = new Vet();
        vet.setId(3);
        vet.setFirstName("Test");
        vet.setLastName("Vet");
        vet.setSpecialties(null);

        VetResponse response = vetMapper.toResponse(vet);

        assertThat(response.specialties()).isEmpty();
    }

    @Test
    void toResponseList_mapsCollection() {
        Vet vet1 = new Vet();
        vet1.setId(1);
        vet1.setFirstName("James");
        vet1.setLastName("Carter");
        vet1.setSpecialties(new HashSet<>());

        Vet vet2 = new Vet();
        vet2.setId(2);
        vet2.setFirstName("Helen");
        vet2.setLastName("Leary");
        vet2.setSpecialties(new HashSet<>());

        List<VetResponse> responses = vetMapper.toResponseList(List.of(vet1, vet2));

        assertThat(responses).hasSize(2);
    }

    @Test
    void toEntity_mapsRequestFields() {
        VetRequest request = new VetRequest("James", "Carter",
            List.of(new SpecialtyResponse(1, "radiology")));

        Vet vet = vetMapper.toEntity(request);

        assertThat(vet.getFirstName()).isEqualTo("James");
        assertThat(vet.getLastName()).isEqualTo("Carter");
        assertThat(vet.getId()).isNull();
    }

    @Test
    void toResponse_multipleSpecialties_sortedByName() {
        Specialty s1 = new Specialty();
        s1.setId(1);
        s1.setName("surgery");

        Specialty s2 = new Specialty();
        s2.setId(2);
        s2.setName("dentistry");

        Specialty s3 = new Specialty();
        s3.setId(3);
        s3.setName("radiology");

        Vet vet = new Vet();
        vet.setId(1);
        vet.setFirstName("Linda");
        vet.setLastName("Douglas");
        vet.setSpecialties(new HashSet<>(Set.of(s1, s2, s3)));

        VetResponse response = vetMapper.toResponse(vet);

        assertThat(response.specialties()).extracting(SpecialtyResponse::name)
            .containsExactly("dentistry", "radiology", "surgery");
    }

    @Test
    void toResponseList_null_returnsNull() {
        List<VetResponse> result = vetMapper.toResponseList(null);
        assertThat(result).isNull();
    }

    @Test
    void toEntity_null_returnsNull() {
        Vet result = vetMapper.toEntity(null);
        assertThat(result).isNull();
    }

    @Test
    void toResponse_null_returnsNull() {
        VetResponse result = vetMapper.toResponse(null);
        assertThat(result).isNull();
    }
}
