package com.petclinic.vet.repository;

import com.petclinic.vet.config.JpaAuditingConfig;
import com.petclinic.vet.entity.Specialty;
import com.petclinic.vet.entity.Vet;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import(JpaAuditingConfig.class)
@ActiveProfiles("test")
class VetRepositoryTest {

    @Autowired
    private VetRepository vetRepository;

    @Autowired
    private SpecialtyRepository specialtyRepository;

    private Specialty radiology;
    private Specialty surgery;

    @BeforeEach
    void setUp() {
        vetRepository.deleteAll();
        specialtyRepository.deleteAll();

        radiology = new Specialty();
        radiology.setName("radiology");
        radiology = specialtyRepository.save(radiology);

        surgery = new Specialty();
        surgery.setName("surgery");
        surgery = specialtyRepository.save(surgery);

        Vet james = new Vet();
        james.setFirstName("James");
        james.setLastName("Carter");
        james.setSpecialties(Set.of(radiology));
        vetRepository.save(james);

        Vet helen = new Vet();
        helen.setFirstName("Helen");
        helen.setLastName("Leary");
        helen.setSpecialties(Set.of(radiology, surgery));
        vetRepository.save(helen);
    }

    @Test
    void findBySpecialtyId_radiology() {
        List<Vet> results = vetRepository.findBySpecialtyId(radiology.getId());
        assertThat(results).hasSize(2);
    }

    @Test
    void findBySpecialtyId_surgery() {
        List<Vet> results = vetRepository.findBySpecialtyId(surgery.getId());
        assertThat(results).hasSize(1);
        assertThat(results.get(0).getFirstName()).isEqualTo("Helen");
    }

    @Test
    void findBySpecialtyId_noMatch() {
        List<Vet> results = vetRepository.findBySpecialtyId(999);
        assertThat(results).isEmpty();
    }

    @Test
    void findByLastNameContainingIgnoreCase_found() {
        List<Vet> results = vetRepository.findByLastNameContainingIgnoreCase("cart");
        assertThat(results).hasSize(1);
        assertThat(results.get(0).getLastName()).isEqualTo("Carter");
    }

    @Test
    void findByLastNameContainingIgnoreCase_noMatch() {
        List<Vet> results = vetRepository.findByLastNameContainingIgnoreCase("xyz");
        assertThat(results).isEmpty();
    }

    @Test
    void findBySpecialtyName_found() {
        List<Vet> results = vetRepository.findBySpecialtyName("radiology");
        assertThat(results).hasSize(2);
    }

    @Test
    void findBySpecialtyName_caseInsensitive() {
        List<Vet> results = vetRepository.findBySpecialtyName("SURGERY");
        assertThat(results).hasSize(1);
    }

    @Test
    void findAll_returnsAll() {
        List<Vet> results = vetRepository.findAll();
        assertThat(results).hasSize(2);
    }
}
