package com.petclinic.vet.repository;

import com.petclinic.vet.entity.Specialty;
import com.petclinic.vet.entity.Vet;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
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

        Vet linda = new Vet();
        linda.setFirstName("Linda");
        linda.setLastName("Douglas");
        linda.setSpecialties(Set.of(surgery));
        vetRepository.save(linda);
    }

    @Test
    void findBySpecialtyName_returnsMatchingVets() {
        List<Vet> result = vetRepository.findBySpecialtyName("radiology");
        assertThat(result).hasSize(2);
    }

    @Test
    void findBySpecialtyName_noMatch() {
        List<Vet> result = vetRepository.findBySpecialtyName("dentistry");
        assertThat(result).isEmpty();
    }

    @Test
    void findByLastNameContainingIgnoreCase_matchesPartial() {
        List<Vet> result = vetRepository.findByLastNameContainingIgnoreCase("cart");
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getLastName()).isEqualTo("Carter");
    }

    @Test
    void findByLastNameContainingIgnoreCase_caseInsensitive() {
        List<Vet> result = vetRepository.findByLastNameContainingIgnoreCase("CART");
        assertThat(result).hasSize(1);
    }

    @Test
    void findByLastNameContainingIgnoreCase_noMatch() {
        List<Vet> result = vetRepository.findByLastNameContainingIgnoreCase("Smith");
        assertThat(result).isEmpty();
    }

    @Test
    void findBySpecialtyNameAndLastNameContainingIgnoreCase_matchesBoth() {
        List<Vet> result = vetRepository.findBySpecialtyNameAndLastNameContainingIgnoreCase("radiology", "Cart");
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getLastName()).isEqualTo("Carter");
    }

    @Test
    void findBySpecialtyNameAndLastNameContainingIgnoreCase_noMatch() {
        List<Vet> result = vetRepository.findBySpecialtyNameAndLastNameContainingIgnoreCase("dentistry", "Cart");
        assertThat(result).isEmpty();
    }

    @Test
    void save_setsAuditFields() {
        Vet vet = new Vet();
        vet.setFirstName("Test");
        vet.setLastName("Vet");
        Vet saved = vetRepository.save(vet);

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getCreatedAt()).isNotNull();
        assertThat(saved.getUpdatedAt()).isNotNull();
    }

    @Test
    void findAll_returnsAllVets() {
        List<Vet> result = vetRepository.findAll();
        assertThat(result).hasSize(3);
    }
}
