package com.petclinic.vet.repository;

import com.petclinic.vet.config.JpaAuditingConfig;
import com.petclinic.vet.entity.Specialty;
import com.petclinic.vet.entity.Vet;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Repository tests for VetRepository using @DataJpaTest.
 * Verifies custom query methods and JPA integration with H2.
 */
@DataJpaTest
@Import(JpaAuditingConfig.class)
@ActiveProfiles("test")
class VetRepositoryTest {

    @Autowired
    private VetRepository vetRepository;

    @Autowired
    private SpecialtyRepository specialtyRepository;

    @Test
    @DisplayName("findAll returns seed data vets")
    void findAll_returnsSeedData() {
        List<Vet> vets = vetRepository.findAll();
        // Seed data from V1 migration contains 6 vets
        assertThat(vets).hasSizeGreaterThanOrEqualTo(6);
    }

    @Test
    @DisplayName("findById returns vet with specialties loaded")
    void findById_loadsSpecialties() {
        // Vet 2 (Helen Leary) has radiology specialty assigned in seed data
        Optional<Vet> vet = vetRepository.findById(2);
        assertThat(vet).isPresent();
        assertThat(vet.get().getFirstName()).isEqualTo("Helen");
        assertThat(vet.get().getSpecialties()).isNotEmpty();
    }

    @Test
    @DisplayName("findById returns empty when vet does not exist")
    void findById_notFound() {
        Optional<Vet> vet = vetRepository.findById(999);
        assertThat(vet).isEmpty();
    }

    @Test
    @DisplayName("findByLastNameContainingIgnoreCase returns matching vets")
    void findByLastName_found() {
        List<Vet> results = vetRepository.findByLastNameContainingIgnoreCase("Carter");
        assertThat(results).hasSize(1);
        assertThat(results.get(0).getLastName()).isEqualTo("Carter");
    }

    @Test
    @DisplayName("findByLastNameContainingIgnoreCase is case-insensitive")
    void findByLastName_caseInsensitive() {
        List<Vet> results = vetRepository.findByLastNameContainingIgnoreCase("carter");
        assertThat(results).hasSize(1);
    }

    @Test
    @DisplayName("findByLastNameContainingIgnoreCase supports partial match")
    void findByLastName_partialMatch() {
        List<Vet> results = vetRepository.findByLastNameContainingIgnoreCase("art");
        assertThat(results).hasSize(1);
        assertThat(results.get(0).getLastName()).isEqualTo("Carter");
    }

    @Test
    @DisplayName("findByLastNameContainingIgnoreCase returns empty for no match")
    void findByLastName_noMatch() {
        List<Vet> results = vetRepository.findByLastNameContainingIgnoreCase("xyz");
        assertThat(results).isEmpty();
    }

    @Test
    @DisplayName("findBySpecialtyNameContainingIgnoreCase returns matching vets")
    void findBySpecialty_found() {
        // Vets with radiology: Helen Leary (id=2) and Henry Stevens (id=5)
        List<Vet> results = vetRepository.findBySpecialtyNameContainingIgnoreCase("radiology");
        assertThat(results).hasSizeGreaterThanOrEqualTo(2);
    }

    @Test
    @DisplayName("findBySpecialtyNameContainingIgnoreCase supports partial match")
    void findBySpecialty_partialMatch() {
        List<Vet> results = vetRepository.findBySpecialtyNameContainingIgnoreCase("surg");
        assertThat(results).isNotEmpty();
    }

    @Test
    @DisplayName("findBySpecialtyNameContainingIgnoreCase returns empty for no match")
    void findBySpecialty_noMatch() {
        List<Vet> results = vetRepository.findBySpecialtyNameContainingIgnoreCase("nonexistent");
        assertThat(results).isEmpty();
    }

    @Test
    @DisplayName("findByFirstNameAndLastNameContainingIgnoreCase returns matching vet")
    void findByFirstAndLastName_found() {
        List<Vet> results = vetRepository.findByFirstNameAndLastNameContainingIgnoreCase("James", "Carter");
        assertThat(results).hasSize(1);
        assertThat(results.get(0).getFirstName()).isEqualTo("James");
    }

    @Test
    @DisplayName("findByFirstNameAndLastNameContainingIgnoreCase returns empty when no match")
    void findByFirstAndLastName_noMatch() {
        List<Vet> results = vetRepository.findByFirstNameAndLastNameContainingIgnoreCase("Unknown", "Person");
        assertThat(results).isEmpty();
    }

    @Test
    @DisplayName("save persists a new vet with specialties")
    void save_persistsVetWithSpecialties() {
        Specialty radiology = specialtyRepository.findById(1).orElseThrow();

        Vet vet = new Vet();
        vet.setFirstName("New");
        vet.setLastName("Vet");
        vet.getSpecialties().add(radiology);
        Vet saved = vetRepository.save(vet);

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getSpecialties()).hasSize(1);
    }

    @Test
    @DisplayName("delete removes an existing vet")
    void delete_removesVet() {
        Vet vet = new Vet();
        vet.setFirstName("Temp");
        vet.setLastName("Vet");
        Vet saved = vetRepository.save(vet);

        vetRepository.deleteById(saved.getId());

        assertThat(vetRepository.findById(saved.getId())).isEmpty();
    }
}
