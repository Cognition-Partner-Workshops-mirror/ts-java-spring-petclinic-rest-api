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

    private Specialty radiology;
    private Specialty surgery;
    private Vet james;
    private Vet helen;

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
        List<Vet> all = vetRepository.findAll();
        assertThat(all).hasSize(2);
    }

    @Test
    void findById_existingId_returnsVet() {
        Optional<Vet> found = vetRepository.findById(james.getId());
        assertThat(found).isPresent();
        assertThat(found.get().getFirstName()).isEqualTo("James");
    }

    @Test
    void findById_nonExistingId_returnsEmpty() {
        Optional<Vet> found = vetRepository.findById(999);
        assertThat(found).isEmpty();
    }

    @Test
    void findBySpecialtyId_returnsVetsWithSpecialty() {
        List<Vet> vets = vetRepository.findBySpecialtyId(radiology.getId());
        assertThat(vets).hasSize(2);
    }

    @Test
    void findBySpecialtyId_surgeryOnly_returnsHelen() {
        List<Vet> vets = vetRepository.findBySpecialtyId(surgery.getId());
        assertThat(vets).hasSize(1);
        assertThat(vets.get(0).getFirstName()).isEqualTo("Helen");
    }

    @Test
    void findByLastNameContainingIgnoreCase_matchingName() {
        List<Vet> vets = vetRepository.findByLastNameContainingIgnoreCase("cart");
        assertThat(vets).hasSize(1);
        assertThat(vets.get(0).getLastName()).isEqualTo("Carter");
    }

    @Test
    void findByLastNameContainingIgnoreCase_noMatch() {
        List<Vet> vets = vetRepository.findByLastNameContainingIgnoreCase("Smith");
        assertThat(vets).isEmpty();
    }

    @Test
    void findBySpecialtyName_matchingName() {
        List<Vet> vets = vetRepository.findBySpecialtyName("surgery");
        assertThat(vets).hasSize(1);
        assertThat(vets.get(0).getFirstName()).isEqualTo("Helen");
    }

    @Test
    void findBySpecialtyName_noMatch() {
        List<Vet> vets = vetRepository.findBySpecialtyName("dentistry");
        assertThat(vets).isEmpty();
    }

    @Test
    void save_newVet_assignsId() {
        Vet newVet = new Vet();
        newVet.setFirstName("Linda");
        newVet.setLastName("Douglas");
        Vet saved = vetRepository.save(newVet);
        assertThat(saved.getId()).isNotNull();
    }

    @Test
    void delete_removesVet() {
        vetRepository.delete(james);
        Optional<Vet> found = vetRepository.findById(james.getId());
        assertThat(found).isEmpty();
    }
}
