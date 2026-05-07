package com.petclinic.vet.repository;

import com.petclinic.vet.entity.Specialty;
import com.petclinic.vet.entity.Vet;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class VetRepositoryTest {

    @Autowired
    private VetRepository vetRepository;

    @Autowired
    private SpecialtyRepository specialtyRepository;

    private Specialty radiology;
    private Specialty surgery;

    @BeforeEach
    void setUp() {
        radiology = new Specialty();
        radiology.setName("radiology");
        radiology = specialtyRepository.save(radiology);

        surgery = new Specialty();
        surgery.setName("surgery");
        surgery = specialtyRepository.save(surgery);
    }

    @Test
    void saveAndFind() {
        Vet vet = new Vet();
        vet.setFirstName("James");
        vet.setLastName("Carter");
        vet.setSpecialties(List.of(radiology));
        Vet saved = vetRepository.save(vet);

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getCreatedAt()).isNotNull();
        assertThat(saved.getSpecialties()).hasSize(1);
    }

    @Test
    void findBySpecialtyName() {
        Vet vet1 = new Vet();
        vet1.setFirstName("James");
        vet1.setLastName("Carter");
        vet1.setSpecialties(List.of(radiology));
        vetRepository.save(vet1);

        Vet vet2 = new Vet();
        vet2.setFirstName("Helen");
        vet2.setLastName("Leary");
        vet2.setSpecialties(List.of(surgery));
        vetRepository.save(vet2);

        List<Vet> radiologyVets = vetRepository.findBySpecialtyName("radiology");
        assertThat(radiologyVets).hasSize(1);
        assertThat(radiologyVets.get(0).getFirstName()).isEqualTo("James");
    }

    @Test
    void findBySpecialtyName_noMatch() {
        Vet vet = new Vet();
        vet.setFirstName("James");
        vet.setLastName("Carter");
        vet.setSpecialties(List.of(radiology));
        vetRepository.save(vet);

        List<Vet> results = vetRepository.findBySpecialtyName("dentistry");
        assertThat(results).isEmpty();
    }

    @Test
    void findByNameContaining() {
        Vet vet = new Vet();
        vet.setFirstName("James");
        vet.setLastName("Carter");
        vetRepository.save(vet);

        List<Vet> results = vetRepository.findByNameContaining("Cart");
        assertThat(results).hasSize(1);
        assertThat(results.get(0).getLastName()).isEqualTo("Carter");
    }

    @Test
    void findByNameContaining_firstName() {
        Vet vet = new Vet();
        vet.setFirstName("James");
        vet.setLastName("Carter");
        vetRepository.save(vet);

        List<Vet> results = vetRepository.findByNameContaining("Jam");
        assertThat(results).hasSize(1);
    }

    @Test
    void findByNameContaining_caseInsensitive() {
        Vet vet = new Vet();
        vet.setFirstName("James");
        vet.setLastName("Carter");
        vetRepository.save(vet);

        List<Vet> results = vetRepository.findByNameContaining("carter");
        assertThat(results).hasSize(1);
    }

    @Test
    void findByLastNameIgnoreCase() {
        Vet vet = new Vet();
        vet.setFirstName("James");
        vet.setLastName("Carter");
        vetRepository.save(vet);

        List<Vet> results = vetRepository.findByLastNameIgnoreCase("carter");
        assertThat(results).hasSize(1);
    }

    @Test
    void findByLastNameIgnoreCase_noMatch() {
        Vet vet = new Vet();
        vet.setFirstName("James");
        vet.setLastName("Carter");
        vetRepository.save(vet);

        List<Vet> results = vetRepository.findByLastNameIgnoreCase("Smith");
        assertThat(results).isEmpty();
    }

    @Test
    void deleteVet() {
        Vet vet = new Vet();
        vet.setFirstName("James");
        vet.setLastName("Carter");
        Vet saved = vetRepository.save(vet);

        vetRepository.deleteById(saved.getId());
        assertThat(vetRepository.findById(saved.getId())).isEmpty();
    }
}
