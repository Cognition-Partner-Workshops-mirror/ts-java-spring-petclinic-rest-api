package com.petclinic.vet.repository;

import com.petclinic.vet.entity.Specialty;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
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
    void findById_notFound() {
        Optional<Specialty> specialty = specialtyRepository.findById(999);
        assertThat(specialty).isEmpty();
    }

    @Test
    void findByNameContainingIgnoreCase_findsMatch() {
        List<Specialty> results = specialtyRepository.findByNameContainingIgnoreCase("radio");
        assertThat(results).hasSize(1);
        assertThat(results.get(0).getName()).isEqualTo("radiology");
    }

    @Test
    void findByNameContainingIgnoreCase_caseInsensitive() {
        List<Specialty> results = specialtyRepository.findByNameContainingIgnoreCase("SURG");
        assertThat(results).hasSize(1);
        assertThat(results.get(0).getName()).isEqualTo("surgery");
    }

    @Test
    void findByNameContainingIgnoreCase_noMatch() {
        List<Specialty> results = specialtyRepository.findByNameContainingIgnoreCase("cardiology");
        assertThat(results).isEmpty();
    }

    @Test
    void findByNameIn_findsMultiple() {
        List<Specialty> results = specialtyRepository.findByNameIn(Set.of("radiology", "surgery"));
        assertThat(results).hasSize(2);
    }

    @Test
    void findByNameIn_noMatch() {
        List<Specialty> results = specialtyRepository.findByNameIn(Set.of("unknown"));
        assertThat(results).isEmpty();
    }

    @Test
    void save_createsNewSpecialty() {
        Specialty specialty = new Specialty();
        specialty.setName("oncology");
        Specialty saved = specialtyRepository.save(specialty);
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getName()).isEqualTo("oncology");
    }

    @Test
    void delete_removesSpecialty() {
        Specialty specialty = new Specialty();
        specialty.setName("toDelete");
        Specialty saved = specialtyRepository.save(specialty);
        specialtyRepository.delete(saved);
        assertThat(specialtyRepository.findById(saved.getId())).isEmpty();
    }

    @Test
    void save_updatesExistingSpecialty() {
        Specialty specialty = specialtyRepository.findById(1).orElseThrow();
        specialty.setName("updated-radiology");
        Specialty saved = specialtyRepository.save(specialty);
        assertThat(saved.getName()).isEqualTo("updated-radiology");
    }
}
