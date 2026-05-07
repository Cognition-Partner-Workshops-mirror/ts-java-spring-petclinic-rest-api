package com.petclinic.vet.repository;

import com.petclinic.vet.entity.Specialty;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class SpecialtyRepositoryTest {

    @Autowired
    private SpecialtyRepository specialtyRepository;

    @Test
    void findAll_returnsSeededSpecialties() {
        List<Specialty> specialties = specialtyRepository.findAll();
        assertThat(specialties).hasSizeGreaterThanOrEqualTo(3);
    }

    @Test
    void findById_returnsSpecialty() {
        Optional<Specialty> specialty = specialtyRepository.findById(1);
        assertThat(specialty).isPresent();
        assertThat(specialty.get().getName()).isEqualTo("radiology");
    }

    @Test
    void findById_returnsEmptyForNonExistent() {
        Optional<Specialty> specialty = specialtyRepository.findById(999);
        assertThat(specialty).isEmpty();
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
    void save_updatesExistingSpecialty() {
        Specialty specialty = specialtyRepository.findById(1).orElseThrow();
        specialty.setName("updated-radiology");
        Specialty updated = specialtyRepository.save(specialty);
        assertThat(updated.getName()).isEqualTo("updated-radiology");
    }

    @Test
    void delete_removesSpecialty() {
        long countBefore = specialtyRepository.count();
        Specialty specialty = new Specialty();
        specialty.setName("temp");
        specialty = specialtyRepository.save(specialty);
        specialtyRepository.delete(specialty);
        specialtyRepository.flush();
        assertThat(specialtyRepository.count()).isEqualTo(countBefore);
    }

    @Test
    void findByNameContainingIgnoreCase_findsMatches() {
        List<Specialty> results = specialtyRepository.findByNameContainingIgnoreCase("radio");
        assertThat(results).hasSize(1);
        assertThat(results.get(0).getName()).isEqualTo("radiology");
    }

    @Test
    void findByNameContainingIgnoreCase_caseInsensitive() {
        List<Specialty> results = specialtyRepository.findByNameContainingIgnoreCase("SURGERY");
        assertThat(results).hasSize(1);
    }

    @Test
    void findByNameContainingIgnoreCase_noMatches() {
        List<Specialty> results = specialtyRepository.findByNameContainingIgnoreCase("nonexistent");
        assertThat(results).isEmpty();
    }
}
