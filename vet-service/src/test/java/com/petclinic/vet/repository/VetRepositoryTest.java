package com.petclinic.vet.repository;

import com.petclinic.vet.entity.Specialty;
import com.petclinic.vet.entity.Vet;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Sql(statements = {
    "DELETE FROM vet_specialties",
    "DELETE FROM vets",
    "DELETE FROM specialties",
    "ALTER TABLE specialties ALTER COLUMN id RESTART WITH 100",
    "ALTER TABLE vets ALTER COLUMN id RESTART WITH 100",
    "INSERT INTO specialties (name, created_at, updated_at) VALUES ('radiology', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)",
    "INSERT INTO specialties (name, created_at, updated_at) VALUES ('surgery', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)",
    "INSERT INTO vets (first_name, last_name, created_at, updated_at) VALUES ('James', 'Carter', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)",
    "INSERT INTO vets (first_name, last_name, created_at, updated_at) VALUES ('Helen', 'Leary', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)",
    "INSERT INTO vets (first_name, last_name, created_at, updated_at) VALUES ('Linda', 'Douglas', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)",
    "INSERT INTO vet_specialties (vet_id, specialty_id) VALUES (100, 100)",
    "INSERT INTO vet_specialties (vet_id, specialty_id) VALUES (101, 100)",
    "INSERT INTO vet_specialties (vet_id, specialty_id) VALUES (101, 101)"
}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
class VetRepositoryTest {

    @Autowired
    private VetRepository vetRepository;

    @Autowired
    private SpecialtyRepository specialtyRepository;

    @Test
    void findBySpecialtyId_returnsVetsWithSpecialty() {
        List<Vet> result = vetRepository.findBySpecialtyId(100);
        assertThat(result).hasSize(2);
    }

    @Test
    void findBySpecialtyId_noResults() {
        List<Vet> result = vetRepository.findBySpecialtyId(999);
        assertThat(result).isEmpty();
    }

    @Test
    void findByLastNameContainingIgnoreCase_returnsMatches() {
        List<Vet> result = vetRepository.findByLastNameContainingIgnoreCase("cart");
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getFirstName()).isEqualTo("James");
    }

    @Test
    void findByLastNameContainingIgnoreCase_caseInsensitive() {
        List<Vet> result = vetRepository.findByLastNameContainingIgnoreCase("LEARY");
        assertThat(result).hasSize(1);
    }

    @Test
    void findByLastNameContainingIgnoreCase_noMatches() {
        List<Vet> result = vetRepository.findByLastNameContainingIgnoreCase("nonexistent");
        assertThat(result).isEmpty();
    }

    @Test
    void findBySpecialtyName_returnsVets() {
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
    void findAll_returnsAllVets() {
        List<Vet> result = vetRepository.findAll();
        assertThat(result).hasSize(3);
    }
}
