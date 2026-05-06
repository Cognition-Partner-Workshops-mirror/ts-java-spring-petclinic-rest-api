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
    void findAll_returnsSeedData() {
        List<Specialty> specialties = repository.findAll();
        assertThat(specialties).hasSizeGreaterThanOrEqualTo(3);
    }

    @Test
    void findById_found() {
        Optional<Specialty> result = repository.findById(1);
        assertThat(result).isPresent();
        assertThat(result.get().getName()).isEqualTo("radiology");
    }

    @Test
    void findById_notFound() {
        Optional<Specialty> result = repository.findById(999);
        assertThat(result).isEmpty();
    }

    @Test
    void findByNameContainingIgnoreCase_found() {
        List<Specialty> results = repository.findByNameContainingIgnoreCase("radio");
        assertThat(results).isNotEmpty();
        assertThat(results.get(0).getName()).isEqualTo("radiology");
    }

    @Test
    void findByNameContainingIgnoreCase_notFound() {
        List<Specialty> results = repository.findByNameContainingIgnoreCase("nonexistent");
        assertThat(results).isEmpty();
    }

    @Test
    void save_newSpecialty() {
        Specialty specialty = new Specialty();
        specialty.setName("oncology");
        Specialty saved = repository.save(specialty);
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getName()).isEqualTo("oncology");
    }

    @Test
    void delete_existingSpecialty() {
        Specialty specialty = new Specialty();
        specialty.setName("toDelete");
        Specialty saved = repository.save(specialty);

        repository.delete(saved);
        repository.flush();

        Optional<Specialty> result = repository.findById(saved.getId());
        assertThat(result).isEmpty();
    }
}
