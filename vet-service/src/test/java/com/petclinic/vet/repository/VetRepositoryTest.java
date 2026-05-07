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
    void save_newVet_assignsId() {
        Vet vet = new Vet();
        vet.setFirstName("Test");
        vet.setLastName("Vet");
        Vet saved = vetRepository.save(vet);
        assertThat(saved.getId()).isNotNull();
    }

    @Test
    void save_vetWithSpecialties() {
        Specialty specialty = specialtyRepository.findById(1).orElseThrow();
        Vet vet = new Vet();
        vet.setFirstName("New");
        vet.setLastName("Doctor");
        vet.addSpecialty(specialty);
        Vet saved = vetRepository.save(vet);
        assertThat(saved.getSpecialties()).hasSize(1);
    }

    @Test
    void findBySpecialtyName_returnsMatchingVets() {
        List<Vet> vets = vetRepository.findBySpecialtyName("radiology");
        assertThat(vets).isNotEmpty();
        assertThat(vets).allSatisfy(vet ->
            assertThat(vet.getSpecialties()).extracting(Specialty::getName).contains("radiology")
        );
    }

    @Test
    void findBySpecialtyName_noMatch_returnsEmpty() {
        List<Vet> vets = vetRepository.findBySpecialtyName("nonexistent");
        assertThat(vets).isEmpty();
    }

    @Test
    void findByLastNameContainingIgnoreCase_matchesPartialName() {
        List<Vet> vets = vetRepository.findByLastNameContainingIgnoreCase("cart");
        assertThat(vets).hasSize(1);
        assertThat(vets.get(0).getLastName()).isEqualTo("Carter");
    }

    @Test
    void findByLastNameContainingIgnoreCase_caseInsensitive() {
        List<Vet> vets = vetRepository.findByLastNameContainingIgnoreCase("CARTER");
        assertThat(vets).hasSize(1);
    }

    @Test
    void findByLastNameContainingIgnoreCase_noMatch_returnsEmpty() {
        List<Vet> vets = vetRepository.findByLastNameContainingIgnoreCase("nonexistent");
        assertThat(vets).isEmpty();
    }

    @Test
    void findByFirstNameContainingIgnoreCase_matchesPartialName() {
        List<Vet> vets = vetRepository.findByFirstNameContainingIgnoreCase("jam");
        assertThat(vets).hasSize(1);
        assertThat(vets.get(0).getFirstName()).isEqualTo("James");
    }

    @Test
    void delete_removesVet() {
        Vet vet = new Vet();
        vet.setFirstName("Delete");
        vet.setLastName("Me");
        vet = vetRepository.save(vet);
        Integer id = vet.getId();

        vetRepository.delete(vet);
        assertThat(vetRepository.findById(id)).isEmpty();
    }

    @Test
    void vetWithMultipleSpecialties() {
        Optional<Vet> vet = vetRepository.findById(3);
        assertThat(vet).isPresent();
        assertThat(vet.get().getSpecialties()).hasSize(2);
    }
}
