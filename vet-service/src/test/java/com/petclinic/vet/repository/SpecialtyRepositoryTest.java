package com.petclinic.vet.repository;

import com.petclinic.vet.config.JpaAuditingConfig;
import com.petclinic.vet.entity.Specialty;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import(JpaAuditingConfig.class)
@TestPropertySource(properties = {
    "spring.flyway.enabled=false",
    "spring.jpa.hibernate.ddl-auto=create-drop"
})
class SpecialtyRepositoryTest {

    @Autowired
    private SpecialtyRepository specialtyRepository;

    @BeforeEach
    void setUp() {
        specialtyRepository.deleteAll();
    }

    @Test
    void findByNameContainingIgnoreCase_returnsMatching() {
        Specialty specialty = new Specialty();
        specialty.setName("radiology");
        specialtyRepository.save(specialty);

        List<Specialty> result = specialtyRepository.findByNameContainingIgnoreCase("rad");

        assertThat(result).hasSize(1);
        assertThat(result.getFirst().getName()).isEqualTo("radiology");
    }

    @Test
    void findByNameContainingIgnoreCase_caseInsensitive() {
        Specialty specialty = new Specialty();
        specialty.setName("radiology");
        specialtyRepository.save(specialty);

        List<Specialty> result = specialtyRepository.findByNameContainingIgnoreCase("RADIO");

        assertThat(result).hasSize(1);
    }

    @Test
    void findByNameContainingIgnoreCase_noMatch_returnsEmpty() {
        Specialty specialty = new Specialty();
        specialty.setName("radiology");
        specialtyRepository.save(specialty);

        List<Specialty> result = specialtyRepository.findByNameContainingIgnoreCase("xyz");

        assertThat(result).isEmpty();
    }

    @Test
    void save_persistsSpecialty() {
        Specialty specialty = new Specialty();
        specialty.setName("oncology");
        Specialty saved = specialtyRepository.save(specialty);

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getName()).isEqualTo("oncology");
    }
}
