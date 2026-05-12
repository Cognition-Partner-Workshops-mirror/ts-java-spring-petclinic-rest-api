package com.petclinic.vet.repository;

import com.petclinic.vet.entity.Specialty;
import com.petclinic.vet.entity.Vet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
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
    void findAll_returnsSeededVets() {
        List<Vet> all = vetRepository.findAll();
        assertThat(all).hasSizeGreaterThanOrEqualTo(6);
    }

    @Test
    void findById_returnsVet() {
        Optional<Vet> found = vetRepository.findById(1);
        assertThat(found).isPresent();
        assertThat(found.get().getFirstName()).isEqualTo("James");
        assertThat(found.get().getLastName()).isEqualTo("Carter");
    }

    @Test
    void findById_notFound() {
        Optional<Vet> found = vetRepository.findById(999);
        assertThat(found).isEmpty();
    }

    @Test
    void findByLastNameContainingIgnoreCase_returnsMatches() {
        List<Vet> found = vetRepository.findByLastNameContainingIgnoreCase("cart");
        assertThat(found).hasSize(1);
        assertThat(found.get(0).getLastName()).isEqualTo("Carter");
    }

    @Test
    void findBySpecialtyName_returnsVetsWithSpecialty() {
        List<Vet> found = vetRepository.findBySpecialtyName("radiology");
        assertThat(found).hasSizeGreaterThanOrEqualTo(2);
    }

    @Test
    void findBySpecialtyNameContaining_returnsMatches() {
        List<Vet> found = vetRepository.findBySpecialtyNameContaining("surg");
        assertThat(found).hasSizeGreaterThanOrEqualTo(2);
    }

    @Test
    void findByNameContaining_matchesFirstOrLastName() {
        List<Vet> found = vetRepository.findByNameContaining("james");
        assertThat(found).hasSize(1);
        assertThat(found.get(0).getFirstName()).isEqualTo("James");
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
    void save_vetWithSpecialties() {
        Specialty s = specialtyRepository.findById(1).orElseThrow();
        Vet vet = new Vet();
        vet.setFirstName("New");
        vet.setLastName("Doctor");
        vet.setSpecialties(Set.of(s));
        Vet saved = vetRepository.save(vet);
        assertThat(saved.getSpecialties()).hasSize(1);
    }

    @Test
    void delete_removesVet() {
        Vet vet = new Vet();
        vet.setFirstName("Temp");
        vet.setLastName("Vet");
        Vet saved = vetRepository.save(vet);
        vetRepository.delete(saved);
        assertThat(vetRepository.findById(saved.getId())).isEmpty();
    }
}
