package com.petclinic.vet.repository;

import com.petclinic.vet.entity.Specialty;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class SpecialtyRepositoryTest {

    @Autowired
    private SpecialtyRepository specialtyRepository;

    @BeforeEach
    void setUp() {
        specialtyRepository.deleteAll();
    }

    @Test
    void shouldSaveAndFindById() {
        Specialty specialty = new Specialty("radiology");
        specialty.setCreatedAt(LocalDateTime.now());
        specialty.setUpdatedAt(LocalDateTime.now());
        Specialty saved = specialtyRepository.save(specialty);

        assertThat(saved.getId()).isNotNull();
        assertThat(specialtyRepository.findById(saved.getId())).isPresent();
    }

    @Test
    void shouldFindByNameContainingIgnoreCase() {
        Specialty s1 = new Specialty("radiology");
        s1.setCreatedAt(LocalDateTime.now());
        s1.setUpdatedAt(LocalDateTime.now());
        Specialty s2 = new Specialty("surgery");
        s2.setCreatedAt(LocalDateTime.now());
        s2.setUpdatedAt(LocalDateTime.now());
        Specialty s3 = new Specialty("dentistry");
        s3.setCreatedAt(LocalDateTime.now());
        s3.setUpdatedAt(LocalDateTime.now());
        specialtyRepository.saveAll(List.of(s1, s2, s3));

        List<Specialty> result = specialtyRepository.findByNameContainingIgnoreCase("RAD");
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("radiology");
    }

    @Test
    void shouldFindByIdIn() {
        Specialty s1 = new Specialty("radiology");
        s1.setCreatedAt(LocalDateTime.now());
        s1.setUpdatedAt(LocalDateTime.now());
        Specialty s2 = new Specialty("surgery");
        s2.setCreatedAt(LocalDateTime.now());
        s2.setUpdatedAt(LocalDateTime.now());
        specialtyRepository.saveAll(List.of(s1, s2));

        List<Specialty> all = specialtyRepository.findAll();
        List<Integer> ids = all.stream().map(Specialty::getId).toList();

        List<Specialty> result = specialtyRepository.findByIdIn(ids);
        assertThat(result).hasSize(2);
    }

    @Test
    void shouldFindAll() {
        Specialty s1 = new Specialty("radiology");
        s1.setCreatedAt(LocalDateTime.now());
        s1.setUpdatedAt(LocalDateTime.now());
        Specialty s2 = new Specialty("surgery");
        s2.setCreatedAt(LocalDateTime.now());
        s2.setUpdatedAt(LocalDateTime.now());
        specialtyRepository.saveAll(List.of(s1, s2));

        assertThat(specialtyRepository.findAll()).hasSize(2);
    }

    @Test
    void shouldDeleteSpecialty() {
        Specialty specialty = new Specialty("radiology");
        specialty.setCreatedAt(LocalDateTime.now());
        specialty.setUpdatedAt(LocalDateTime.now());
        Specialty saved = specialtyRepository.save(specialty);

        specialtyRepository.deleteById(saved.getId());
        assertThat(specialtyRepository.findById(saved.getId())).isEmpty();
    }
}
