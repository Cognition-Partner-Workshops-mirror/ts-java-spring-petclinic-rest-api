package com.petclinic.vet.repository;

import com.petclinic.vet.entity.Specialty;
import com.petclinic.vet.entity.Vet;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
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
    void findById_returnsVet() {
        Optional<Vet> vet = vetRepository.findById(1);
        assertThat(vet).isPresent();
        assertThat(vet.get().getFirstName()).isEqualTo("James");
        assertThat(vet.get().getLastName()).isEqualTo("Carter");
    }

    @Test
    void findById_returnsEmptyForNonExistent() {
        Optional<Vet> vet = vetRepository.findById(999);
        assertThat(vet).isEmpty();
    }

    @Test
    void findById_vetWithSpecialties() {
        Optional<Vet> vet = vetRepository.findById(3);
        assertThat(vet).isPresent();
        assertThat(vet.get().getSpecialties()).hasSize(2);
    }

    @Test
    void save_createsNewVet() {
        Vet vet = new Vet();
        vet.setFirstName("John");
        vet.setLastName("Doe");
        Vet saved = vetRepository.save(vet);
        assertThat(saved.getId()).isNotNull();
    }

    @Test
    void save_vetWithSpecialties() {
        Specialty specialty = specialtyRepository.findById(1).orElseThrow();
        Vet vet = new Vet();
        vet.setFirstName("Test");
        vet.setLastName("Vet");
        vet.setSpecialties(Set.of(specialty));
        Vet saved = vetRepository.save(vet);
        assertThat(saved.getSpecialties()).hasSize(1);
    }

    @Test
    void delete_removesVet() {
        Vet vet = new Vet();
        vet.setFirstName("Temp");
        vet.setLastName("Vet");
        vet = vetRepository.save(vet);
        Integer id = vet.getId();
        vetRepository.delete(vet);
        vetRepository.flush();
        assertThat(vetRepository.findById(id)).isEmpty();
    }

    @Test
    void findBySpecialtyId_findsVets() {
        List<Vet> vets = vetRepository.findBySpecialtyId(1);
        assertThat(vets).hasSizeGreaterThanOrEqualTo(2);
    }

    @Test
    void findBySpecialtyId_noResults() {
        List<Vet> vets = vetRepository.findBySpecialtyId(999);
        assertThat(vets).isEmpty();
    }

    @Test
    void findByLastNameContainingIgnoreCase_findsVets() {
        List<Vet> vets = vetRepository.findByLastNameContainingIgnoreCase("carter");
        assertThat(vets).hasSize(1);
        assertThat(vets.get(0).getFirstName()).isEqualTo("James");
    }

    @Test
    void findByLastNameContainingIgnoreCase_partialMatch() {
        List<Vet> vets = vetRepository.findByLastNameContainingIgnoreCase("art");
        assertThat(vets).hasSize(1);
    }

    @Test
    void findByLastNameContainingIgnoreCase_noResults() {
        List<Vet> vets = vetRepository.findByLastNameContainingIgnoreCase("nonexistent");
        assertThat(vets).isEmpty();
    }

    @Test
    void findBySpecialtyName_findsVets() {
        List<Vet> vets = vetRepository.findBySpecialtyName("radiology");
        assertThat(vets).hasSizeGreaterThanOrEqualTo(2);
    }

    @Test
    void findBySpecialtyName_caseInsensitive() {
        List<Vet> vets = vetRepository.findBySpecialtyName("RADIOLOGY");
        assertThat(vets).hasSizeGreaterThanOrEqualTo(2);
    }

    @Test
    void findBySpecialtyName_noResults() {
        List<Vet> vets = vetRepository.findBySpecialtyName("nonexistent");
        assertThat(vets).isEmpty();
    }
}
