package com.petclinic.vet.repository;

import com.petclinic.vet.entity.Specialty;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class SpecialtyRepositoryTest {

    @Autowired
    private SpecialtyRepository repository;

    @Test
    void findAll_returnsSeededData() {
        List<Specialty> specialties = repository.findAll();
        assertThat(specialties).hasSizeGreaterThanOrEqualTo(3);
    }

    @Test
    void findByNameIgnoreCase_found() {
        Optional<Specialty> result = repository.findByNameIgnoreCase("RADIOLOGY");
        assertThat(result).isPresent();
        assertThat(result.get().getName()).isEqualTo("radiology");
    }

    @Test
    void findByNameIgnoreCase_notFound() {
        Optional<Specialty> result = repository.findByNameIgnoreCase("nonexistent");
        assertThat(result).isEmpty();
    }

    @Test
    void findByNameContainingIgnoreCase_found() {
        List<Specialty> results = repository.findByNameContainingIgnoreCase("sur");
        assertThat(results).hasSize(1);
        assertThat(results.get(0).getName()).isEqualTo("surgery");
    }

    @Test
    void findByNameContainingIgnoreCase_notFound() {
        List<Specialty> results = repository.findByNameContainingIgnoreCase("zzz");
        assertThat(results).isEmpty();
    }

    @Test
    void save_createsNewSpecialty() {
        Specialty specialty = new Specialty();
        specialty.setName("oncology");
        repository.saveAndFlush(specialty);
        assertThat(specialty.getId()).isNotNull();
        assertThat(specialty.getName()).isEqualTo("oncology");
    }

    @Test
    void deleteById_removesSpecialty() {
        Specialty existing = repository.findById(3).orElseThrow();
        repository.delete(existing);
        repository.flush();
        assertThat(repository.findById(3)).isEmpty();
    }
}
