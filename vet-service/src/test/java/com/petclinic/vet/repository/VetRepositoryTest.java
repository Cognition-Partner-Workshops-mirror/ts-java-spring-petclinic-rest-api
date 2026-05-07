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
    void shouldFindAllVets() {
        List<Vet> vets = vetRepository.findAll();
        assertThat(vets).hasSizeGreaterThanOrEqualTo(6);
    }

    @Test
    void shouldFindVetById() {
        Optional<Vet> vet = vetRepository.findById(1);
        assertThat(vet).isPresent();
        assertThat(vet.get().getFirstName()).isEqualTo("James");
        assertThat(vet.get().getLastName()).isEqualTo("Carter");
    }

    @Test
    void shouldFindBySpecialtyId() {
        List<Vet> vets = vetRepository.findBySpecialtyId(1);
        assertThat(vets).isNotEmpty();
        assertThat(vets).allSatisfy(v ->
            assertThat(v.getSpecialties()).anyMatch(s -> s.getId().equals(1))
        );
    }

    @Test
    void shouldFindByLastNameContainingIgnoreCase() {
        List<Vet> vets = vetRepository.findByLastNameContainingIgnoreCase("cart");
        assertThat(vets).hasSize(1);
        assertThat(vets.get(0).getLastName()).isEqualTo("Carter");
    }

    @Test
    void shouldFindBySpecialtyName() {
        List<Vet> vets = vetRepository.findBySpecialtyName("radiology");
        assertThat(vets).isNotEmpty();
        assertThat(vets).allSatisfy(v ->
            assertThat(v.getSpecialties()).anyMatch(s -> s.getName().equals("radiology"))
        );
    }

    @Test
    void shouldReturnEmptyForNonExistentLastName() {
        List<Vet> vets = vetRepository.findByLastNameContainingIgnoreCase("nonexistent");
        assertThat(vets).isEmpty();
    }

    @Test
    void shouldSaveNewVet() {
        Specialty radiology = specialtyRepository.findById(1).orElseThrow();
        Vet vet = new Vet();
        vet.setFirstName("Test");
        vet.setLastName("Vet");
        vet.setSpecialties(Set.of(radiology));
        Vet saved = vetRepository.save(vet);
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getSpecialties()).hasSize(1);
    }

    @Test
    void shouldDeleteVet() {
        Vet vet = new Vet();
        vet.setFirstName("Delete");
        vet.setLastName("Me");
        Vet saved = vetRepository.save(vet);
        vetRepository.deleteById(saved.getId());
        assertThat(vetRepository.findById(saved.getId())).isEmpty();
    }
}
