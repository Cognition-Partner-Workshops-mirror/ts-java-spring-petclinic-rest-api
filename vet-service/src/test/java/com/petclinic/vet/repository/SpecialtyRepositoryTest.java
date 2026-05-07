package com.petclinic.vet.repository;

import com.petclinic.vet.entity.Specialty;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class SpecialtyRepositoryTest {

    @Autowired
    private SpecialtyRepository repository;

    private Specialty radiology;

    @BeforeEach
    void setUp() {
        repository.deleteAll();
        radiology = new Specialty();
        radiology.setName("radiology");
        radiology = repository.save(radiology);
    }

    @Test
    void findAll_returnsSpecialties() {
        Specialty surgery = new Specialty();
        surgery.setName("surgery");
        repository.save(surgery);

        List<Specialty> result = repository.findAll();

        assertThat(result).hasSize(2);
    }

    @Test
    void findById_returnsSpecialty() {
        Optional<Specialty> result = repository.findById(radiology.getId());

        assertThat(result).isPresent();
        assertThat(result.get().getName()).isEqualTo("radiology");
    }

    @Test
    void findByNameContainingIgnoreCase_returnsMatches() {
        Specialty surgery = new Specialty();
        surgery.setName("surgery");
        repository.save(surgery);

        List<Specialty> result = repository.findByNameContainingIgnoreCase("RAD");

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("radiology");
    }

    @Test
    void findByNameContainingIgnoreCase_returnsEmptyForNoMatch() {
        List<Specialty> result = repository.findByNameContainingIgnoreCase("xyz");

        assertThat(result).isEmpty();
    }

    @Test
    void save_setsAuditFields() {
        assertThat(radiology.getCreatedAt()).isNotNull();
        assertThat(radiology.getUpdatedAt()).isNotNull();
    }

    @Test
    void delete_removesSpecialty() {
        repository.delete(radiology);

        assertThat(repository.findById(radiology.getId())).isEmpty();
    }
}
