package com.petclinic.vet.repository;

import com.petclinic.vet.entity.Specialty;
import com.petclinic.vet.entity.Vet;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class VetRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private VetRepository vetRepository;

    @Autowired
    private SpecialtyRepository specialtyRepository;

    private Specialty radiology;
    private Specialty surgery;

    @BeforeEach
    void setUp() {
        radiology = new Specialty();
        radiology.setName("radiology");
        radiology = entityManager.persist(radiology);

        surgery = new Specialty();
        surgery.setName("surgery");
        surgery = entityManager.persist(surgery);

        Vet vet1 = new Vet();
        vet1.setFirstName("James");
        vet1.setLastName("Carter");
        entityManager.persist(vet1);

        Vet vet2 = new Vet();
        vet2.setFirstName("Helen");
        vet2.setLastName("Leary");
        vet2.setSpecialties(Set.of(radiology));
        entityManager.persist(vet2);

        Vet vet3 = new Vet();
        vet3.setFirstName("Linda");
        vet3.setLastName("Douglas");
        vet3.setSpecialties(Set.of(radiology, surgery));
        entityManager.persist(vet3);

        entityManager.flush();
        entityManager.clear();
    }

    @Test
    void findByLastNameContainingIgnoreCase_shouldReturnMatchingVets() {
        List<Vet> vets = vetRepository.findByLastNameContainingIgnoreCase("cart");
        assertThat(vets).hasSize(1);
        assertThat(vets.get(0).getLastName()).isEqualTo("Carter");
    }

    @Test
    void findByLastNameContainingIgnoreCase_shouldReturnEmptyForNoMatch() {
        List<Vet> vets = vetRepository.findByLastNameContainingIgnoreCase("xyz");
        assertThat(vets).isEmpty();
    }

    @Test
    void findBySpecialtyName_shouldReturnVetsWithExactSpecialty() {
        List<Vet> vets = vetRepository.findBySpecialtyName("radiology");
        assertThat(vets).hasSize(2);
    }

    @Test
    void findBySpecialtyNameContaining_shouldReturnVetsWithMatchingSpecialty() {
        List<Vet> vets = vetRepository.findBySpecialtyNameContaining("surg");
        assertThat(vets).hasSize(1);
        assertThat(vets.get(0).getFirstName()).isEqualTo("Linda");
    }

    @Test
    void findByNameContaining_shouldReturnVetsByFirstOrLastName() {
        List<Vet> vets = vetRepository.findByNameContaining("helen");
        assertThat(vets).hasSize(1);
        assertThat(vets.get(0).getFirstName()).isEqualTo("Helen");
    }

    @Test
    void findByNameContaining_shouldReturnVetsByLastName() {
        List<Vet> vets = vetRepository.findByNameContaining("douglas");
        assertThat(vets).hasSize(1);
        assertThat(vets.get(0).getLastName()).isEqualTo("Douglas");
    }

    @Test
    void findAll_shouldReturnAllVets() {
        List<Vet> vets = vetRepository.findAll();
        assertThat(vets).hasSize(3);
    }

    @Test
    void save_shouldPersistNewVet() {
        Vet vet = new Vet();
        vet.setFirstName("New");
        vet.setLastName("Vet");
        vet.setSpecialties(Set.of(surgery));

        Vet saved = vetRepository.save(vet);

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getFirstName()).isEqualTo("New");
        assertThat(saved.getSpecialties()).hasSize(1);
    }

    @Test
    void delete_shouldRemoveVet() {
        List<Vet> before = vetRepository.findAll();
        vetRepository.delete(before.get(0));
        entityManager.flush();
        List<Vet> after = vetRepository.findAll();
        assertThat(after).hasSize(before.size() - 1);
    }
}
