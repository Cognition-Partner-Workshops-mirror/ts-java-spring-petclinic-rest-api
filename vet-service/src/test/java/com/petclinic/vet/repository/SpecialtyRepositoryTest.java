package com.petclinic.vet.repository;

import com.petclinic.vet.config.JpaAuditConfig;
import com.petclinic.vet.entity.Specialty;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
@Import(JpaAuditConfig.class)
class SpecialtyRepositoryTest {

    @Autowired
    private SpecialtyRepository specialtyRepository;

    @BeforeEach
    void setUp() {
        specialtyRepository.deleteAll();
        specialtyRepository.save(createSpecialty("radiology"));
        specialtyRepository.save(createSpecialty("surgery"));
        specialtyRepository.save(createSpecialty("dentistry"));
    }

    @Test
    void findByNameContainingIgnoreCase_returnsMatches() {
        List<Specialty> results = specialtyRepository.findByNameContainingIgnoreCase("rad");
        assertThat(results).hasSize(1);
        assertThat(results.get(0).getName()).isEqualTo("radiology");
    }

    @Test
    void findByNameContainingIgnoreCase_caseInsensitive() {
        List<Specialty> results = specialtyRepository.findByNameContainingIgnoreCase("SURGERY");
        assertThat(results).hasSize(1);
    }

    @Test
    void findByNameContainingIgnoreCase_noMatch() {
        List<Specialty> results = specialtyRepository.findByNameContainingIgnoreCase("cardiology");
        assertThat(results).isEmpty();
    }

    @Test
    void existsByNameIgnoreCase_true() {
        assertThat(specialtyRepository.existsByNameIgnoreCase("RADIOLOGY")).isTrue();
    }

    @Test
    void existsByNameIgnoreCase_false() {
        assertThat(specialtyRepository.existsByNameIgnoreCase("cardiology")).isFalse();
    }

    @Test
    void findAll_returnsAll() {
        assertThat(specialtyRepository.findAll()).hasSize(3);
    }

    @Test
    void save_setsAuditFields() {
        Specialty s = new Specialty();
        s.setName("test");
        Specialty saved = specialtyRepository.save(s);

        assertThat(saved.getCreatedAt()).isNotNull();
        assertThat(saved.getUpdatedAt()).isNotNull();
    }

    private Specialty createSpecialty(String name) {
        Specialty s = new Specialty();
        s.setName(name);
        return s;
    }
}
