package com.petclinic.vet.repository;

import com.petclinic.vet.config.JpaAuditingConfig;
import com.petclinic.vet.entity.Specialty;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import(JpaAuditingConfig.class)
class SpecialtyRepositoryTest {

    @Autowired
    private SpecialtyRepository specialtyRepository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    void findByNameIgnoreCase_existingName_returnsSpecialty() {
        Optional<Specialty> result = specialtyRepository.findByNameIgnoreCase("RADIOLOGY");
        assertThat(result).isPresent();
        assertThat(result.get().getName()).isEqualTo("radiology");
    }

    @Test
    void findByNameIgnoreCase_nonExistingName_returnsEmpty() {
        Optional<Specialty> result = specialtyRepository.findByNameIgnoreCase("oncology");
        assertThat(result).isEmpty();
    }

    @Test
    void existsByNameIgnoreCase_existingName_returnsTrue() {
        assertThat(specialtyRepository.existsByNameIgnoreCase("Surgery")).isTrue();
    }

    @Test
    void existsByNameIgnoreCase_nonExistingName_returnsFalse() {
        assertThat(specialtyRepository.existsByNameIgnoreCase("oncology")).isFalse();
    }

    @Test
    void findAll_returnsSeededSpecialties() {
        assertThat(specialtyRepository.findAll()).hasSize(3);
    }

    @Test
    void save_newSpecialty_persistsAndGeneratesId() {
        Specialty specialty = new Specialty("oncology");
        Specialty saved = specialtyRepository.saveAndFlush(specialty);
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getName()).isEqualTo("oncology");
        assertThat(saved.getCreatedAt()).isNotNull();
    }

    @Test
    void deleteById_unreferencedSpecialty_removesIt() {
        Specialty extra = new Specialty("cardiology");
        extra = specialtyRepository.saveAndFlush(extra);
        entityManager.clear();

        long countBefore = specialtyRepository.count();
        specialtyRepository.deleteById(extra.getId());
        specialtyRepository.flush();
        assertThat(specialtyRepository.count()).isEqualTo(countBefore - 1);
    }
}
