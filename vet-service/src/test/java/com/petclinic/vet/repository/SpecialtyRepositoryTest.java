package com.petclinic.vet.repository;

import com.petclinic.vet.config.JpaAuditingConfig;
import com.petclinic.vet.entity.Specialty;
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
 * Repository tests for SpecialtyRepository using @DataJpaTest.
 * Verifies custom query methods and JPA integration with H2.
 */
@DataJpaTest
@Import(JpaAuditingConfig.class)
@ActiveProfiles("test")
class SpecialtyRepositoryTest {

    @Autowired
    private SpecialtyRepository specialtyRepository;

    @Test
    @DisplayName("findAll returns seed data specialties")
    void findAll_returnsSeedData() {
        List<Specialty> specialties = specialtyRepository.findAll();
        // Seed data from V1 migration contains 3 specialties
        assertThat(specialties).hasSizeGreaterThanOrEqualTo(3);
    }

    @Test
    @DisplayName("findById returns specialty when it exists")
    void findById_found() {
        Optional<Specialty> specialty = specialtyRepository.findById(1);
        assertThat(specialty).isPresent();
        assertThat(specialty.get().getName()).isEqualTo("radiology");
    }

    @Test
    @DisplayName("findById returns empty when specialty does not exist")
    void findById_notFound() {
        Optional<Specialty> specialty = specialtyRepository.findById(999);
        assertThat(specialty).isEmpty();
    }

    @Test
    @DisplayName("findByNameContainingIgnoreCase returns matching specialties")
    void findByNameContaining_found() {
        List<Specialty> results = specialtyRepository.findByNameContainingIgnoreCase("rad");
        assertThat(results).hasSize(1);
        assertThat(results.get(0).getName()).isEqualTo("radiology");
    }

    @Test
    @DisplayName("findByNameContainingIgnoreCase is case-insensitive")
    void findByNameContaining_caseInsensitive() {
        List<Specialty> results = specialtyRepository.findByNameContainingIgnoreCase("RAD");
        assertThat(results).hasSize(1);
    }

    @Test
    @DisplayName("findByNameContainingIgnoreCase returns empty for no match")
    void findByNameContaining_noMatch() {
        List<Specialty> results = specialtyRepository.findByNameContainingIgnoreCase("xyz");
        assertThat(results).isEmpty();
    }

    @Test
    @DisplayName("existsByNameIgnoreCase returns true for existing name")
    void existsByName_true() {
        assertThat(specialtyRepository.existsByNameIgnoreCase("radiology")).isTrue();
    }

    @Test
    @DisplayName("existsByNameIgnoreCase returns true regardless of case")
    void existsByName_caseInsensitive() {
        assertThat(specialtyRepository.existsByNameIgnoreCase("RADIOLOGY")).isTrue();
    }

    @Test
    @DisplayName("existsByNameIgnoreCase returns false for non-existing name")
    void existsByName_false() {
        assertThat(specialtyRepository.existsByNameIgnoreCase("nonexistent")).isFalse();
    }

    @Test
    @DisplayName("save persists a new specialty")
    void save_persistsNewSpecialty() {
        Specialty specialty = new Specialty();
        specialty.setName("cardiology");
        Specialty saved = specialtyRepository.save(specialty);

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getName()).isEqualTo("cardiology");

        // Verify it can be retrieved
        Optional<Specialty> found = specialtyRepository.findById(saved.getId());
        assertThat(found).isPresent();
    }

    @Test
    @DisplayName("delete removes an existing specialty")
    void delete_removesSpecialty() {
        Specialty specialty = new Specialty();
        specialty.setName("temp-specialty");
        Specialty saved = specialtyRepository.save(specialty);

        specialtyRepository.deleteById(saved.getId());

        assertThat(specialtyRepository.findById(saved.getId())).isEmpty();
    }
}
