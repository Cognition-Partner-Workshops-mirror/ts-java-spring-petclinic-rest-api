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

    @BeforeEach
    void setUp() {
        vetRepository.deleteAll();
        specialtyRepository.deleteAll();

        radiology = new Specialty();
        radiology.setName("radiology");
        radiology = specialtyRepository.save(radiology);

        Specialty surgery = new Specialty();
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
    void findById_existingId_returnsVet() {
        Optional<Vet> result = vetRepository.findById(james.getId());
        assertThat(result).isPresent();
        assertThat(result.get().getFirstName()).isEqualTo("James");
    }

    @Test
    void findById_nonExistingId_returnsEmpty() {
        Optional<Vet> result = vetRepository.findById(999);
        assertThat(result).isEmpty();
    }

    @Test
    void findBySpecialtyName_returnsMatchingVets() {
        List<Vet> result = vetRepository.findBySpecialtyName("radiology");
        assertThat(result).hasSize(2);
    }

    @Test
    void findBySpecialtyName_noMatch() {
        List<Vet> result = vetRepository.findBySpecialtyName("dentistry");
        assertThat(result).isEmpty();
    }

    @Test
    void searchByName_matchesFirstName() {
        List<Vet> result = vetRepository.searchByName("James");
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getFirstName()).isEqualTo("James");
    }

    @Test
    void searchByName_matchesLastName() {
        List<Vet> result = vetRepository.searchByName("Leary");
        assertThat(result).hasSize(1);
    }

    @Test
    void searchByName_partialMatch() {
        List<Vet> result = vetRepository.searchByName("art");
        assertThat(result).hasSize(1);
    }

    @Test
    void searchByName_caseInsensitive() {
        List<Vet> result = vetRepository.searchByName("james");
        assertThat(result).hasSize(1);
    }

    @Test
    void searchByName_noMatch() {
        List<Vet> result = vetRepository.searchByName("Unknown");
        assertThat(result).isEmpty();
    }

    @Test
    void findByLastNameIgnoreCase_returnsMatch() {
        List<Vet> result = vetRepository.findByLastNameIgnoreCase("carter");
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getFirstName()).isEqualTo("James");
    }

    @Test
    void save_createsVetWithAuditFields() {
        Vet vet = new Vet();
        vet.setFirstName("Linda");
        vet.setLastName("Douglas");
        Vet saved = vetRepository.save(vet);

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getCreatedAt()).isNotNull();
        assertThat(saved.getUpdatedAt()).isNotNull();
    }

    @Test
    void delete_removesVet() {
        vetRepository.delete(james);
        assertThat(vetRepository.findById(james.getId())).isEmpty();
    }
}
