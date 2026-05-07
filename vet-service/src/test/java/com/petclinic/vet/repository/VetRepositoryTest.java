package com.petclinic.vet.repository;

import com.petclinic.vet.entity.Specialty;
import com.petclinic.vet.entity.Vet;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
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
    void shouldFindAllVets() {
        List<Vet> vets = vetRepository.findAll();
        assertThat(vets).hasSizeGreaterThanOrEqualTo(6);
    }

    @Test
    void shouldFindVetById() {
        Vet vet = vetRepository.findById(1).orElse(null);
        assertThat(vet).isNotNull();
        assertThat(vet.getFirstName()).isEqualTo("James");
        assertThat(vet.getLastName()).isEqualTo("Carter");
    }

    @Test
    void shouldFindBySpecialtyName() {
        List<Vet> vets = vetRepository.findBySpecialtyName("radiology");
        assertThat(vets).hasSizeGreaterThanOrEqualTo(2);
    }

    @Test
    void shouldFindBySpecialtyNameNoMatch() {
        List<Vet> vets = vetRepository.findBySpecialtyName("nonexistent");
        assertThat(vets).isEmpty();
    }

    @Test
    void shouldFindByLastNameContainingIgnoreCase() {
        List<Vet> vets = vetRepository.findByLastNameContainingIgnoreCase("cart");
        assertThat(vets).hasSize(1);
        assertThat(vets.get(0).getLastName()).isEqualTo("Carter");
    }

    @Test
    void shouldFindByNameContainingIgnoreCase() {
        List<Vet> vets = vetRepository.findByNameContainingIgnoreCase("james");
        assertThat(vets).hasSizeGreaterThanOrEqualTo(1);
    }

    @Test
    void shouldFindByNameContainingIgnoreCaseNoMatch() {
        List<Vet> vets = vetRepository.findByNameContainingIgnoreCase("zzz");
        assertThat(vets).isEmpty();
    }

    @Test
    void shouldSaveVet() {
        Vet vet = new Vet();
        vet.setFirstName("John");
        vet.setLastName("Doe");
        Vet saved = vetRepository.save(vet);
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getFirstName()).isEqualTo("John");
    }

    @Test
    void shouldSaveVetWithSpecialties() {
        Specialty radiology = specialtyRepository.findById(1).orElseThrow();
        Vet vet = new Vet();
        vet.setFirstName("Jane");
        vet.setLastName("Smith");
        vet.setSpecialties(Set.of(radiology));
        Vet saved = vetRepository.save(vet);
        assertThat(saved.getSpecialties()).hasSize(1);
    }

    @Test
    void shouldDeleteVet() {
        Vet vet = new Vet();
        vet.setFirstName("Temp");
        vet.setLastName("Vet");
        Vet saved = vetRepository.save(vet);
        Integer id = saved.getId();

        vetRepository.deleteById(id);
        assertThat(vetRepository.findById(id)).isEmpty();
    }
}
