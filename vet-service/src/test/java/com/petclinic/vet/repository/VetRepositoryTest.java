package com.petclinic.vet.repository;

import com.petclinic.vet.entity.Specialty;
import com.petclinic.vet.entity.Vet;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Repository tests for VetRepository using @DataJpaTest.
 * Tests custom query methods with an H2 in-memory database.
 */
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
        // Seed data is loaded by Flyway migrations
        radiology = specialtyRepository.findByNameIgnoreCase("radiology").orElse(null);
        surgery = specialtyRepository.findByNameIgnoreCase("surgery").orElse(null);
    }

    @Test
    void findByLastNameContainingIgnoreCase_findsMatchingVets() {
        List<Vet> results = vetRepository.findByLastNameContainingIgnoreCase("Carter");

        assertThat(results).isNotEmpty();
        assertThat(results.get(0).getLastName()).isEqualTo("Carter");
    }

    @Test
    void findByLastNameContainingIgnoreCase_caseInsensitive() {
        List<Vet> results = vetRepository.findByLastNameContainingIgnoreCase("carter");

        assertThat(results).isNotEmpty();
        assertThat(results.get(0).getLastName()).isEqualTo("Carter");
    }

    @Test
    void findByLastNameContainingIgnoreCase_returnsEmptyForNoMatch() {
        List<Vet> results = vetRepository.findByLastNameContainingIgnoreCase("NonExistent");

        assertThat(results).isEmpty();
    }

    @Test
    void findBySpecialtyId_findsVetsWithSpecialty() {
        assertThat(radiology).isNotNull();
        List<Vet> results = vetRepository.findBySpecialtyId(radiology.getId());

        assertThat(results).isNotEmpty();
        // Helen Leary and Henry Stevens have radiology
        assertThat(results).hasSizeGreaterThanOrEqualTo(2);
    }

    @Test
    void findBySpecialtyId_returnsEmptyForUnassignedSpecialty() {
        // Create a new specialty that's not assigned to any vet
        Specialty newSpecialty = new Specialty();
        newSpecialty.setName("ophthalmology");
        newSpecialty = specialtyRepository.save(newSpecialty);

        List<Vet> results = vetRepository.findBySpecialtyId(newSpecialty.getId());

        assertThat(results).isEmpty();
    }

    @Test
    void findBySpecialtyName_findsVetsWithMatchingSpecialty() {
        List<Vet> results = vetRepository.findBySpecialtyName("surgery");

        assertThat(results).isNotEmpty();
    }

    @Test
    void findBySpecialtyName_caseInsensitive() {
        List<Vet> results = vetRepository.findBySpecialtyName("SURGERY");

        assertThat(results).isNotEmpty();
    }

    @Test
    void save_persistsNewVetWithSpecialties() {
        Vet newVet = new Vet();
        newVet.setFirstName("Test");
        newVet.setLastName("Vet");
        newVet.setSpecialties(Set.of(radiology));

        Vet saved = vetRepository.save(newVet);

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getFirstName()).isEqualTo("Test");
        assertThat(saved.getSpecialties()).hasSize(1);
    }

    @Test
    void findAll_returnsAllSeededVets() {
        List<Vet> allVets = vetRepository.findAll();

        // 6 vets seeded in V2 migration
        assertThat(allVets).hasSize(6);
    }
}
