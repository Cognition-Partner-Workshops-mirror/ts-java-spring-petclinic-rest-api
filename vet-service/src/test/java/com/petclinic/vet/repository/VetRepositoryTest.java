package com.petclinic.vet.repository;

import com.petclinic.vet.entity.Specialty;
import com.petclinic.vet.entity.Vet;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

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
        assertThat(vets).isNotEmpty();
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
    void save_newVet_persistsAndGeneratesId() {
        Vet newVet = new Vet();
        newVet.setFirstName("Test");
        newVet.setLastName("Vet");

        Vet saved = vetRepository.save(newVet);

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getFirstName()).isEqualTo("Test");
    }

    @Test
    void save_vetWithSpecialties_persistsRelationship() {
        Specialty radiology = specialtyRepository.findById(1).orElseThrow();

        Vet newVet = new Vet();
        newVet.setFirstName("New");
        newVet.setLastName("Doctor");
        Set<Specialty> specialties = new HashSet<>();
        specialties.add(radiology);
        newVet.setSpecialties(specialties);

        Vet saved = vetRepository.save(newVet);

        Optional<Vet> fetched = vetRepository.findById(saved.getId());
        assertThat(fetched).isPresent();
        assertThat(fetched.get().getSpecialties()).hasSize(1);
        assertThat(fetched.get().getSpecialties().iterator().next().getName()).isEqualTo("radiology");
    }

    @Test
    void delete_existingVet_removesFromDb() {
        Vet newVet = new Vet();
        newVet.setFirstName("Temp");
        newVet.setLastName("Vet");
        Vet saved = vetRepository.save(newVet);

        vetRepository.delete(saved);

        Optional<Vet> fetched = vetRepository.findById(saved.getId());
        assertThat(fetched).isEmpty();
    }

    @Test
    void findByLastNameContainingIgnoreCase_returnsMatchingVets() {
        List<Vet> results = vetRepository.findByLastNameContainingIgnoreCase("carter");
        assertThat(results).hasSize(1);
        assertThat(results.get(0).getFirstName()).isEqualTo("James");
    }

    @Test
    void findByLastNameContainingIgnoreCase_noMatch_returnsEmptyList() {
        List<Vet> results = vetRepository.findByLastNameContainingIgnoreCase("nonexistent");
        assertThat(results).isEmpty();
    }

    @Test
    void findBySpecialtyName_returnsVetsWithSpecialty() {
        List<Vet> results = vetRepository.findBySpecialtyName("radiology");
        assertThat(results).isNotEmpty();
        assertThat(results).extracting(Vet::getLastName)
            .contains("Leary", "Stevens");
    }

    @Test
    void findBySpecialtyName_noMatch_returnsEmptyList() {
        List<Vet> results = vetRepository.findBySpecialtyName("nonexistent");
        assertThat(results).isEmpty();
    }

    @Test
    void findBySpecialtyId_returnsVetsWithSpecialty() {
        List<Vet> results = vetRepository.findBySpecialtyId(1);
        assertThat(results).isNotEmpty();
    }
}
