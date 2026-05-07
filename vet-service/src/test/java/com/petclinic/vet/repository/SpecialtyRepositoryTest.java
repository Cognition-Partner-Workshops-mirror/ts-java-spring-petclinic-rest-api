package com.petclinic.vet.repository;

import com.petclinic.vet.config.JpaAuditingConfig;
import com.petclinic.vet.entity.Specialty;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
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
    void findAll_returnsSeededSpecialties() {
        List<Specialty> specialties = specialtyRepository.findAll();
        assertThat(specialties).hasSizeGreaterThanOrEqualTo(3);
    }

    @Test
    void findById_existingId_returnsSpecialty() {
        Optional<Specialty> specialty = specialtyRepository.findById(1);
        assertThat(specialty).isPresent();
        assertThat(specialty.get().getName()).isEqualTo("radiology");
    }

    @Test
    void findById_nonExistingId_returnsEmpty() {
        Optional<Specialty> specialty = specialtyRepository.findById(999);
        assertThat(specialty).isEmpty();
    }

    @Test
    void findByNameIgnoreCase_existingName_returnsSpecialty() {
        Optional<Specialty> specialty = specialtyRepository.findByNameIgnoreCase("RADIOLOGY");
        assertThat(specialty).isPresent();
        assertThat(specialty.get().getName()).isEqualTo("radiology");
    }

    @Test
    void findByNameIgnoreCase_nonExistingName_returnsEmpty() {
        Optional<Specialty> specialty = specialtyRepository.findByNameIgnoreCase("nonexistent");
        assertThat(specialty).isEmpty();
    }

    @Test
    void save_newSpecialty_persistsSuccessfully() {
        Specialty specialty = new Specialty();
        specialty.setName("oncology");
        Specialty saved = specialtyRepository.save(specialty);

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getName()).isEqualTo("oncology");
    }

    @Test
    void delete_existingSpecialty_removesIt() {
        Specialty specialty = specialtyRepository.findById(1).orElseThrow();
        specialtyRepository.delete(specialty);
        assertThat(specialtyRepository.findById(1)).isEmpty();
    }
}
