package com.petclinic.vet.repository;

import com.petclinic.vet.config.JpaAuditingConfig;
import com.petclinic.vet.entity.Specialty;
import com.petclinic.vet.entity.Vet;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Repository tests for {@link VetRepository} using @DataJpaTest with H2.
 */
@DataJpaTest
@ActiveProfiles("test")
@Import(JpaAuditingConfig.class)
class VetRepositoryTest {

    @Autowired
    private VetRepository vetRepository;

    @Autowired
    private SpecialtyRepository specialtyRepository;

    @BeforeEach
    void setUp() {
        vetRepository.deleteAll();
        specialtyRepository.deleteAll();
    }

    @Test
    void shouldSaveAndFindById() {
        Vet vet = new Vet();
        vet.setFirstName("James");
        vet.setLastName("Carter");
        Vet saved = vetRepository.save(vet);

        Optional<Vet> found = vetRepository.findById(saved.getId());
        assertThat(found).isPresent();
        assertThat(found.get().getFirstName()).isEqualTo("James");
        assertThat(found.get().getLastName()).isEqualTo("Carter");
    }

    @Test
    void shouldFindByLastNameContainingIgnoreCase() {
        Vet v1 = new Vet();
        v1.setFirstName("James");
        v1.setLastName("Carter");
        Vet v2 = new Vet();
        v2.setFirstName("Helen");
        v2.setLastName("Leary");
        vetRepository.saveAll(List.of(v1, v2));

        List<Vet> results = vetRepository.findByLastNameContainingIgnoreCase("cart");
        assertThat(results).hasSize(1);
        assertThat(results.get(0).getLastName()).isEqualTo("Carter");
    }

    @Test
    void shouldFindBySpecialtyName() {
        Specialty radiology = new Specialty();
        radiology.setName("radiology");
        radiology = specialtyRepository.save(radiology);

        Specialty surgery = new Specialty();
        surgery.setName("surgery");
        surgery = specialtyRepository.save(surgery);

        Vet v1 = new Vet();
        v1.setFirstName("Helen");
        v1.setLastName("Leary");
        v1.addSpecialty(radiology);
        vetRepository.save(v1);

        Vet v2 = new Vet();
        v2.setFirstName("Linda");
        v2.setLastName("Douglas");
        v2.addSpecialty(surgery);
        vetRepository.save(v2);

        List<Vet> radiologyVets = vetRepository.findBySpecialtyName("radiology");
        assertThat(radiologyVets).hasSize(1);
        assertThat(radiologyVets.get(0).getFirstName()).isEqualTo("Helen");

        // Case-insensitive
        List<Vet> surgeryVets = vetRepository.findBySpecialtyName("SURGERY");
        assertThat(surgeryVets).hasSize(1);
        assertThat(surgeryVets.get(0).getFirstName()).isEqualTo("Linda");
    }

    @Test
    void shouldFindByFirstAndLastName() {
        Vet vet = new Vet();
        vet.setFirstName("James");
        vet.setLastName("Carter");
        vetRepository.save(vet);

        List<Vet> results = vetRepository.findByFirstNameContainingIgnoreCaseAndLastNameContainingIgnoreCase("jam", "cart");
        assertThat(results).hasSize(1);
        assertThat(results.get(0).getFirstName()).isEqualTo("James");
    }

    @Test
    void shouldSaveVetWithSpecialties() {
        Specialty s1 = new Specialty();
        s1.setName("radiology");
        s1 = specialtyRepository.save(s1);

        Specialty s2 = new Specialty();
        s2.setName("surgery");
        s2 = specialtyRepository.save(s2);

        Vet vet = new Vet();
        vet.setFirstName("Linda");
        vet.setLastName("Douglas");
        vet.setSpecialties(List.of(s1, s2));
        Vet saved = vetRepository.save(vet);

        Optional<Vet> found = vetRepository.findById(saved.getId());
        assertThat(found).isPresent();
        assertThat(found.get().getSpecialties()).hasSize(2);
    }

    @Test
    void shouldDeleteVet() {
        Vet vet = new Vet();
        vet.setFirstName("Test");
        vet.setLastName("Vet");
        Vet saved = vetRepository.save(vet);

        vetRepository.deleteById(saved.getId());
        Optional<Vet> found = vetRepository.findById(saved.getId());
        assertThat(found).isEmpty();
    }

    @Test
    void shouldClearSpecialties() {
        Specialty s = new Specialty();
        s.setName("radiology");
        s = specialtyRepository.save(s);

        Vet vet = new Vet();
        vet.setFirstName("James");
        vet.setLastName("Carter");
        vet.addSpecialty(s);
        vet = vetRepository.save(vet);

        vet.clearSpecialties();
        vet = vetRepository.save(vet);

        Optional<Vet> found = vetRepository.findById(vet.getId());
        assertThat(found).isPresent();
        assertThat(found.get().getSpecialties()).isEmpty();
    }

    @Test
    void shouldReturnEmptyForNoResults() {
        List<Vet> results = vetRepository.findByLastNameContainingIgnoreCase("nonexistent");
        assertThat(results).isEmpty();
    }

    @Test
    void shouldFindAllVets() {
        Vet v1 = new Vet();
        v1.setFirstName("James");
        v1.setLastName("Carter");
        Vet v2 = new Vet();
        v2.setFirstName("Helen");
        v2.setLastName("Leary");
        vetRepository.saveAll(List.of(v1, v2));

        List<Vet> all = vetRepository.findAll();
        assertThat(all).hasSize(2);
    }
}
