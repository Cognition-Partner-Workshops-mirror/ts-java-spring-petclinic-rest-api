package com.petclinic.vet.repository;

import com.petclinic.vet.entity.Specialty;
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
    private SpecialtyRepository specialtyRepository;

    @Test
    void saveAndFind() {
        Specialty specialty = new Specialty();
        specialty.setName("radiology");
        Specialty saved = specialtyRepository.save(specialty);

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getCreatedAt()).isNotNull();
        assertThat(saved.getUpdatedAt()).isNotNull();

        Optional<Specialty> found = specialtyRepository.findById(saved.getId());
        assertThat(found).isPresent();
        assertThat(found.get().getName()).isEqualTo("radiology");
    }

    @Test
    void findByNameContainingIgnoreCase() {
        Specialty s1 = new Specialty();
        s1.setName("radiology");
        specialtyRepository.save(s1);

        Specialty s2 = new Specialty();
        s2.setName("surgery");
        specialtyRepository.save(s2);

        List<Specialty> results = specialtyRepository.findByNameContainingIgnoreCase("RAD");
        assertThat(results).hasSize(1);
        assertThat(results.get(0).getName()).isEqualTo("radiology");
    }

    @Test
    void findByNameContainingIgnoreCase_noMatch() {
        Specialty s = new Specialty();
        s.setName("radiology");
        specialtyRepository.save(s);

        List<Specialty> results = specialtyRepository.findByNameContainingIgnoreCase("xyz");
        assertThat(results).isEmpty();
    }

    @Test
    void deleteSpecialty() {
        Specialty specialty = new Specialty();
        specialty.setName("dentistry");
        Specialty saved = specialtyRepository.save(specialty);

        specialtyRepository.deleteById(saved.getId());

        assertThat(specialtyRepository.findById(saved.getId())).isEmpty();
    }

    @Test
    void findAll_empty() {
        List<Specialty> all = specialtyRepository.findAll();
        assertThat(all).isEmpty();
    }
}
