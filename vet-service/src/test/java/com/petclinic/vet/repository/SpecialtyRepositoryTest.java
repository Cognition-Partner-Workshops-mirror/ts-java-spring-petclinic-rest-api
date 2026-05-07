package com.petclinic.vet.repository;

import com.petclinic.vet.entity.Specialty;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import com.petclinic.vet.config.JpaAuditingConfig;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Repository tests for SpecialtyRepository using @DataJpaTest.
 * Uses H2 in-memory database with Flyway seed data.
 */
@DataJpaTest
@ActiveProfiles("test")
@Import(JpaAuditingConfig.class)
class SpecialtyRepositoryTest {

    @Autowired
    private SpecialtyRepository specialtyRepository;

    @Test
    void findAll_returnsSeededSpecialties() {
        // V2 seed data includes 3 specialties: radiology, surgery, dentistry
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
    void findByNameContainingIgnoreCase_matchingName_returnsResults() {
        List<Specialty> results = specialtyRepository.findByNameContainingIgnoreCase("radio");
        assertThat(results).hasSize(1);
        assertThat(results.get(0).getName()).isEqualTo("radiology");
    }

    @Test
    void findByNameContainingIgnoreCase_caseInsensitive_returnsResults() {
        List<Specialty> results = specialtyRepository.findByNameContainingIgnoreCase("SURG");
        assertThat(results).hasSize(1);
        assertThat(results.get(0).getName()).isEqualTo("surgery");
    }

    @Test
    void findByNameContainingIgnoreCase_noMatch_returnsEmpty() {
        List<Specialty> results = specialtyRepository.findByNameContainingIgnoreCase("cardiology");
        assertThat(results).isEmpty();
    }

    @Test
    void save_newSpecialty_persistsAndGeneratesId() {
        Specialty specialty = new Specialty();
        specialty.setName("dermatology");
        Specialty saved = specialtyRepository.save(specialty);

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getName()).isEqualTo("dermatology");

        Optional<Specialty> found = specialtyRepository.findById(saved.getId());
        assertThat(found).isPresent();
    }

    @Test
    void delete_existingSpecialty_removesFromDatabase() {
        Specialty specialty = new Specialty();
        specialty.setName("ophthalmology");
        Specialty saved = specialtyRepository.save(specialty);
        Integer id = saved.getId();

        specialtyRepository.deleteById(id);

        assertThat(specialtyRepository.findById(id)).isEmpty();
    }
}
