package com.petclinic.vet.repository;

import com.petclinic.vet.config.JpaAuditingConfig;
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
@Import(JpaAuditingConfig.class)
@ActiveProfiles("test")
class SpecialtyRepositoryTest {

    @Autowired
    private SpecialtyRepository specialtyRepository;

    @BeforeEach
    void setUp() {
        specialtyRepository.deleteAll();
        Specialty radiology = new Specialty();
        radiology.setName("radiology");
        Specialty surgery = new Specialty();
        surgery.setName("surgery");
        Specialty dentistry = new Specialty();
        dentistry.setName("dentistry");
        specialtyRepository.saveAll(List.of(radiology, surgery, dentistry));
    }

    @Test
    void findByNameContainingIgnoreCase_found() {
        List<Specialty> results = specialtyRepository.findByNameContainingIgnoreCase("rad");
        assertThat(results).hasSize(1);
        assertThat(results.get(0).getName()).isEqualTo("radiology");
    }

    @Test
    void findByNameContainingIgnoreCase_caseInsensitive() {
        List<Specialty> results = specialtyRepository.findByNameContainingIgnoreCase("RAD");
        assertThat(results).hasSize(1);
    }

    @Test
    void findByNameContainingIgnoreCase_noMatch() {
        List<Specialty> results = specialtyRepository.findByNameContainingIgnoreCase("cardio");
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
        List<Specialty> results = specialtyRepository.findAll();
        assertThat(results).hasSize(3);
    }
}
