package com.petclinic.vet.repository;

import com.petclinic.vet.entity.Specialty;
import com.petclinic.vet.entity.Vet;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class VetRepositoryTest {

    @Autowired
    private VetRepository vetRepository;

    @Autowired
    private SpecialtyRepository specialtyRepository;

    private Specialty radiology;
    private Vet james;

    @BeforeEach
    void setUp() {
        vetRepository.deleteAll();
        specialtyRepository.deleteAll();

        radiology = new Specialty();
        radiology.setName("radiology");
        radiology = specialtyRepository.save(radiology);

        Specialty surgery = new Specialty();
        surgery.setName("surgery");
        specialtyRepository.save(surgery);

        james = new Vet();
        james.setFirstName("James");
        james.setLastName("Carter");
        james.setSpecialties(Set.of(radiology));
        james = vetRepository.save(james);

        Vet helen = new Vet();
        helen.setFirstName("Helen");
        helen.setLastName("Leary");
        helen.setSpecialties(Set.of(radiology));
        vetRepository.save(helen);
    }

    @Test
    void findBySpecialtyId_returnsVetsWithSpecialty() {
        List<Vet> vets = vetRepository.findBySpecialtyId(radiology.getId());
        assertThat(vets).hasSize(2);
    }

    @Test
    void findBySpecialtyId_noResults() {
        List<Vet> vets = vetRepository.findBySpecialtyId(9999);
        assertThat(vets).isEmpty();
    }

    @Test
    void findByNameContainingIgnoreCase_findsLastName() {
        List<Vet> vets = vetRepository.findByNameContainingIgnoreCase("Carter");
        assertThat(vets).hasSize(1);
        assertThat(vets.get(0).getFirstName()).isEqualTo("James");
    }

    @Test
    void findByNameContainingIgnoreCase_findsFirstName() {
        List<Vet> vets = vetRepository.findByNameContainingIgnoreCase("helen");
        assertThat(vets).hasSize(1);
    }

    @Test
    void findByNameContainingIgnoreCase_noMatch() {
        List<Vet> vets = vetRepository.findByNameContainingIgnoreCase("Unknown");
        assertThat(vets).isEmpty();
    }

    @Test
    void save_setsAuditFields() {
        assertThat(james.getId()).isNotNull();
        assertThat(james.getCreatedAt()).isNotNull();
        assertThat(james.getUpdatedAt()).isNotNull();
    }
}
