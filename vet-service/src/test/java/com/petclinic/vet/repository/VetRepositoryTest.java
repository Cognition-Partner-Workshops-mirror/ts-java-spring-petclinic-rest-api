package com.petclinic.vet.repository;

import com.petclinic.vet.entity.Specialty;
import com.petclinic.vet.entity.Vet;
import com.petclinic.vet.config.JpaAuditingConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
@Import(JpaAuditingConfig.class)
class VetRepositoryTest {

    @Autowired
    private VetRepository vetRepository;

    @Autowired
    private SpecialtyRepository specialtyRepository;

    @Test
    void findAll_shouldReturnSeededVets() {
        List<Vet> vets = vetRepository.findAll();
        assertThat(vets).hasSizeGreaterThanOrEqualTo(6);
    }

    @Test
    void findById_shouldReturnVet() {
        Optional<Vet> vet = vetRepository.findById(1);
        assertThat(vet).isPresent();
        assertThat(vet.get().getFirstName()).isEqualTo("James");
        assertThat(vet.get().getLastName()).isEqualTo("Carter");
    }

    @Test
    void findById_shouldReturnEmptyForNonExistent() {
        Optional<Vet> vet = vetRepository.findById(999);
        assertThat(vet).isEmpty();
    }

    @Test
    void findBySpecialtyName_shouldReturnVetsWithSpecialty() {
        List<Vet> vets = vetRepository.findBySpecialtyName("radiology");
        assertThat(vets).hasSizeGreaterThanOrEqualTo(2);
    }

    @Test
    void findBySpecialtyName_shouldReturnEmptyForUnknownSpecialty() {
        List<Vet> vets = vetRepository.findBySpecialtyName("unknown");
        assertThat(vets).isEmpty();
    }

    @Test
    void findByLastNameContainingIgnoreCase_shouldMatch() {
        List<Vet> vets = vetRepository.findByLastNameContainingIgnoreCase("Cart");
        assertThat(vets).hasSize(1);
        assertThat(vets.get(0).getLastName()).isEqualTo("Carter");
    }

    @Test
    void findByNameContainingIgnoreCase_shouldMatchFirstOrLastName() {
        List<Vet> vets = vetRepository.findByNameContainingIgnoreCase("helen");
        assertThat(vets).hasSize(1);
        assertThat(vets.get(0).getFirstName()).isEqualTo("Helen");
    }

    @Test
    void findByNameContainingIgnoreCase_shouldReturnEmptyForNoMatch() {
        List<Vet> vets = vetRepository.findByNameContainingIgnoreCase("zzz");
        assertThat(vets).isEmpty();
    }

    @Test
    void save_shouldPersistNewVet() {
        Vet vet = new Vet();
        vet.setFirstName("Test");
        vet.setLastName("Vet");
        Vet saved = vetRepository.save(vet);
        assertThat(saved.getId()).isNotNull();
    }

    @Test
    void save_shouldPersistVetWithSpecialties() {
        Specialty specialty = specialtyRepository.findById(1).orElseThrow();
        Vet vet = new Vet();
        vet.setFirstName("New");
        vet.setLastName("Doctor");
        vet.setSpecialties(Set.of(specialty));
        Vet saved = vetRepository.save(vet);
        assertThat(saved.getSpecialties()).hasSize(1);
    }

    @Test
    void delete_shouldRemoveVet() {
        Vet vet = new Vet();
        vet.setFirstName("ToDelete");
        vet.setLastName("Vet");
        Vet saved = vetRepository.save(vet);
        Integer id = saved.getId();

        vetRepository.deleteById(id);
        assertThat(vetRepository.findById(id)).isEmpty();
    }
}
