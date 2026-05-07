package com.petclinic.vet.repository;

import com.petclinic.vet.entity.Specialty;
import com.petclinic.vet.entity.Vet;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
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

    @Test
    void findAll_returnsSeededData() {
        List<Vet> vets = vetRepository.findAll();
        assertThat(vets).hasSizeGreaterThanOrEqualTo(6);
    }

    @Test
    void findById_existingId_returnsVet() {
        Optional<Vet> vet = vetRepository.findById(1);
        assertThat(vet).isPresent();
        assertThat(vet.get().getFirstName()).isEqualTo("James");
        assertThat(vet.get().getLastName()).isEqualTo("Carter");
    }

    @Test
    void findById_nonExistingId_returnsEmpty() {
        Optional<Vet> vet = vetRepository.findById(999);
        assertThat(vet).isEmpty();
    }

    @Test
    void save_createsNewVet() {
        Vet vet = new Vet();
        vet.setFirstName("John");
        vet.setLastName("Doe");
        Vet saved = vetRepository.save(vet);

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getFirstName()).isEqualTo("John");
    }

    @Test
    void save_createsVetWithSpecialties() {
        Specialty specialty = specialtyRepository.findById(1).orElseThrow();
        Vet vet = new Vet();
        vet.setFirstName("Test");
        vet.setLastName("Vet");
        vet.setSpecialties(new HashSet<>(Set.of(specialty)));
        Vet saved = vetRepository.save(vet);

        assertThat(saved.getSpecialties()).hasSize(1);
    }

    @Test
    void findBySpecialtyName_matchingSpecialty_returnsVets() {
        List<Vet> vets = vetRepository.findBySpecialtyName("radiology");
        assertThat(vets).hasSizeGreaterThanOrEqualTo(2);
    }

    @Test
    void findBySpecialtyName_noMatch_returnsEmpty() {
        List<Vet> vets = vetRepository.findBySpecialtyName("nonexistent");
        assertThat(vets).isEmpty();
    }

    @Test
    void findByLastNameContainingIgnoreCase_matchingName_returnsVets() {
        List<Vet> vets = vetRepository.findByLastNameContainingIgnoreCase("Carter");
        assertThat(vets).hasSize(1);
        assertThat(vets.get(0).getLastName()).isEqualTo("Carter");
    }

    @Test
    void findByLastNameContainingIgnoreCase_caseInsensitive_returnsVets() {
        List<Vet> vets = vetRepository.findByLastNameContainingIgnoreCase("carter");
        assertThat(vets).hasSize(1);
    }

    @Test
    void findByLastNameContainingIgnoreCase_partialMatch_returnsVets() {
        List<Vet> vets = vetRepository.findByLastNameContainingIgnoreCase("art");
        assertThat(vets).hasSize(1);
    }

    @Test
    void findByLastNameContainingIgnoreCase_noMatch_returnsEmpty() {
        List<Vet> vets = vetRepository.findByLastNameContainingIgnoreCase("nonexistent");
        assertThat(vets).isEmpty();
    }

    @Test
    void delete_removesVet() {
        Vet vet = new Vet();
        vet.setFirstName("Temp");
        vet.setLastName("Vet");
        Vet saved = vetRepository.save(vet);
        Integer id = saved.getId();

        vetRepository.deleteById(id);

        assertThat(vetRepository.findById(id)).isEmpty();
    }
}
