package com.petclinic.vet.repository;

import com.petclinic.vet.entity.Specialty;
import com.petclinic.vet.entity.Vet;
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
    void findAll_returnsSeededVets() {
        List<Vet> all = vetRepository.findAll();
        assertThat(all).hasSizeGreaterThanOrEqualTo(6);
    }

    @Test
    void findById_returnsVet() {
        Optional<Vet> vet = vetRepository.findById(1);
        assertThat(vet).isPresent();
        assertThat(vet.get().getFirstName()).isEqualTo("James");
        assertThat(vet.get().getLastName()).isEqualTo("Carter");
    }

    @Test
    void findById_returnsEmptyForMissingId() {
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
        Specialty spec = specialtyRepository.findById(1).orElseThrow();
        Vet vet = new Vet();
        vet.setFirstName("Test");
        vet.setLastName("Vet");
        vet.setSpecialties(Set.of(spec));
        Vet saved = vetRepository.save(vet);
        assertThat(saved.getSpecialties()).hasSize(1);
    }

    @Test
    void findBySpecialtyId_returnsVetsWithSpecialty() {
        List<Vet> vets = vetRepository.findBySpecialtyId(1);
        assertThat(vets).isNotEmpty();
        assertThat(vets.stream().anyMatch(v -> v.getLastName().equals("Leary"))).isTrue();
    }

    @Test
    void findBySpecialtyId_returnsEmptyForUnusedSpecialty() {
        Specialty spec = new Specialty();
        spec.setName("unused");
        Specialty saved = specialtyRepository.save(spec);
        List<Vet> vets = vetRepository.findBySpecialtyId(saved.getId());
        assertThat(vets).isEmpty();
    }

    @Test
    void findByLastNameContainingIgnoreCase_returnsMatches() {
        List<Vet> vets = vetRepository.findByLastNameContainingIgnoreCase("cart");
        assertThat(vets).hasSize(1);
        assertThat(vets.get(0).getLastName()).isEqualTo("Carter");
    }

    @Test
    void findByLastNameContainingIgnoreCase_returnsEmptyForNoMatch() {
        List<Vet> vets = vetRepository.findByLastNameContainingIgnoreCase("zzz");
        assertThat(vets).isEmpty();
    }

    @Test
    void findBySpecialtyName_returnsMatches() {
        List<Vet> vets = vetRepository.findBySpecialtyName("radiology");
        assertThat(vets).isNotEmpty();
    }

    @Test
    void findBySpecialtyName_returnsEmptyForNoMatch() {
        List<Vet> vets = vetRepository.findBySpecialtyName("nonexistent");
        assertThat(vets).isEmpty();
    }

    @Test
    void delete_removesVet() {
        Vet vet = new Vet();
        vet.setFirstName("Delete");
        vet.setLastName("Me");
        Vet saved = vetRepository.save(vet);
        vetRepository.delete(saved);
        assertThat(vetRepository.findById(saved.getId())).isEmpty();
    }
}
