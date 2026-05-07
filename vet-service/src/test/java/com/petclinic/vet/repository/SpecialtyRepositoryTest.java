package com.petclinic.vet.repository;

import com.petclinic.vet.entity.Specialty;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import com.petclinic.vet.config.JpaAuditingConfig;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import(JpaAuditingConfig.class)
@ActiveProfiles("test")
class SpecialtyRepositoryTest {

    @Autowired
    private SpecialtyRepository specialtyRepository;

    @Test
    void findByNameIgnoreCase_found() {
        Optional<Specialty> result = specialtyRepository.findByNameIgnoreCase("RADIOLOGY");
        assertThat(result).isPresent();
        assertThat(result.get().getName()).isEqualTo("radiology");
    }

    @Test
    void findByNameIgnoreCase_notFound() {
        Optional<Specialty> result = specialtyRepository.findByNameIgnoreCase("nonexistent");
        assertThat(result).isEmpty();
    }

    @Test
    void searchByName_partialMatch() {
        List<Specialty> result = specialtyRepository.searchByName("rad");
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("radiology");
    }

    @Test
    void searchByName_noMatch() {
        List<Specialty> result = specialtyRepository.searchByName("xyz");
        assertThat(result).isEmpty();
    }

    @Test
    void existsByNameIgnoreCase_true() {
        assertThat(specialtyRepository.existsByNameIgnoreCase("Surgery")).isTrue();
    }

    @Test
    void existsByNameIgnoreCase_false() {
        assertThat(specialtyRepository.existsByNameIgnoreCase("nonexistent")).isFalse();
    }

    @Test
    void findAll_returnsSeeded() {
        List<Specialty> all = specialtyRepository.findAll();
        assertThat(all).hasSizeGreaterThanOrEqualTo(3);
    }

    @Test
    void saveAndRetrieve() {
        Specialty cardiology = new Specialty();
        cardiology.setName("cardiology");
        Specialty saved = specialtyRepository.save(cardiology);

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getName()).isEqualTo("cardiology");

        Optional<Specialty> retrieved = specialtyRepository.findById(saved.getId());
        assertThat(retrieved).isPresent();
    }
}
