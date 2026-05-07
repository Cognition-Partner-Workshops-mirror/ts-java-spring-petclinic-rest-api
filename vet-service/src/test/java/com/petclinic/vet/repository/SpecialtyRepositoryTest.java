package com.petclinic.vet.repository;

import com.petclinic.vet.entity.Specialty;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class SpecialtyRepositoryTest {

    @Autowired
    private SpecialtyRepository specialtyRepository;

    @Test
    void findAll_returnsSeededSpecialties() {
        List<Specialty> specialties = specialtyRepository.findAll();
        assertThat(specialties).hasSizeGreaterThanOrEqualTo(3);
    }

    @Test
    void findById_returnsSpecialty() {
        Optional<Specialty> specialty = specialtyRepository.findById(1);
        assertThat(specialty).isPresent();
        assertThat(specialty.get().getName()).isEqualTo("radiology");
    }

    @Test
    void findByNameIgnoreCase_found() {
        Optional<Specialty> specialty = specialtyRepository.findByNameIgnoreCase("RADIOLOGY");
        assertThat(specialty).isPresent();
        assertThat(specialty.get().getName()).isEqualTo("radiology");
    }

    @Test
    void findByNameIgnoreCase_notFound() {
        Optional<Specialty> specialty = specialtyRepository.findByNameIgnoreCase("nonexistent");
        assertThat(specialty).isEmpty();
    }

    @Test
    void findByNameInIgnoreCase_returnsMatching() {
        List<Specialty> specialties = specialtyRepository.findByNameInIgnoreCase(
            Set.of("radiology", "surgery"));
        assertThat(specialties).hasSize(2);
    }

    @Test
    void save_persistsNewSpecialty() {
        Specialty specialty = new Specialty();
        specialty.setName("cardiology");
        Specialty saved = specialtyRepository.save(specialty);
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getName()).isEqualTo("cardiology");
    }

    @Test
    void delete_removesSpecialty() {
        // Create a new specialty not referenced by any vet, then delete it
        Specialty specialty = new Specialty();
        specialty.setName("toDelete");
        Specialty saved = specialtyRepository.save(specialty);
        Integer savedId = saved.getId();
        specialtyRepository.deleteById(savedId);
        specialtyRepository.flush();
        assertThat(specialtyRepository.findById(savedId)).isEmpty();
    }
}
