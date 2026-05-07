package com.petclinic.vet.repository;

import com.petclinic.vet.config.JpaAuditingConfig;
import com.petclinic.vet.entity.Specialty;
import com.petclinic.vet.entity.Vet;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import(JpaAuditingConfig.class)
@TestPropertySource(properties = {
    "spring.flyway.enabled=false",
    "spring.jpa.hibernate.ddl-auto=create-drop"
})
class VetRepositoryTest {

    @Autowired
    private VetRepository vetRepository;

    @Autowired
    private SpecialtyRepository specialtyRepository;

    @BeforeEach
    void setUp() {
        vetRepository.deleteAll();
        specialtyRepository.deleteAll();
    }

    @Test
    void findByLastNameContainingIgnoreCase_returnsMatching() {
        Vet vet = new Vet();
        vet.setFirstName("James");
        vet.setLastName("Carter");
        vetRepository.save(vet);

        List<Vet> result = vetRepository.findByLastNameContainingIgnoreCase("cart");

        assertThat(result).hasSize(1);
        assertThat(result.getFirst().getLastName()).isEqualTo("Carter");
    }

    @Test
    void findByLastNameContainingIgnoreCase_noMatch_returnsEmpty() {
        Vet vet = new Vet();
        vet.setFirstName("James");
        vet.setLastName("Carter");
        vetRepository.save(vet);

        List<Vet> result = vetRepository.findByLastNameContainingIgnoreCase("xyz");

        assertThat(result).isEmpty();
    }

    @Test
    void findBySpecialtyName_returnsMatching() {
        Specialty specialty = new Specialty();
        specialty.setName("radiology");
        specialty = specialtyRepository.save(specialty);

        Vet vet = new Vet();
        vet.setFirstName("Helen");
        vet.setLastName("Leary");
        vet.setSpecialties(Set.of(specialty));
        vetRepository.save(vet);

        List<Vet> result = vetRepository.findBySpecialtyName("radiology");

        assertThat(result).hasSize(1);
        assertThat(result.getFirst().getFirstName()).isEqualTo("Helen");
    }

    @Test
    void findBySpecialtyNameContainingIgnoreCase_returnsMatching() {
        Specialty specialty = new Specialty();
        specialty.setName("radiology");
        specialty = specialtyRepository.save(specialty);

        Vet vet = new Vet();
        vet.setFirstName("Helen");
        vet.setLastName("Leary");
        vet.setSpecialties(Set.of(specialty));
        vetRepository.save(vet);

        List<Vet> result = vetRepository.findBySpecialtyNameContainingIgnoreCase("RAD");

        assertThat(result).hasSize(1);
    }

    @Test
    void findBySpecialtyNameContainingIgnoreCase_noMatch_returnsEmpty() {
        Specialty specialty = new Specialty();
        specialty.setName("radiology");
        specialty = specialtyRepository.save(specialty);

        Vet vet = new Vet();
        vet.setFirstName("Helen");
        vet.setLastName("Leary");
        vet.setSpecialties(Set.of(specialty));
        vetRepository.save(vet);

        List<Vet> result = vetRepository.findBySpecialtyNameContainingIgnoreCase("surgery");

        assertThat(result).isEmpty();
    }

    @Test
    void save_persistsVetWithSpecialties() {
        Specialty s1 = new Specialty();
        s1.setName("surgery");
        s1 = specialtyRepository.save(s1);

        Specialty s2 = new Specialty();
        s2.setName("dentistry");
        s2 = specialtyRepository.save(s2);

        Vet vet = new Vet();
        vet.setFirstName("Linda");
        vet.setLastName("Douglas");
        vet.setSpecialties(Set.of(s1, s2));
        Vet saved = vetRepository.save(vet);

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getSpecialties()).hasSize(2);
    }
}
