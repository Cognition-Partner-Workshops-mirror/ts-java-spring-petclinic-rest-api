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

/**
 * Repository tests for {@link VetRepository} using an embedded H2 database.
 */
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
        radiology = specialtyRepository.save(new Specialty("radiology"));
        surgery = specialtyRepository.save(new Specialty("surgery"));
    }

    @Test
    void shouldSaveAndRetrieveVet() {
        Vet vet = new Vet("James", "Carter");
        Vet saved = vetRepository.save(vet);

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getFirstName()).isEqualTo("James");
        assertThat(saved.getLastName()).isEqualTo("Carter");
    }

    @Test
    void shouldSaveVetWithSpecialties() {
        Vet vet = new Vet("Helen", "Leary");
        vet.setSpecialties(Set.of(radiology));
        Vet saved = vetRepository.save(vet);

        Vet found = vetRepository.findById(saved.getId()).orElseThrow();
        assertThat(found.getSpecialties()).hasSize(1);
        assertThat(found.getSpecialties().iterator().next().getName()).isEqualTo("radiology");
    }

    @Test
    void shouldFindByLastNameContainingIgnoreCase() {
        vetRepository.save(new Vet("James", "Carter"));
        vetRepository.save(new Vet("Helen", "Carpenter"));
        vetRepository.save(new Vet("Linda", "Douglas"));

        List<Vet> results = vetRepository.findByLastNameContainingIgnoreCase("car");

        assertThat(results).hasSize(2);
        assertThat(results).extracting(Vet::getLastName)
                .containsExactlyInAnyOrder("Carter", "Carpenter");
    }

    @Test
    void shouldFindByFirstNameContainingIgnoreCase() {
        vetRepository.save(new Vet("James", "Carter"));
        vetRepository.save(new Vet("Janet", "Wilson"));

        List<Vet> results = vetRepository.findByFirstNameContainingIgnoreCase("ja");

        assertThat(results).hasSize(2);
    }

    @Test
    void shouldFindBySpecialtyId() {
        Vet vet1 = new Vet("Helen", "Leary");
        vet1.setSpecialties(Set.of(radiology));
        vetRepository.save(vet1);

        Vet vet2 = new Vet("Linda", "Douglas");
        vet2.setSpecialties(Set.of(surgery));
        vetRepository.save(vet2);

        Vet vet3 = new Vet("Rafael", "Ortega");
        vet3.setSpecialties(Set.of(radiology, surgery));
        vetRepository.save(vet3);

        List<Vet> radiologyVets = vetRepository.findBySpecialtyId(radiology.getId());

        assertThat(radiologyVets).hasSize(2);
        assertThat(radiologyVets).extracting(Vet::getFirstName)
                .containsExactlyInAnyOrder("Helen", "Rafael");
    }

    @Test
    void shouldFindByLastNameAndSpecialtyId() {
        Vet vet1 = new Vet("Helen", "Leary");
        vet1.setSpecialties(Set.of(radiology));
        vetRepository.save(vet1);

        Vet vet2 = new Vet("Linda", "Douglas");
        vet2.setSpecialties(Set.of(radiology));
        vetRepository.save(vet2);

        List<Vet> results = vetRepository.findByLastNameAndSpecialtyId("Lea", radiology.getId());

        assertThat(results).hasSize(1);
        assertThat(results.get(0).getLastName()).isEqualTo("Leary");
    }

    @Test
    void shouldReturnEmptyWhenNoLastNameMatch() {
        vetRepository.save(new Vet("James", "Carter"));

        List<Vet> results = vetRepository.findByLastNameContainingIgnoreCase("xyz");

        assertThat(results).isEmpty();
    }

    @Test
    void shouldReturnEmptyWhenNoSpecialtyMatch() {
        Vet vet = new Vet("James", "Carter");
        vet.setSpecialties(Set.of(radiology));
        vetRepository.save(vet);

        List<Vet> results = vetRepository.findBySpecialtyId(surgery.getId());

        assertThat(results).isEmpty();
    }

    @Test
    void shouldDeleteVet() {
        Vet saved = vetRepository.save(new Vet("James", "Carter"));

        vetRepository.deleteById(saved.getId());

        assertThat(vetRepository.findById(saved.getId())).isEmpty();
    }

    @Test
    void shouldFindAllVets() {
        vetRepository.save(new Vet("James", "Carter"));
        vetRepository.save(new Vet("Helen", "Leary"));

        List<Vet> all = vetRepository.findAll();

        assertThat(all).hasSize(2);
    }
}
