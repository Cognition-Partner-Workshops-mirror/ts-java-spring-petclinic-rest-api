package com.petclinic.vet.repository;

import com.petclinic.vet.entity.Specialty;
import com.petclinic.vet.entity.Vet;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import com.petclinic.vet.config.JpaAuditingConfig;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
@Import(JpaAuditingConfig.class)
class VetRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private VetRepository vetRepository;

    private Specialty radiology;
    private Specialty surgery;
    private Vet james;
    private Vet helen;

    @BeforeEach
    void setUp() {
        radiology = new Specialty();
        radiology.setName("radiology");
        entityManager.persist(radiology);

        surgery = new Specialty();
        surgery.setName("surgery");
        entityManager.persist(surgery);

        james = new Vet();
        james.setFirstName("James");
        james.setLastName("Carter");
        james.setSpecialties(new HashSet<>(Set.of(radiology)));
        entityManager.persist(james);

        helen = new Vet();
        helen.setFirstName("Helen");
        helen.setLastName("Leary");
        helen.setSpecialties(new HashSet<>(Set.of(radiology, surgery)));
        entityManager.persist(helen);

        entityManager.flush();
    }

    @Test
    void findBySpecialtyId_returnsVetsWithSpecialty() {
        List<Vet> result = vetRepository.findBySpecialtyId(radiology.getId());

        assertThat(result).hasSize(2);
    }

    @Test
    void findBySpecialtyId_noMatch_returnsEmpty() {
        Specialty dentistry = new Specialty();
        dentistry.setName("dentistry");
        entityManager.persist(dentistry);
        entityManager.flush();

        List<Vet> result = vetRepository.findBySpecialtyId(dentistry.getId());

        assertThat(result).isEmpty();
    }

    @Test
    void findByLastNameContainingIgnoreCase_returnsMatchingVets() {
        List<Vet> result = vetRepository.findByLastNameContainingIgnoreCase("cart");

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getLastName()).isEqualTo("Carter");
    }

    @Test
    void findByLastNameContainingIgnoreCase_noMatch_returnsEmpty() {
        List<Vet> result = vetRepository.findByLastNameContainingIgnoreCase("xyz");

        assertThat(result).isEmpty();
    }

    @Test
    void findBySpecialtyName_returnsMatchingVets() {
        List<Vet> result = vetRepository.findBySpecialtyName("surgery");

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getFirstName()).isEqualTo("Helen");
    }

    @Test
    void findBySpecialtyName_caseInsensitive() {
        List<Vet> result = vetRepository.findBySpecialtyName("RADIOLOGY");

        assertThat(result).hasSize(2);
    }
}
