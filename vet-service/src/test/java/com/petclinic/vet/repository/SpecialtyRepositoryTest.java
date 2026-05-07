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
        List<Specialty> result = specialtyRepository.findByNameContainingIgnoreCase("radio");
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("radiology");
    }

    @Test
    void shouldReturnEmptyForNonExistentName() {
        List<Specialty> result = specialtyRepository.findByNameContainingIgnoreCase("nonexistent");
        assertThat(result).isEmpty();
    }

    @Test
    void shouldSaveNewSpecialty() {
        Specialty specialty = new Specialty();
        specialty.setName("oncology");
        Specialty saved = specialtyRepository.save(specialty);
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getName()).isEqualTo("oncology");
    }

    @Test
    void shouldDeleteSpecialty() {
        Specialty specialty = new Specialty();
        specialty.setName("to-delete");
        Specialty saved = specialtyRepository.save(specialty);
        specialtyRepository.deleteById(saved.getId());
        assertThat(specialtyRepository.findById(saved.getId())).isEmpty();
    }
}
