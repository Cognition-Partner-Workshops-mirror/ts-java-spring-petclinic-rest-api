package com.petclinic.vet.repository;

import com.petclinic.vet.entity.Specialty;
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

    @Autowired
    private SpecialtyRepository specialtyRepository;

    @Test
    void findAll_returnsSeededVets() {
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
    void findBySpecialtyName_returnsVetsWithSpecialty() {
        List<Vet> vets = vetRepository.findBySpecialtyName("radiology");
        assertThat(vets).hasSizeGreaterThanOrEqualTo(2);
    }

    @Test
    void findBySpecialtyName_noMatch() {
        List<Vet> vets = vetRepository.findBySpecialtyName("oncology");
        assertThat(vets).isEmpty();
    }

    @Test
    void findByLastNameContainingIgnoreCase_matchesPartial() {
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
        List<Vet> vets = vetRepository.findByLastNameContainingIgnoreCase("NonExistent");
        assertThat(vets).isEmpty();
    }

    @Test
    void findByFirstNameAndLastNameContainingIgnoreCase_matchesBoth() {
        List<Vet> vets = vetRepository.findByFirstNameAndLastNameContainingIgnoreCase("James", "Carter");
        assertThat(vets).hasSize(1);
    }

    @Test
    void save_newVet_persistsSuccessfully() {
        Vet vet = new Vet();
        vet.setFirstName("John");
        vet.setLastName("Doe");
        Vet saved = vetRepository.save(vet);
        assertThat(saved.getId()).isNotNull();
    }

    @Test
    void save_vetWithSpecialties_persistsRelationship() {
        Specialty specialty = specialtyRepository.findById(1).orElseThrow();
        Vet vet = new Vet();
        vet.setFirstName("Jane");
        vet.setLastName("Smith");
        vet.addSpecialty(specialty);
        Vet saved = vetRepository.save(vet);

        Vet found = vetRepository.findById(saved.getId()).orElseThrow();
        assertThat(found.getSpecialties()).hasSize(1);
    }

    @Test
    void delete_existingVet_removesFromDb() {
        Vet vet = new Vet();
        vet.setFirstName("Temp");
        vet.setLastName("Vet");
        vet = vetRepository.save(vet);
        Integer id = vet.getId();

        vetRepository.delete(vet);
        assertThat(vetRepository.findById(id)).isEmpty();
    }

    @Test
    void vetSpecialties_areSortedByName() {
        Optional<Vet> vet = vetRepository.findById(3);
        assertThat(vet).isPresent();
        List<Specialty> sorted = vet.get().getSpecialtiesSorted();
        assertThat(sorted).hasSizeGreaterThanOrEqualTo(2);
        for (int i = 1; i < sorted.size(); i++) {
            assertThat(sorted.get(i).getName().compareToIgnoreCase(sorted.get(i - 1).getName()))
                .isGreaterThanOrEqualTo(0);
        }
    }
}
