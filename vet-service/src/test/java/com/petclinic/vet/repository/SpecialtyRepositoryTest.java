package com.petclinic.vet.repository;

import com.petclinic.vet.entity.Specialty;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Sql(statements = {
    "DELETE FROM vet_specialties",
    "DELETE FROM vets",
    "DELETE FROM specialties",
    "ALTER TABLE specialties ALTER COLUMN id RESTART WITH 100",
    "INSERT INTO specialties (name, created_at, updated_at) VALUES ('radiology', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)",
    "INSERT INTO specialties (name, created_at, updated_at) VALUES ('surgery', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)",
    "INSERT INTO specialties (name, created_at, updated_at) VALUES ('dentistry', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)"
}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
class SpecialtyRepositoryTest {

    @Autowired
    private SpecialtyRepository specialtyRepository;

    @Test
    void findByNameContainingIgnoreCase_returnsMatches() {
        List<Specialty> result = specialtyRepository.findByNameContainingIgnoreCase("rad");
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("radiology");
    }

    @Test
    void findByNameContainingIgnoreCase_caseInsensitive() {
        List<Specialty> result = specialtyRepository.findByNameContainingIgnoreCase("SURGERY");
        assertThat(result).hasSize(1);
    }

    @Test
    void findByNameContainingIgnoreCase_partialMatch() {
        List<Specialty> result = specialtyRepository.findByNameContainingIgnoreCase("ry");
        assertThat(result).hasSize(2); // surgery, dentistry
    }

    @Test
    void findByNameContainingIgnoreCase_noMatches() {
        List<Specialty> result = specialtyRepository.findByNameContainingIgnoreCase("cardiology");
        assertThat(result).isEmpty();
    }

    @Test
    void existsByNameIgnoreCase_exists() {
        assertThat(specialtyRepository.existsByNameIgnoreCase("radiology")).isTrue();
        assertThat(specialtyRepository.existsByNameIgnoreCase("RADIOLOGY")).isTrue();
    }

    @Test
    void existsByNameIgnoreCase_notExists() {
        assertThat(specialtyRepository.existsByNameIgnoreCase("cardiology")).isFalse();
    }

    @Test
    void findAll_returnsAllSpecialties() {
        List<Specialty> result = specialtyRepository.findAll();
        assertThat(result).hasSize(3);
    }

    @Test
    void save_setsAuditFields() {
        Specialty specialty = new Specialty();
        specialty.setName("cardiology");
        Specialty saved = specialtyRepository.save(specialty);

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getCreatedAt()).isNotNull();
        assertThat(saved.getUpdatedAt()).isNotNull();
    }
}
