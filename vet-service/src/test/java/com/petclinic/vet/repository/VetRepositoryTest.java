package com.petclinic.vet.repository;

import com.petclinic.vet.entity.Vet;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class VetRepositoryTest {

    @Autowired
    private VetRepository vetRepository;

    @Test
    void findAll_returnsSeededVets() {
        List<Vet> vets = vetRepository.findAll();
        assertThat(vets).hasSizeGreaterThanOrEqualTo(6);
    }

    @Test
    void findById_returnsVet() {
        Optional<Vet> vet = vetRepository.findById(1);
        assertThat(vet).isPresent();
        assertThat(vet.get().getFirstName()).isEqualTo("James");
        assertThat(vet.get().getLastName()).isEqualTo("Carter");
    }

    @Test
    void findById_notFound() {
        Optional<Vet> vet = vetRepository.findById(999);
        assertThat(vet).isEmpty();
    }

    @Test
    void findBySpecialtyName_findsVetsWithRadiology() {
        List<Vet> vets = vetRepository.findBySpecialtyName("radiology");
        assertThat(vets).hasSizeGreaterThanOrEqualTo(2);
    }

    @Test
    void findBySpecialtyName_noMatch() {
        List<Vet> vets = vetRepository.findBySpecialtyName("unknown");
        assertThat(vets).isEmpty();
    }

    @Test
    void findByLastNameContainingIgnoreCase_findsMatch() {
        List<Vet> vets = vetRepository.findByLastNameContainingIgnoreCase("Carter");
        assertThat(vets).hasSize(1);
        assertThat(vets.get(0).getFirstName()).isEqualTo("James");
    }

    @Test
    void findByLastNameContainingIgnoreCase_caseInsensitive() {
        List<Vet> vets = vetRepository.findByLastNameContainingIgnoreCase("carter");
        assertThat(vets).hasSize(1);
    }

    @Test
    void findByLastNameContainingIgnoreCase_noMatch() {
        List<Vet> vets = vetRepository.findByLastNameContainingIgnoreCase("Unknown");
        assertThat(vets).isEmpty();
    }

    @Test
    void findByFirstNameAndLastNameContainingIgnoreCase_findsMatch() {
        List<Vet> vets = vetRepository.findByFirstNameAndLastNameContainingIgnoreCase("James", "Carter");
        assertThat(vets).hasSize(1);
    }

    @Test
    void findByFirstNameAndLastNameContainingIgnoreCase_noMatch() {
        List<Vet> vets = vetRepository.findByFirstNameAndLastNameContainingIgnoreCase("Unknown", "Unknown");
        assertThat(vets).isEmpty();
    }

    @Test
    void save_createsNewVet() {
        Vet vet = new Vet();
        vet.setFirstName("Test");
        vet.setLastName("Vet");
        Vet saved = vetRepository.save(vet);
        assertThat(saved.getId()).isNotNull();
    }

    @Test
    void delete_removesVet() {
        Vet vet = new Vet();
        vet.setFirstName("ToDelete");
        vet.setLastName("Vet");
        Vet saved = vetRepository.save(vet);
        vetRepository.delete(saved);
        assertThat(vetRepository.findById(saved.getId())).isEmpty();
    }

    @Test
    void findById_returnsVetWithSpecialties() {
        Optional<Vet> vet = vetRepository.findById(3);
        assertThat(vet).isPresent();
        assertThat(vet.get().getSpecialties()).hasSizeGreaterThanOrEqualTo(2);
    }
}
