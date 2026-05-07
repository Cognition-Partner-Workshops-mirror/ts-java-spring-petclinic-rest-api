package com.petclinic.vet.repository;

import com.petclinic.vet.entity.Vet;
import java.util.List;
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

    @Test
    void findBySpecialtyId_returnsVetsWithSpecialty() {
        List<Vet> result = vetRepository.findBySpecialtyId(1);

        assertThat(result).isNotEmpty();
        assertThat(result).allSatisfy(vet ->
            assertThat(vet.getSpecialties()).anyMatch(s -> s.getId().equals(1)));
    }

    @Test
    void findBySpecialtyId_noMatch() {
        List<Vet> result = vetRepository.findBySpecialtyId(999);

        assertThat(result).isEmpty();
    }

    @Test
    void findByLastNameContainingIgnoreCase_returnsMatchingVets() {
        List<Vet> result = vetRepository.findByLastNameContainingIgnoreCase("carter");

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getLastName()).isEqualTo("Carter");
    }

    @Test
    void findByLastNameContainingIgnoreCase_noMatch() {
        List<Vet> result = vetRepository.findByLastNameContainingIgnoreCase("xyz");

        assertThat(result).isEmpty();
    }

    @Test
    void findBySpecialtyName_returnsVetsWithSpecialtyName() {
        List<Vet> result = vetRepository.findBySpecialtyName("radiology");

        assertThat(result).isNotEmpty();
        assertThat(result).allSatisfy(vet ->
            assertThat(vet.getSpecialties()).anyMatch(s -> s.getName().equals("radiology")));
    }

    @Test
    void findBySpecialtyName_caseInsensitive() {
        List<Vet> result = vetRepository.findBySpecialtyName("RADIOLOGY");

        assertThat(result).isNotEmpty();
    }

    @Test
    void findBySpecialtyName_noMatch() {
        List<Vet> result = vetRepository.findBySpecialtyName("xyz");

        assertThat(result).isEmpty();
    }

    @Test
    void findAll_returnsSeedData() {
        List<Vet> result = vetRepository.findAll();

        assertThat(result).hasSize(6);
    }

    @Test
    void saveAndRetrieve() {
        Vet vet = new Vet("Test", "Vet");
        Vet saved = vetRepository.saveAndFlush(vet);

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getId()).isGreaterThan(6);

        Vet found = vetRepository.findById(saved.getId()).orElseThrow();
        assertThat(found.getFirstName()).isEqualTo("Test");
        assertThat(found.getLastName()).isEqualTo("Vet");
    }
}
