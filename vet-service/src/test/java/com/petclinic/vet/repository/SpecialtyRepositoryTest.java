package com.petclinic.vet.repository;

import com.petclinic.vet.entity.Specialty;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Repository-layer tests for SpecialtyRepository using @DataJpaTest.
 * Runs against H2 in-memory database with Flyway migrations applied.
 */
@DataJpaTest
class SpecialtyRepositoryTest {

    @Autowired
    private SpecialtyRepository specialtyRepository;

    @Test
    void findAll_returnsSeededSpecialties() {
        // V2 migration seeds 3 specialties: radiology, surgery, dentistry
        List<Specialty> specialties = specialtyRepository.findAll();
        assertThat(specialties).hasSize(3);
    }

    @Test
    void findById_returnsSpecialty() {
        Optional<Specialty> specialty = specialtyRepository.findById(1);
        assertThat(specialty).isPresent();
        assertThat(specialty.get().getName()).isEqualTo("radiology");
    }

    @Test
    void findById_notFound_returnsEmpty() {
        Optional<Specialty> specialty = specialtyRepository.findById(999);
        assertThat(specialty).isEmpty();
    }

    @Test
    void findByNameContainingIgnoreCase_matchesPartialName() {
        List<Specialty> results = specialtyRepository.findByNameContainingIgnoreCase("surg");
        assertThat(results).hasSize(1);
        assertThat(results.get(0).getName()).isEqualTo("surgery");
    }

    @Test
    void findByNameContainingIgnoreCase_caseInsensitive() {
        List<Specialty> results = specialtyRepository.findByNameContainingIgnoreCase("RADIO");
        assertThat(results).hasSize(1);
        assertThat(results.get(0).getName()).isEqualTo("radiology");
    }

    @Test
    void findByNameContainingIgnoreCase_noMatch_returnsEmpty() {
        List<Specialty> results = specialtyRepository.findByNameContainingIgnoreCase("nonexistent");
        assertThat(results).isEmpty();
    }

    @Test
    void save_createsNewSpecialty() {
        Specialty specialty = new Specialty();
        specialty.setName("orthopedics");
        Specialty saved = specialtyRepository.save(specialty);

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getName()).isEqualTo("orthopedics");
    }

    @Test
    void delete_removesSpecialty() {
        // Create a new specialty and then delete it
        Specialty specialty = new Specialty();
        specialty.setName("temporary");
        Specialty saved = specialtyRepository.save(specialty);

        specialtyRepository.delete(saved);

        Optional<Specialty> deleted = specialtyRepository.findById(saved.getId());
        assertThat(deleted).isEmpty();
    }
}
