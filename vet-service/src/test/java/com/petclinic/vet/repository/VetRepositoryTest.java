package com.petclinic.vet.repository;

import com.petclinic.vet.entity.Vet;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class VetRepositoryTest {

    @Autowired
    private VetRepository vetRepository;

    @Test
    void findAllWithSpecialties_returnsAllVets() {
        List<Vet> vets = vetRepository.findAllWithSpecialties();
        assertThat(vets).hasSize(6);
    }

    @Test
    void findByIdWithSpecialties_existingVetWithSpecialties_returnsVetWithSpecialties() {
        Optional<Vet> result = vetRepository.findByIdWithSpecialties(2);
        assertThat(result).isPresent();
        Vet vet = result.get();
        assertThat(vet.getFirstName()).isEqualTo("Helen");
        assertThat(vet.getSpecialties()).hasSize(1);
    }

    @Test
    void findByIdWithSpecialties_vetWithoutSpecialties_returnsVetWithEmptySpecialties() {
        Optional<Vet> result = vetRepository.findByIdWithSpecialties(1);
        assertThat(result).isPresent();
        Vet vet = result.get();
        assertThat(vet.getFirstName()).isEqualTo("James");
        assertThat(vet.getSpecialties()).isEmpty();
    }

    @Test
    void findByIdWithSpecialties_nonExistingId_returnsEmpty() {
        Optional<Vet> result = vetRepository.findByIdWithSpecialties(999);
        assertThat(result).isEmpty();
    }

    @Test
    void findByLastNameContainingIgnoreCase_matchingName_returnsVets() {
        List<Vet> result = vetRepository.findByLastNameContainingIgnoreCase("carter");
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getLastName()).isEqualTo("Carter");
    }

    @Test
    void findByLastNameContainingIgnoreCase_partialMatch_returnsVets() {
        List<Vet> result = vetRepository.findByLastNameContainingIgnoreCase("en");
        assertThat(result).hasSizeGreaterThanOrEqualTo(1);
    }

    @Test
    void findByLastNameContainingIgnoreCase_noMatch_returnsEmpty() {
        List<Vet> result = vetRepository.findByLastNameContainingIgnoreCase("zzz");
        assertThat(result).isEmpty();
    }

    @Test
    void findBySpecialtyId_existingSpecialty_returnsVets() {
        List<Vet> result = vetRepository.findBySpecialtyId(1);
        assertThat(result).hasSize(2);
    }

    @Test
    void findBySpecialtyId_nonExistingSpecialty_returnsEmpty() {
        List<Vet> result = vetRepository.findBySpecialtyId(999);
        assertThat(result).isEmpty();
    }

    @Test
    void findBySpecialtyNameContainingIgnoreCase_matchingName_returnsVets() {
        List<Vet> result = vetRepository.findBySpecialtyNameContainingIgnoreCase("surgery");
        assertThat(result).hasSize(2);
    }

    @Test
    void findBySpecialtyNameContainingIgnoreCase_noMatch_returnsEmpty() {
        List<Vet> result = vetRepository.findBySpecialtyNameContainingIgnoreCase("oncology");
        assertThat(result).isEmpty();
    }

    @Test
    void findByIdWithSpecialties_vetWithMultipleSpecialties_returnsAll() {
        Optional<Vet> result = vetRepository.findByIdWithSpecialties(3);
        assertThat(result).isPresent();
        assertThat(result.get().getSpecialties()).hasSize(2);
    }
}
