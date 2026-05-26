package com.petclinic.vet.repository;

import com.petclinic.vet.entity.Specialty;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Repository tests for {@link SpecialtyRepository} using @DataJpaTest.
 * Runs against the H2 database with Flyway migrations (including seed data).
 */
@DataJpaTest
class SpecialtyRepositoryTest {

    @Autowired
    private SpecialtyRepository specialtyRepository;

    @Test
    @DisplayName("findAll returns the seeded specialties")
    void findAll_returnsSeedData() {
        List<Specialty> all = specialtyRepository.findAll();
        // V2 seed data inserts 3 specialties: radiology, surgery, dentistry
        assertThat(all).hasSizeGreaterThanOrEqualTo(3);
    }

    @Test
    @DisplayName("findById returns a seeded specialty")
    void findById_returnsSeedSpecialty() {
        Optional<Specialty> opt = specialtyRepository.findById(1);
        assertThat(opt).isPresent();
        assertThat(opt.get().getName()).isEqualTo("radiology");
    }

    @Test
    @DisplayName("findById returns empty for a non-existent ID")
    void findById_returnsEmpty() {
        Optional<Specialty> opt = specialtyRepository.findById(999);
        assertThat(opt).isEmpty();
    }

    @Test
    @DisplayName("save persists a new specialty with audit fields populated")
    void save_persistsNewSpecialty() {
        Specialty specialty = new Specialty();
        specialty.setName("cardiology");
        Specialty saved = specialtyRepository.save(specialty);

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getName()).isEqualTo("cardiology");
        assertThat(saved.getCreatedAt()).isNotNull();
        assertThat(saved.getUpdatedAt()).isNotNull();
    }

    @Test
    @DisplayName("findByNameContainingIgnoreCase matches partial names")
    void findByNameContaining_matchesPartial() {
        List<Specialty> results = specialtyRepository.findByNameContainingIgnoreCase("rad");
        assertThat(results).isNotEmpty();
        assertThat(results.get(0).getName()).containsIgnoringCase("rad");
    }

    @Test
    @DisplayName("findByNameContainingIgnoreCase returns empty for no match")
    void findByNameContaining_noMatch() {
        List<Specialty> results = specialtyRepository.findByNameContainingIgnoreCase("zzz");
        assertThat(results).isEmpty();
    }

    @Test
    @DisplayName("existsByNameIgnoreCase returns true for existing name")
    void existsByName_existingName() {
        assertThat(specialtyRepository.existsByNameIgnoreCase("radiology")).isTrue();
        assertThat(specialtyRepository.existsByNameIgnoreCase("RADIOLOGY")).isTrue();
    }

    @Test
    @DisplayName("existsByNameIgnoreCase returns false for non-existing name")
    void existsByName_nonExistingName() {
        assertThat(specialtyRepository.existsByNameIgnoreCase("nonexistent")).isFalse();
    }

    @Test
    @DisplayName("delete removes the specialty")
    void delete_removesSpecialty() {
        Specialty specialty = new Specialty();
        specialty.setName("temp");
        Specialty saved = specialtyRepository.save(specialty);

        specialtyRepository.delete(saved);
        specialtyRepository.flush();

        assertThat(specialtyRepository.findById(saved.getId())).isEmpty();
    }
}
