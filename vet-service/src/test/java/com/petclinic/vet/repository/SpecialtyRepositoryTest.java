package com.petclinic.vet.repository;

import com.petclinic.vet.entity.Specialty;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Repository tests for SpecialtyRepository using an H2 in-memory database.
 * Flyway seeds the database with 3 specialties (radiology, surgery, dentistry).
 */
@DataJpaTest
class SpecialtyRepositoryTest {

    @Autowired
    private SpecialtyRepository specialtyRepository;

    @Test
    void findAll_returnsSeededSpecialties() {
        List<Specialty> all = specialtyRepository.findAll();
        // Seed data has 3 specialties
        assertThat(all).hasSizeGreaterThanOrEqualTo(3);
    }

    @Test
    void findById_existingId_returnsSpecialty() {
        Optional<Specialty> result = specialtyRepository.findById(1);
        assertThat(result).isPresent();
        assertThat(result.get().getName()).isEqualTo("radiology");
    }

    @Test
    void findById_nonExistingId_returnsEmpty() {
        Optional<Specialty> result = specialtyRepository.findById(999);
        assertThat(result).isEmpty();
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
        List<Specialty> results = specialtyRepository.findByNameContainingIgnoreCase("cardiology");
        assertThat(results).isEmpty();
    }

    @Test
    void save_createsNewSpecialty() {
        Specialty specialty = new Specialty();
        specialty.setName("cardiology");
        Specialty saved = specialtyRepository.save(specialty);

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getName()).isEqualTo("cardiology");
    }

    @Test
    void delete_removesSpecialty() {
        Specialty specialty = new Specialty();
        specialty.setName("temporary");
        Specialty saved = specialtyRepository.save(specialty);
        Integer id = saved.getId();

        specialtyRepository.delete(saved);

        assertThat(specialtyRepository.findById(id)).isEmpty();
    }
}
