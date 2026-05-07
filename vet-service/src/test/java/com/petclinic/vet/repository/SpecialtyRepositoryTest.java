package com.petclinic.vet.repository;

import com.petclinic.vet.entity.Specialty;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class SpecialtyRepositoryTest {

    @Autowired
    private SpecialtyRepository specialtyRepository;

    @Test
    void shouldFindAllSpecialties() {
        List<Specialty> specialties = specialtyRepository.findAll();
        assertThat(specialties).hasSizeGreaterThanOrEqualTo(3);
    }

    @Test
    void shouldFindSpecialtyById() {
        Optional<Specialty> specialty = specialtyRepository.findById(1);
        assertThat(specialty).isPresent();
        assertThat(specialty.get().getName()).isEqualTo("radiology");
    }

    @Test
    void shouldFindByNameContainingIgnoreCase() {
        List<Specialty> results = specialtyRepository.findByNameContainingIgnoreCase("radio");
        assertThat(results).hasSize(1);
        assertThat(results.get(0).getName()).isEqualTo("radiology");
    }

    @Test
    void shouldFindByNameIgnoreCase() {
        List<Specialty> results = specialtyRepository.findByNameIgnoreCase("SURGERY");
        assertThat(results).hasSize(1);
        assertThat(results.get(0).getName()).isEqualTo("surgery");
    }

    @Test
    void shouldSaveNewSpecialty() {
        Specialty specialty = new Specialty("oncology");
        Specialty saved = specialtyRepository.save(specialty);
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getName()).isEqualTo("oncology");
    }

    @Test
    void shouldUpdateSpecialty() {
        Specialty specialty = specialtyRepository.findById(1).orElseThrow();
        specialty.setName("updated-radiology");
        Specialty saved = specialtyRepository.save(specialty);
        assertThat(saved.getName()).isEqualTo("updated-radiology");
    }

    @Test
    void shouldDeleteSpecialty() {
        Specialty specialty = new Specialty("temp");
        Specialty saved = specialtyRepository.save(specialty);
        Integer id = saved.getId();
        specialtyRepository.deleteById(id);
        assertThat(specialtyRepository.findById(id)).isEmpty();
    }

    @Test
    void shouldReturnEmptyForNonExistentId() {
        Optional<Specialty> specialty = specialtyRepository.findById(999);
        assertThat(specialty).isEmpty();
    }
}
