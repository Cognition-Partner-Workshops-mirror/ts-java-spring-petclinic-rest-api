package com.petclinic.vet.repository;

import com.petclinic.vet.config.JpaAuditConfig;
import com.petclinic.vet.entity.Specialty;
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
 * Uses H2 in-memory database with Flyway migrations and seed data.
 */
@DataJpaTest
@ActiveProfiles("test")
@Import(JpaAuditConfig.class)
class SpecialtyRepositoryTest {

    @Autowired
    private SpecialtyRepository specialtyRepository;

    @Test
    void findAll_returnsSeedSpecialties() {
        // Seed data from V4 migration includes 3 specialties
        List<Specialty> specialties = specialtyRepository.findAll();
        assertThat(specialties).hasSizeGreaterThanOrEqualTo(3);
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
    void findByNameIgnoreCase_existingName_returnsSpecialty() {
        List<Specialty> result = specialtyRepository.findByNameIgnoreCase("RADIOLOGY");
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("radiology");
    }

    @Test
    void findByNameIgnoreCase_nonExistingName_returnsEmpty() {
        List<Specialty> result = specialtyRepository.findByNameIgnoreCase("nonexistent");
        assertThat(result).isEmpty();
    }

    @Test
    void searchByName_partialMatch_returnsMatching() {
        List<Specialty> result = specialtyRepository.searchByName("rad");
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("radiology");
    }

    @Test
    void searchByName_noMatch_returnsEmpty() {
        List<Specialty> result = specialtyRepository.searchByName("xyz");
        assertThat(result).isEmpty();
    }

    @Test
    void existsByNameIgnoreCase_existingName_returnsTrue() {
        boolean exists = specialtyRepository.existsByNameIgnoreCase("surgery");
        assertThat(exists).isTrue();
    }

    @Test
    void existsByNameIgnoreCase_nonExistingName_returnsFalse() {
        boolean exists = specialtyRepository.existsByNameIgnoreCase("nonexistent");
        assertThat(exists).isFalse();
    }

    @Test
    void save_newSpecialty_assignsId() {
        Specialty specialty = new Specialty();
        specialty.setName("oncology");
        Specialty saved = specialtyRepository.save(specialty);

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getName()).isEqualTo("oncology");
    }

    @Test
    void delete_existingSpecialty_removesFromDatabase() {
        Specialty specialty = new Specialty();
        specialty.setName("temporary");
        Specialty saved = specialtyRepository.save(specialty);

        specialtyRepository.delete(saved);

        Optional<Specialty> result = specialtyRepository.findById(saved.getId());
        assertThat(result).isEmpty();
    }
}
