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
@ActiveProfiles("test")
@Import(JpaAuditingConfig.class)
class SpecialtyRepositoryTest {

    @Autowired
    private SpecialtyRepository specialtyRepository;

    @BeforeEach
    void setUp() {
        specialtyRepository.deleteAll();
        Specialty radiology = new Specialty();
        radiology.setName("radiology");
        specialtyRepository.save(radiology);

        Specialty surgery = new Specialty();
        surgery.setName("surgery");
        specialtyRepository.save(surgery);

        Specialty dentistry = new Specialty();
        dentistry.setName("dentistry");
        specialtyRepository.save(dentistry);
    }

    @Test
    void findByNameContainingIgnoreCase_findsMatches() {
        List<Specialty> result = specialtyRepository.findByNameContainingIgnoreCase("rad");
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("radiology");
    }

    @Test
    void findByNameContainingIgnoreCase_caseInsensitive() {
        List<Specialty> result = specialtyRepository.findByNameContainingIgnoreCase("RAD");
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("radiology");
    }

    @Test
    void findByNameContainingIgnoreCase_noMatches() {
        List<Specialty> result = specialtyRepository.findByNameContainingIgnoreCase("xyz");
        assertThat(result).isEmpty();
    }

    @Test
    void existsByNameIgnoreCase_exists() {
        boolean exists = specialtyRepository.existsByNameIgnoreCase("Radiology");
        assertThat(exists).isTrue();
    }

    @Test
    void existsByNameIgnoreCase_notExists() {
        boolean exists = specialtyRepository.existsByNameIgnoreCase("cardiology");
        assertThat(exists).isFalse();
    }

    @Test
    void findAll_returnsAll() {
        List<Specialty> all = specialtyRepository.findAll();
        assertThat(all).hasSize(3);
    }

    @Test
    void save_persistsEntity() {
        Specialty cardiology = new Specialty();
        cardiology.setName("cardiology");
        Specialty saved = specialtyRepository.save(cardiology);

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getName()).isEqualTo("cardiology");
    }

    @Test
    void delete_removesEntity() {
        List<Specialty> before = specialtyRepository.findAll();
        specialtyRepository.delete(before.get(0));
        List<Specialty> after = specialtyRepository.findAll();
        assertThat(after).hasSize(before.size() - 1);
    }
}
