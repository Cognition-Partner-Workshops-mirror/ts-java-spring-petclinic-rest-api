package com.petclinic.vet.repository;

import com.petclinic.vet.entity.Specialty;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Repository tests for SpecialtyRepository using @DataJpaTest.
 * Tests custom query methods with an H2 in-memory database.
 */
@DataJpaTest
@ActiveProfiles("test")
class SpecialtyRepositoryTest {

    @Autowired
    private SpecialtyRepository specialtyRepository;

    @Test
    void findByNameIgnoreCase_findsExactMatch() {
        Optional<Specialty> result = specialtyRepository.findByNameIgnoreCase("radiology");

        assertThat(result).isPresent();
        assertThat(result.get().getName()).isEqualTo("radiology");
    }

    @Test
    void findByNameIgnoreCase_caseInsensitive() {
        Optional<Specialty> result = specialtyRepository.findByNameIgnoreCase("RADIOLOGY");

        assertThat(result).isPresent();
        assertThat(result.get().getName()).isEqualTo("radiology");
    }

    @Test
    void findByNameIgnoreCase_returnsEmptyForNoMatch() {
        Optional<Specialty> result = specialtyRepository.findByNameIgnoreCase("nonexistent");

        assertThat(result).isEmpty();
    }

    @Test
    void findByNameContainingIgnoreCase_findsPartialMatch() {
        List<Specialty> results = specialtyRepository.findByNameContainingIgnoreCase("radio");

        assertThat(results).hasSize(1);
        assertThat(results.get(0).getName()).isEqualTo("radiology");
    }

    @Test
    void findByNameContainingIgnoreCase_returnsEmptyForNoMatch() {
        List<Specialty> results = specialtyRepository.findByNameContainingIgnoreCase("xyz");

        assertThat(results).isEmpty();
    }

    @Test
    void findAll_returnsAllSeededSpecialties() {
        List<Specialty> all = specialtyRepository.findAll();

        // 3 specialties seeded in V2 migration
        assertThat(all).hasSize(3);
    }

    @Test
    void save_persistsNewSpecialty() {
        Specialty newSpecialty = new Specialty();
        newSpecialty.setName("cardiology");

        Specialty saved = specialtyRepository.save(newSpecialty);

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getName()).isEqualTo("cardiology");
    }

    @Test
    void delete_removesSpecialty() {
        Specialty specialty = new Specialty();
        specialty.setName("temporary");
        specialty = specialtyRepository.save(specialty);

        specialtyRepository.delete(specialty);

        Optional<Specialty> found = specialtyRepository.findById(specialty.getId());
        assertThat(found).isEmpty();
    }
}
