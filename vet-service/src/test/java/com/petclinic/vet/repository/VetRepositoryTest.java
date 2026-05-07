package com.petclinic.vet.repository;

import com.petclinic.vet.entity.Vet;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class VetRepositoryTest {

    @Autowired
    private VetRepository repository;

    @Test
    void shouldFindAll() {
        List<Vet> vets = repository.findAll();
        assertThat(vets).hasSizeGreaterThanOrEqualTo(6);
    }

    @Test
    void shouldFindById() {
        Optional<Vet> vet = repository.findById(1);
        assertThat(vet).isPresent();
        assertThat(vet.get().getFirstName()).isEqualTo("James");
        assertThat(vet.get().getLastName()).isEqualTo("Carter");
    }

    @Test
    void shouldReturnEmptyForNonExistentId() {
        Optional<Vet> vet = repository.findById(999);
        assertThat(vet).isEmpty();
    }

    @Test
    void shouldFindByLastNameContaining() {
        List<Vet> results = repository.findByLastNameContainingIgnoreCase("cart");
        assertThat(results).hasSize(1);
        assertThat(results.get(0).getLastName()).isEqualTo("Carter");
    }

    @Test
    void shouldSearchByFirstName() {
        List<Vet> results = repository.searchByName("James");
        assertThat(results).hasSize(1);
        assertThat(results.get(0).getFirstName()).isEqualTo("James");
    }

    @Test
    void shouldSearchByLastName() {
        List<Vet> results = repository.searchByName("Douglas");
        assertThat(results).hasSize(1);
        assertThat(results.get(0).getLastName()).isEqualTo("Douglas");
    }

    @Test
    void shouldReturnEmptyForUnknownSearch() {
        List<Vet> results = repository.searchByName("zzzzz");
        assertThat(results).isEmpty();
    }

    @Test
    void shouldFindBySpecialtyId() {
        List<Vet> results = repository.findBySpecialtyId(1);
        assertThat(results).hasSizeGreaterThanOrEqualTo(2);
    }

    @Test
    void shouldFindBySpecialtyName() {
        List<Vet> results = repository.findBySpecialtyName("radiology");
        assertThat(results).hasSizeGreaterThanOrEqualTo(2);
    }

    @Test
    void shouldSaveNewVet() {
        Vet vet = new Vet();
        vet.setFirstName("Test");
        vet.setLastName("Vet");
        Vet saved = repository.save(vet);

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getFirstName()).isEqualTo("Test");
        assertThat(saved.getCreatedAt()).isNotNull();
        assertThat(saved.getUpdatedAt()).isNotNull();
    }

    @Test
    void shouldDeleteVet() {
        Vet vet = new Vet();
        vet.setFirstName("Temp");
        vet.setLastName("Doc");
        Vet saved = repository.save(vet);
        Integer id = saved.getId();

        repository.deleteById(id);

        assertThat(repository.findById(id)).isEmpty();
    }
}
