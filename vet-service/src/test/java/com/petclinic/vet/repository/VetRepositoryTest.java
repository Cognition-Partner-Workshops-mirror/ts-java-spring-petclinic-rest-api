package com.petclinic.vet.repository;

import com.petclinic.vet.config.JpaAuditingConfig;
import com.petclinic.vet.entity.Specialty;
import com.petclinic.vet.entity.Vet;
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
    void findAll_returnsSeedData() {
        List<Vet> all = vetRepository.findAll();
        assertThat(all).hasSizeGreaterThanOrEqualTo(6);
    }

    @Test
    void findBySpecialtyName_found() {
        List<Vet> vets = vetRepository.findBySpecialtyName("radiology");
        assertThat(vets).isNotEmpty();
    }

    @Test
    void findBySpecialtyName_notFound() {
        List<Vet> vets = vetRepository.findBySpecialtyName("nonexistent");
        assertThat(vets).isEmpty();
    }

    @Test
    void searchByName_byLastName() {
        List<Vet> vets = vetRepository.searchByName("Carter");
        assertThat(vets).isNotEmpty();
        assertThat(vets.getFirst().getLastName()).isEqualTo("Carter");
    }

    @Test
    void searchByName_byFirstName() {
        List<Vet> vets = vetRepository.searchByName("Helen");
        assertThat(vets).isNotEmpty();
        assertThat(vets.getFirst().getFirstName()).isEqualTo("Helen");
    }

    @Test
    void searchByName_caseInsensitive() {
        List<Vet> vets = vetRepository.searchByName("carter");
        assertThat(vets).isNotEmpty();
    }

    @Test
    void searchByName_notFound() {
        List<Vet> vets = vetRepository.searchByName("Nonexistent");
        assertThat(vets).isEmpty();
    }

    @Test
    void saveAndFind() {
        Vet vet = new Vet();
        vet.setFirstName("Test");
        vet.setLastName("Vet");
        Vet saved = vetRepository.save(vet);

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getCreatedAt()).isNotNull();

        Optional<Vet> found = vetRepository.findById(saved.getId());
        assertThat(found).isPresent();
        assertThat(found.get().getFirstName()).isEqualTo("Test");
    }

    @Test
    void saveWithSpecialties() {
        Specialty specialty = specialtyRepository.findAll().getFirst();

        Vet vet = new Vet();
        vet.setFirstName("New");
        vet.setLastName("Vet");
        vet.setSpecialties(Set.of(specialty));
        Vet saved = vetRepository.save(vet);

        Optional<Vet> found = vetRepository.findById(saved.getId());
        assertThat(found).isPresent();
        assertThat(found.get().getSpecialties()).hasSize(1);
    }

    @Test
    void delete_removesEntity() {
        Vet vet = new Vet();
        vet.setFirstName("Temp");
        vet.setLastName("Vet");
        Vet saved = vetRepository.save(vet);

        vetRepository.deleteById(saved.getId());

        Optional<Vet> found = vetRepository.findById(saved.getId());
        assertThat(found).isEmpty();
    }
}
