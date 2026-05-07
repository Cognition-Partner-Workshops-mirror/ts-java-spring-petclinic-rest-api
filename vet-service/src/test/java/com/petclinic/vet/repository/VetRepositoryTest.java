package com.petclinic.vet.repository;

import com.petclinic.vet.entity.Specialty;
import com.petclinic.vet.entity.Vet;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
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

        Vet vet1 = new Vet();
        vet1.setFirstName("James");
        vet1.setLastName("Carter");
        vet1.setSpecialties(Set.of(radiology));
        vetRepository.save(vet1);

        Vet vet2 = new Vet();
        vet2.setFirstName("Helen");
        vet2.setLastName("Leary");
        vet2.setSpecialties(Set.of(radiology, surgery));
        vetRepository.save(vet2);

        Vet vet3 = new Vet();
        vet3.setFirstName("Linda");
        vet3.setLastName("Douglas");
        vet3.setSpecialties(Set.of(surgery));
        vetRepository.save(vet3);
    }

    @Test
    void findBySpecialtyId_shouldReturnVetsWithSpecialty() {
        List<Vet> results = vetRepository.findBySpecialtyId(radiology.getId());
        assertThat(results).hasSize(2);
    }

    @Test
    void findBySpecialtyName_shouldReturnVetsWithNamedSpecialty() {
        List<Vet> results = vetRepository.findBySpecialtyName("surgery");
        assertThat(results).hasSize(2);
    }

    @Test
    void findBySpecialtyName_shouldBeCaseInsensitive() {
        List<Vet> results = vetRepository.findBySpecialtyName("RADIOLOGY");
        assertThat(results).hasSize(2);
    }

    @Test
    void findByLastNameContainingIgnoreCase_shouldReturnMatchingVets() {
        List<Vet> results = vetRepository.findByLastNameContainingIgnoreCase("cart");
        assertThat(results).hasSize(1);
        assertThat(results.get(0).getLastName()).isEqualTo("Carter");
    }

    @Test
    void findByNameContainingIgnoreCase_shouldSearchFirstAndLastName() {
        List<Vet> results = vetRepository.findByNameContainingIgnoreCase("helen");
        assertThat(results).hasSize(1);
        assertThat(results.get(0).getFirstName()).isEqualTo("Helen");
    }

    @Test
    void findByNameContainingIgnoreCase_shouldMatchLastName() {
        List<Vet> results = vetRepository.findByNameContainingIgnoreCase("douglas");
        assertThat(results).hasSize(1);
        assertThat(results.get(0).getLastName()).isEqualTo("Douglas");
    }

    @Test
    void findByNameContainingIgnoreCase_shouldReturnEmptyForNoMatch() {
        List<Vet> results = vetRepository.findByNameContainingIgnoreCase("unknown");
        assertThat(results).isEmpty();
    }

    @Test
    void findAll_shouldReturnAllVets() {
        List<Vet> results = vetRepository.findAll();
        assertThat(results).hasSize(3);
    }

    @Test
    void save_shouldPersistVet() {
        Vet vet = new Vet();
        vet.setFirstName("Sharon");
        vet.setLastName("Jenkins");
        vet.setSpecialties(Set.of(surgery));
        Vet saved = vetRepository.save(vet);

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getFirstName()).isEqualTo("Sharon");
    }

    @Test
    void delete_shouldRemoveVet() {
        List<Vet> all = vetRepository.findAll();
        vetRepository.delete(all.get(0));
        assertThat(vetRepository.findAll()).hasSize(2);
    }
}
