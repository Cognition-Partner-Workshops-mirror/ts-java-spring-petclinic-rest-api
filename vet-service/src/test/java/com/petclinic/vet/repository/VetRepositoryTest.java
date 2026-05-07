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

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
@Import(JpaAuditingConfig.class)
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
        james.setSpecialties(new HashSet<>(Set.of(radiology)));
        vetRepository.save(james);

        Vet helen = new Vet();
        helen.setFirstName("Helen");
        helen.setLastName("Leary");
        helen.setSpecialties(new HashSet<>(Set.of(radiology, surgery)));
        vetRepository.save(helen);

        Vet linda = new Vet();
        linda.setFirstName("Linda");
        linda.setLastName("Douglas");
        linda.setSpecialties(new HashSet<>(Set.of(surgery)));
        vetRepository.save(linda);
    }

    @Test
    void findBySpecialtyId_returnsMatchingVets() {
        List<Vet> result = vetRepository.findBySpecialtyId(radiology.getId());
        assertThat(result).hasSize(2);
    }

    @Test
    void findBySpecialtyName_returnsMatchingVets() {
        List<Vet> result = vetRepository.findBySpecialtyName("surgery");
        assertThat(result).hasSize(2);
    }

    @Test
    void findBySpecialtyName_caseInsensitive() {
        List<Vet> result = vetRepository.findBySpecialtyName("SURGERY");
        assertThat(result).hasSize(2);
    }

    @Test
    void findBySpecialtyName_noMatches() {
        List<Vet> result = vetRepository.findBySpecialtyName("cardiology");
        assertThat(result).isEmpty();
    }

    @Test
    void findByLastNameContainingIgnoreCase_returnsMatches() {
        List<Vet> result = vetRepository.findByLastNameContainingIgnoreCase("Cart");
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getFirstName()).isEqualTo("James");
    }

    @Test
    void findByLastNameContainingIgnoreCase_caseInsensitive() {
        List<Vet> result = vetRepository.findByLastNameContainingIgnoreCase("CART");
        assertThat(result).hasSize(1);
    }

    @Test
    void findByLastNameContainingIgnoreCase_noMatches() {
        List<Vet> result = vetRepository.findByLastNameContainingIgnoreCase("Smith");
        assertThat(result).isEmpty();
    }

    @Test
    void findByFirstNameAndLastNameContainingIgnoreCase_returnsMatches() {
        List<Vet> result = vetRepository.findByFirstNameAndLastNameContainingIgnoreCase("Helen", "Leary");
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getFirstName()).isEqualTo("Helen");
    }

    @Test
    void findAll_returnsAll() {
        List<Vet> all = vetRepository.findAll();
        assertThat(all).hasSize(3);
    }

    @Test
    void save_persistsVetWithSpecialties() {
        Vet newVet = new Vet();
        newVet.setFirstName("Rafael");
        newVet.setLastName("Ortega");
        newVet.setSpecialties(new HashSet<>(Set.of(surgery)));
        Vet saved = vetRepository.save(newVet);

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getSpecialties()).hasSize(1);
    }

    @Test
    void delete_removesVet() {
        List<Vet> before = vetRepository.findAll();
        vetRepository.delete(before.get(0));
        List<Vet> after = vetRepository.findAll();
        assertThat(after).hasSize(before.size() - 1);
    }
}
