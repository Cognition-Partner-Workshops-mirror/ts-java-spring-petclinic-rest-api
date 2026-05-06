package com.petclinic.vet.repository;

import com.petclinic.vet.entity.Specialty;
import com.petclinic.vet.entity.Vet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class VetRepositoryTest {

    @Autowired
    private VetRepository vetRepository;

    @Autowired
    private SpecialtyRepository specialtyRepository;

    @Test
    void findAll_returnsSeedData() {
        List<Vet> vets = vetRepository.findAll();
        assertThat(vets).hasSizeGreaterThanOrEqualTo(6);
    }

    @Test
    void findById_found() {
        Optional<Vet> result = vetRepository.findById(1);
        assertThat(result).isPresent();
        assertThat(result.get().getFirstName()).isEqualTo("James");
        assertThat(result.get().getLastName()).isEqualTo("Carter");
    }

    @Test
    void findById_notFound() {
        Optional<Vet> result = vetRepository.findById(999);
        assertThat(result).isEmpty();
    }

    @Test
    void findBySpecialtyId_found() {
        List<Vet> results = vetRepository.findBySpecialtyId(1);
        assertThat(results).isNotEmpty();
    }

    @Test
    void findBySpecialtyId_notFound() {
        List<Vet> results = vetRepository.findBySpecialtyId(999);
        assertThat(results).isEmpty();
    }

    @Test
    void findByLastNameContainingIgnoreCase_found() {
        List<Vet> results = vetRepository.findByLastNameContainingIgnoreCase("carter");
        assertThat(results).hasSize(1);
        assertThat(results.get(0).getFirstName()).isEqualTo("James");
    }

    @Test
    void findByLastNameContainingIgnoreCase_notFound() {
        List<Vet> results = vetRepository.findByLastNameContainingIgnoreCase("nonexistent");
        assertThat(results).isEmpty();
    }

    @Test
    void findBySpecialtyName_found() {
        List<Vet> results = vetRepository.findBySpecialtyName("radiology");
        assertThat(results).isNotEmpty();
    }

    @Test
    void findBySpecialtyName_notFound() {
        List<Vet> results = vetRepository.findBySpecialtyName("nonexistent");
        assertThat(results).isEmpty();
    }

    @Test
    void save_newVetWithSpecialties() {
        Specialty specialty = specialtyRepository.findById(1).orElseThrow();

        Vet vet = new Vet();
        vet.setFirstName("New");
        vet.setLastName("Vet");
        vet.setSpecialties(Set.of(specialty));

        Vet saved = vetRepository.save(vet);

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getSpecialties()).hasSize(1);
    }

    @Test
    void delete_existingVet() {
        Vet vet = new Vet();
        vet.setFirstName("ToDelete");
        vet.setLastName("Vet");
        Vet saved = vetRepository.save(vet);

        vetRepository.delete(saved);
        vetRepository.flush();

        Optional<Vet> result = vetRepository.findById(saved.getId());
        assertThat(result).isEmpty();
    }
}
