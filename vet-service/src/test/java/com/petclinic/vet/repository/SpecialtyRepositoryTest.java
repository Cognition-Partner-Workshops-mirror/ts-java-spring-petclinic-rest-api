package com.petclinic.vet.repository;

import com.petclinic.vet.entity.Specialty;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Set;

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
        Specialty specialty = specialtyRepository.findById(1).orElse(null);
        assertThat(specialty).isNotNull();
        assertThat(specialty.getName()).isEqualTo("radiology");
    }

    @Test
    void shouldFindByNameContainingIgnoreCase() {
        List<Specialty> specialties = specialtyRepository.findByNameContainingIgnoreCase("rad");
        assertThat(specialties).hasSize(1);
        assertThat(specialties.get(0).getName()).isEqualTo("radiology");
    }

    @Test
    void shouldFindByNameContainingIgnoreCaseNoMatch() {
        List<Specialty> specialties = specialtyRepository.findByNameContainingIgnoreCase("xyz");
        assertThat(specialties).isEmpty();
    }

    @Test
    void shouldFindByNameIn() {
        List<Specialty> specialties = specialtyRepository.findByNameIn(Set.of("radiology", "surgery"));
        assertThat(specialties).hasSize(2);
    }

    @Test
    void shouldFindByNameInEmpty() {
        List<Specialty> specialties = specialtyRepository.findByNameIn(Set.of("nonexistent"));
        assertThat(specialties).isEmpty();
    }

    @Test
    void shouldSaveSpecialty() {
        Specialty specialty = new Specialty();
        specialty.setName("oncology");
        Specialty saved = specialtyRepository.save(specialty);
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getName()).isEqualTo("oncology");
    }

    @Test
    void shouldDeleteSpecialty() {
        Specialty specialty = new Specialty();
        specialty.setName("temp-specialty");
        Specialty saved = specialtyRepository.save(specialty);
        Integer id = saved.getId();

        specialtyRepository.deleteById(id);
        assertThat(specialtyRepository.findById(id)).isEmpty();
    }
}
