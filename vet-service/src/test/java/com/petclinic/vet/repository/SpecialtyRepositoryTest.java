package com.petclinic.vet.repository;

import com.petclinic.vet.entity.Specialty;
import org.junit.jupiter.api.BeforeEach;
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

    @BeforeEach
    void setUp() {
        repository.deleteAll();
        Specialty radiology = new Specialty();
        radiology.setName("radiology");
        repository.save(radiology);

        Specialty surgery = new Specialty();
        surgery.setName("surgery");
        repository.save(surgery);

        Specialty dentistry = new Specialty();
        dentistry.setName("dentistry");
        repository.save(dentistry);
    }

    @Test
    void findByNameIgnoreCase_found() {
        Optional<Specialty> result = repository.findByNameIgnoreCase("RADIOLOGY");
        assertThat(result).isPresent();
        assertThat(result.get().getName()).isEqualTo("radiology");
    }

    @Test
    void findByNameIgnoreCase_notFound() {
        Optional<Specialty> result = repository.findByNameIgnoreCase("cardiology");
        assertThat(result).isEmpty();
    }

    @Test
    void findByNameContainingIgnoreCase() {
        List<Specialty> result = repository.findByNameContainingIgnoreCase("olog");
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("radiology");
    }

    @Test
    void findAll_returnsAll() {
        List<Specialty> result = repository.findAll();
        assertThat(result).hasSize(3);
    }

    @Test
    void save_setsAuditFields() {
        Specialty s = new Specialty();
        s.setName("oncology");
        Specialty saved = repository.save(s);
        repository.flush();

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getCreatedAt()).isNotNull();
        assertThat(saved.getUpdatedAt()).isNotNull();
    }
}
