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
    void findAll_returnsSeededVets() {
        List<Vet> vets = vetRepository.findAll();
        assertThat(vets).hasSizeGreaterThanOrEqualTo(6);
    }

    @Test
    void findByLastNameContainingIgnoreCase_found() {
        List<Vet> results = vetRepository.findByLastNameContainingIgnoreCase("cart");
        assertThat(results).hasSize(1);
        assertThat(results.get(0).getLastName()).isEqualTo("Carter");
    }

    @Test
    void findByLastNameContainingIgnoreCase_notFound() {
        List<Vet> results = vetRepository.findByLastNameContainingIgnoreCase("zzz");
        assertThat(results).isEmpty();
    }

    @Test
    void findBySpecialtyId_found() {
        List<Vet> results = vetRepository.findBySpecialtyId(1);
        assertThat(results).hasSizeGreaterThanOrEqualTo(1);
    }

    @Test
    void findBySpecialtyId_notFound() {
        List<Vet> results = vetRepository.findBySpecialtyId(999);
        assertThat(results).isEmpty();
    }

    @Test
    void findBySpecialtyName_found() {
        List<Vet> results = vetRepository.findBySpecialtyName("radiology");
        assertThat(results).hasSizeGreaterThanOrEqualTo(1);
    }

    @Test
    void findBySpecialtyName_notFound() {
        List<Vet> results = vetRepository.findBySpecialtyName("nonexistent");
        assertThat(results).isEmpty();
    }

    @Test
    void save_createsVetWithSpecialties() {
        Specialty spec = specialtyRepository.findByNameIgnoreCase("surgery").orElseThrow();
        Vet vet = new Vet();
        vet.setFirstName("Test");
        vet.setLastName("Vet");
        vet.setSpecialties(Set.of(spec));
        vetRepository.saveAndFlush(vet);
        assertThat(vet.getId()).isNotNull();
        assertThat(vet.getSpecialties()).hasSize(1);
    }

    @Test
    void deleteById_removesVet() {
        Vet existing = vetRepository.findById(6).orElseThrow();
        vetRepository.delete(existing);
        vetRepository.flush();
        assertThat(vetRepository.findById(6)).isEmpty();
    }
}
