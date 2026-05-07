package com.petclinic.vet.repository;

import com.petclinic.vet.entity.Specialty;
import com.petclinic.vet.entity.Vet;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class VetRepositoryTest {

    @Autowired
    private VetRepository vetRepository;

    @Autowired
    private SpecialtyRepository specialtyRepository;

    @BeforeEach
    void setUp() {
        vetRepository.deleteAll();
        specialtyRepository.deleteAll();
    }

    @Test
    void shouldSaveAndFindById() {
        Vet vet = new Vet("James", "Carter");
        vet.setCreatedAt(LocalDateTime.now());
        vet.setUpdatedAt(LocalDateTime.now());
        Vet saved = vetRepository.save(vet);

        assertThat(saved.getId()).isNotNull();
        assertThat(vetRepository.findById(saved.getId())).isPresent();
    }

    @Test
    void shouldFindByLastNameContainingIgnoreCase() {
        Vet v1 = new Vet("James", "Carter");
        v1.setCreatedAt(LocalDateTime.now());
        v1.setUpdatedAt(LocalDateTime.now());
        Vet v2 = new Vet("Helen", "Leary");
        v2.setCreatedAt(LocalDateTime.now());
        v2.setUpdatedAt(LocalDateTime.now());
        vetRepository.saveAll(List.of(v1, v2));

        List<Vet> result = vetRepository.findByLastNameContainingIgnoreCase("cart");
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getFirstName()).isEqualTo("James");
    }

    @Test
    void shouldFindBySpecialtyId() {
        Specialty specialty = new Specialty("radiology");
        specialty.setCreatedAt(LocalDateTime.now());
        specialty.setUpdatedAt(LocalDateTime.now());
        specialty = specialtyRepository.save(specialty);

        Vet vet = new Vet("James", "Carter");
        vet.setCreatedAt(LocalDateTime.now());
        vet.setUpdatedAt(LocalDateTime.now());
        vet.setSpecialties(Set.of(specialty));
        vetRepository.save(vet);

        Vet vet2 = new Vet("Helen", "Leary");
        vet2.setCreatedAt(LocalDateTime.now());
        vet2.setUpdatedAt(LocalDateTime.now());
        vetRepository.save(vet2);

        List<Vet> result = vetRepository.findBySpecialtyId(specialty.getId());
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getFirstName()).isEqualTo("James");
    }

    @Test
    void shouldFindAllWithSpecialties() {
        Specialty specialty = new Specialty("surgery");
        specialty.setCreatedAt(LocalDateTime.now());
        specialty.setUpdatedAt(LocalDateTime.now());
        specialty = specialtyRepository.save(specialty);

        Vet vet = new Vet("James", "Carter");
        vet.setCreatedAt(LocalDateTime.now());
        vet.setUpdatedAt(LocalDateTime.now());
        vet.setSpecialties(Set.of(specialty));
        vetRepository.save(vet);

        List<Vet> result = vetRepository.findAllWithSpecialties();
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getSpecialties()).hasSize(1);
    }

    @Test
    void shouldFindByIdWithSpecialties() {
        Specialty specialty = new Specialty("dentistry");
        specialty.setCreatedAt(LocalDateTime.now());
        specialty.setUpdatedAt(LocalDateTime.now());
        specialty = specialtyRepository.save(specialty);

        Vet vet = new Vet("Helen", "Leary");
        vet.setCreatedAt(LocalDateTime.now());
        vet.setUpdatedAt(LocalDateTime.now());
        vet.setSpecialties(Set.of(specialty));
        vet = vetRepository.save(vet);

        Optional<Vet> result = vetRepository.findByIdWithSpecialties(vet.getId());
        assertThat(result).isPresent();
        assertThat(result.get().getSpecialties()).hasSize(1);
    }

    @Test
    void shouldDeleteVet() {
        Vet vet = new Vet("James", "Carter");
        vet.setCreatedAt(LocalDateTime.now());
        vet.setUpdatedAt(LocalDateTime.now());
        Vet saved = vetRepository.save(vet);

        vetRepository.deleteById(saved.getId());
        assertThat(vetRepository.findById(saved.getId())).isEmpty();
    }
}
