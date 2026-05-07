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
    private SpecialtyRepository repository;

    @Test
    void shouldFindAll() {
        List<Specialty> specialties = repository.findAll();
        assertThat(specialties).isNotEmpty();
        assertThat(specialties).hasSizeGreaterThanOrEqualTo(3);
    }

    @Test
    void shouldFindById() {
        Optional<Specialty> specialty = repository.findById(1);
        assertThat(specialty).isPresent();
        assertThat(specialty.get().getName()).isEqualTo("radiology");
    }

    @Test
    void shouldReturnEmptyForNonExistentId() {
        Optional<Specialty> specialty = repository.findById(999);
        assertThat(specialty).isEmpty();
    }

    @Test
    void shouldFindByNameContainingIgnoreCase() {
        List<Specialty> results = repository.findByNameContainingIgnoreCase("radio");
        assertThat(results).hasSize(1);
        assertThat(results.get(0).getName()).isEqualTo("radiology");
    }

    @Test
    void shouldFindByNameCaseInsensitive() {
        List<Specialty> results = repository.findByNameContainingIgnoreCase("SURG");
        assertThat(results).hasSize(1);
        assertThat(results.get(0).getName()).isEqualTo("surgery");
    }

    @Test
    void shouldReturnEmptyForUnknownName() {
        List<Specialty> results = repository.findByNameContainingIgnoreCase("nonexistent");
        assertThat(results).isEmpty();
    }

    @Test
    void shouldSaveNewSpecialty() {
        Specialty specialty = new Specialty();
        specialty.setName("oncology");
        Specialty saved = repository.save(specialty);

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getName()).isEqualTo("oncology");
        assertThat(saved.getCreatedAt()).isNotNull();
        assertThat(saved.getUpdatedAt()).isNotNull();
    }

    @Test
    void shouldDeleteSpecialty() {
        Specialty specialty = new Specialty();
        specialty.setName("temp");
        Specialty saved = repository.save(specialty);
        Integer id = saved.getId();

        repository.deleteById(id);

        assertThat(repository.findById(id)).isEmpty();
    }
}
