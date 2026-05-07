package com.petclinic.vet.repository;

import com.petclinic.vet.entity.Specialty;
import com.petclinic.vet.entity.Vet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
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

    private Vet james;
    private Vet helen;
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

        james = new Vet();
        james.setFirstName("James");
        james.setLastName("Carter");
        james.setSpecialties(Set.of(radiology));
        james = vetRepository.save(james);

        helen = new Vet();
        helen.setFirstName("Helen");
        helen.setLastName("Leary");
        helen.setSpecialties(Set.of(radiology, surgery));
        helen = vetRepository.save(helen);
    }

    @Test
    void findAll_returnsAllVets() {
        List<Vet> result = vetRepository.findAll();
        assertThat(result).hasSize(2);
    }

    @Test
    void findById_returnsVet() {
        Optional<Vet> result = vetRepository.findById(james.getId());
        assertThat(result).isPresent();
        assertThat(result.get().getFirstName()).isEqualTo("James");
    }

    @Test
    void findById_notFound() {
        Optional<Vet> result = vetRepository.findById(999);
        assertThat(result).isEmpty();
    }

    @Test
    void findBySpecialtyId_returnsMatchingVets() {
        List<Vet> result = vetRepository.findBySpecialtyId(radiology.getId());
        assertThat(result).hasSize(2);
    }

    @Test
    void findBySpecialtyId_singleResult() {
        List<Vet> result = vetRepository.findBySpecialtyId(surgery.getId());
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getFirstName()).isEqualTo("Helen");
    }

    @Test
    void findByLastNameContainingIgnoreCase_findsMatch() {
        List<Vet> result = vetRepository.findByLastNameContainingIgnoreCase("cart");
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getLastName()).isEqualTo("Carter");
    }

    @Test
    void findByLastNameContainingIgnoreCase_noMatch() {
        List<Vet> result = vetRepository.findByLastNameContainingIgnoreCase("xyz");
        assertThat(result).isEmpty();
    }

    @Test
    void findBySpecialtyName_returnsMatchingVets() {
        List<Vet> result = vetRepository.findBySpecialtyName("surgery");
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getFirstName()).isEqualTo("Helen");
    }

    @Test
    void findBySpecialtyName_caseInsensitive() {
        List<Vet> result = vetRepository.findBySpecialtyName("RADIOLOGY");
        assertThat(result).hasSize(2);
    }

    @Test
    void save_createsVet() {
        Vet newVet = new Vet();
        newVet.setFirstName("Linda");
        newVet.setLastName("Douglas");
        Vet saved = vetRepository.save(newVet);
        assertThat(saved.getId()).isNotNull();
    }

    @Test
    void delete_removesVet() {
        vetRepository.delete(james);
        assertThat(vetRepository.findById(james.getId())).isEmpty();
    }
}
