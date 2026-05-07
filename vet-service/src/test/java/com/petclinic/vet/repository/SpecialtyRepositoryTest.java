package com.petclinic.vet.repository;

import com.petclinic.vet.entity.Specialty;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class SpecialtyRepositoryTest {

    @Autowired
    private SpecialtyRepository repository;

    @Test
    void findAll_returnsSeedData() {
        List<Specialty> all = repository.findAll();
        assertThat(all).hasSizeGreaterThanOrEqualTo(3);
    }

    @Test
    void findByNameContainingIgnoreCase_returnsMatches() {
        List<Specialty> result = repository.findByNameContainingIgnoreCase("radio");
        assertThat(result).hasSize(1);
        assertThat(result.getFirst().getName()).isEqualTo("radiology");
    }

    @Test
    void findByNameContainingIgnoreCase_caseInsensitive() {
        List<Specialty> result = repository.findByNameContainingIgnoreCase("SURG");
        assertThat(result).hasSize(1);
        assertThat(result.getFirst().getName()).isEqualTo("surgery");
    }

    @Test
    void findByNameContainingIgnoreCase_noMatch() {
        List<Specialty> result = repository.findByNameContainingIgnoreCase("nonexistent");
        assertThat(result).isEmpty();
    }

    @Test
    void findAllByIdIn_returnsMatchingIds() {
        List<Specialty> all = repository.findAll();
        List<Integer> ids = all.stream().map(Specialty::getId).limit(2).toList();
        List<Specialty> result = repository.findAllByIdIn(ids);
        assertThat(result).hasSize(2);
    }

    @Test
    void save_createsNewSpecialty() {
        Specialty entity = new Specialty();
        entity.setName("oncology");
        Specialty saved = repository.save(entity);
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getName()).isEqualTo("oncology");
    }

    @Test
    void delete_removesSpecialty() {
        Specialty entity = new Specialty();
        entity.setName("temporary");
        Specialty saved = repository.save(entity);
        repository.delete(saved);
        assertThat(repository.findById(saved.getId())).isEmpty();
    }
}
