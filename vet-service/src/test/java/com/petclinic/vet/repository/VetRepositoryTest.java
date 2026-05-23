package com.petclinic.vet.repository;

import com.petclinic.vet.entity.Specialty;
import com.petclinic.vet.entity.Vet;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Repository integration tests for VetRepository using @DataJpaTest with H2.
 */
@DataJpaTest
@ActiveProfiles("test")
class VetRepositoryTest {

    @Autowired
    private VetRepository vetRepository;

    @Autowired
    private SpecialtyRepository specialtyRepository;

    private Specialty radiology;
    private Specialty surgery;

    @BeforeEach
    void setUp() {
        vetRepository.deleteAll();
        specialtyRepository.deleteAll();

        radiology = new Specialty();
        radiology.setName("radiology");
        radiology = specialtyRepository.save(radiology);

        surgery = new Specialty();
        surgery.setName("surgery");
        surgery = specialtyRepository.save(surgery);
    }

    @Test
    void save_and_findById() {
        Vet vet = new Vet();
        vet.setFirstName("James");
        vet.setLastName("Carter");
        vet.setSpecialties(Set.of(radiology));
        Vet saved = vetRepository.save(vet);

        Optional<Vet> found = vetRepository.findById(saved.getId());
        assertThat(found).isPresent();
        assertThat(found.get().getFirstName()).isEqualTo("James");
        assertThat(found.get().getSpecialties()).hasSize(1);
    }

    @Test
    void findAll_returnsAllVets() {
        Vet vet1 = new Vet();
        vet1.setFirstName("James");
        vet1.setLastName("Carter");
        vetRepository.save(vet1);

        Vet vet2 = new Vet();
        vet2.setFirstName("Helen");
        vet2.setLastName("Leary");
        vetRepository.save(vet2);

        List<Vet> all = vetRepository.findAll();
        assertThat(all).hasSize(2);
    }

    @Test
    void findByLastNameContainingIgnoreCase_returnsMatches() {
        Vet vet = new Vet();
        vet.setFirstName("James");
        vet.setLastName("Carter");
        vetRepository.save(vet);

        List<Vet> result = vetRepository.findByLastNameContainingIgnoreCase("cart");
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getLastName()).isEqualTo("Carter");
    }

    @Test
    void findByLastNameContainingIgnoreCase_noMatches() {
        Vet vet = new Vet();
        vet.setFirstName("James");
        vet.setLastName("Carter");
        vetRepository.save(vet);

        List<Vet> result = vetRepository.findByLastNameContainingIgnoreCase("unknown");
        assertThat(result).isEmpty();
    }

    @Test
    void findBySpecialtyId_returnsMatchingVets() {
        Vet vet = new Vet();
        vet.setFirstName("James");
        vet.setLastName("Carter");
        vet.setSpecialties(Set.of(radiology));
        vetRepository.save(vet);

        List<Vet> result = vetRepository.findBySpecialtyId(radiology.getId());
        assertThat(result).hasSize(1);
    }

    @Test
    void findBySpecialtyId_noMatches() {
        Vet vet = new Vet();
        vet.setFirstName("James");
        vet.setLastName("Carter");
        vet.setSpecialties(Set.of(radiology));
        vetRepository.save(vet);

        List<Vet> result = vetRepository.findBySpecialtyId(surgery.getId());
        assertThat(result).isEmpty();
    }

    @Test
    void findBySpecialtyName_returnsMatchingVets() {
        Vet vet = new Vet();
        vet.setFirstName("James");
        vet.setLastName("Carter");
        vet.setSpecialties(Set.of(radiology));
        vetRepository.save(vet);

        List<Vet> result = vetRepository.findBySpecialtyName("radiology");
        assertThat(result).hasSize(1);
    }

    @Test
    void findBySpecialtyName_caseInsensitive() {
        Vet vet = new Vet();
        vet.setFirstName("James");
        vet.setLastName("Carter");
        vet.setSpecialties(Set.of(radiology));
        vetRepository.save(vet);

        List<Vet> result = vetRepository.findBySpecialtyName("RADIOLOGY");
        assertThat(result).hasSize(1);
    }

    @Test
    void delete_removesVet() {
        Vet vet = new Vet();
        vet.setFirstName("James");
        vet.setLastName("Carter");
        vet = vetRepository.save(vet);
        vetRepository.delete(vet);

        assertThat(vetRepository.findById(vet.getId())).isEmpty();
    }

    @Test
    void update_modifiesVet() {
        Vet vet = new Vet();
        vet.setFirstName("James");
        vet.setLastName("Carter");
        vet = vetRepository.save(vet);

        vet.setFirstName("Helen");
        Vet updated = vetRepository.save(vet);
        assertThat(updated.getFirstName()).isEqualTo("Helen");
    }
}
