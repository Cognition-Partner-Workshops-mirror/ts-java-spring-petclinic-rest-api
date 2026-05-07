package com.petclinic.vet.service;

import com.petclinic.vet.entity.Specialty;
import com.petclinic.vet.entity.Vet;
import com.petclinic.vet.exception.ResourceNotFoundException;
import com.petclinic.vet.repository.SpecialtyRepository;
import com.petclinic.vet.repository.VetRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anySet;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class VetServiceTest {

    @Mock
    private VetRepository vetRepository;

    @Mock
    private SpecialtyRepository specialtyRepository;

    @InjectMocks
    private VetService vetService;

    private Vet sampleVet;
    private Specialty radiology;

    @BeforeEach
    void setUp() {
        radiology = new Specialty();
        radiology.setId(1);
        radiology.setName("radiology");

        sampleVet = new Vet();
        sampleVet.setId(1);
        sampleVet.setFirstName("James");
        sampleVet.setLastName("Carter");
        sampleVet.setSpecialties(new HashSet<>(Set.of(radiology)));
    }

    @Nested
    class FindAllVets {
        @Test
        void returnsAllVets() {
            when(vetRepository.findAll()).thenReturn(List.of(sampleVet));
            List<Vet> result = vetService.findAllVets();
            assertThat(result).hasSize(1);
            verify(vetRepository).findAll();
        }

        @Test
        void returnsEmptyListWhenNoVets() {
            when(vetRepository.findAll()).thenReturn(List.of());
            List<Vet> result = vetService.findAllVets();
            assertThat(result).isEmpty();
        }
    }

    @Nested
    class FindVetById {
        @Test
        void returnsVetWhenFound() {
            when(vetRepository.findById(1)).thenReturn(Optional.of(sampleVet));
            Vet result = vetService.findVetById(1);
            assertThat(result.getFirstName()).isEqualTo("James");
        }

        @Test
        void throwsNotFoundWhenMissing() {
            when(vetRepository.findById(99)).thenReturn(Optional.empty());
            assertThatThrownBy(() -> vetService.findVetById(99))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Vet not found with id 99");
        }
    }

    @Nested
    class SaveVet {
        @Test
        void savesVetWithSpecialties() {
            when(specialtyRepository.findByNameInIgnoreCase(anySet())).thenReturn(List.of(radiology));
            when(vetRepository.save(any(Vet.class))).thenReturn(sampleVet);

            Vet result = vetService.saveVet(sampleVet);
            assertThat(result).isNotNull();
            verify(vetRepository).save(any(Vet.class));
            verify(specialtyRepository).findByNameInIgnoreCase(anySet());
        }

        @Test
        void savesVetWithoutSpecialties() {
            Vet vetNoSpecs = new Vet();
            vetNoSpecs.setFirstName("John");
            vetNoSpecs.setLastName("Doe");
            vetNoSpecs.setSpecialties(new HashSet<>());

            when(vetRepository.save(any(Vet.class))).thenReturn(vetNoSpecs);

            Vet result = vetService.saveVet(vetNoSpecs);
            assertThat(result).isNotNull();
            verify(specialtyRepository, never()).findByNameInIgnoreCase(anySet());
        }

        @Test
        void savesVetWithNullSpecialties() {
            Vet vetNullSpecs = new Vet();
            vetNullSpecs.setFirstName("Jane");
            vetNullSpecs.setLastName("Doe");
            vetNullSpecs.setSpecialties(null);

            when(vetRepository.save(any(Vet.class))).thenReturn(vetNullSpecs);

            Vet result = vetService.saveVet(vetNullSpecs);
            assertThat(result).isNotNull();
            verify(specialtyRepository, never()).findByNameInIgnoreCase(anySet());
        }
    }

    @Nested
    class UpdateVet {
        @Test
        void updatesExistingVet() {
            Vet updated = new Vet();
            updated.setFirstName("Updated");
            updated.setLastName("Name");
            updated.setSpecialties(new HashSet<>(Set.of(radiology)));

            when(vetRepository.findById(1)).thenReturn(Optional.of(sampleVet));
            when(specialtyRepository.findByNameInIgnoreCase(anySet())).thenReturn(List.of(radiology));
            when(vetRepository.save(any(Vet.class))).thenReturn(sampleVet);

            Vet result = vetService.updateVet(1, updated);
            assertThat(result).isNotNull();
            verify(vetRepository).save(any(Vet.class));
        }

        @Test
        void throwsNotFoundForInvalidId() {
            when(vetRepository.findById(99)).thenReturn(Optional.empty());
            Vet updated = new Vet();
            assertThatThrownBy(() -> vetService.updateVet(99, updated))
                .isInstanceOf(ResourceNotFoundException.class);
        }
    }

    @Nested
    class DeleteVet {
        @Test
        void deletesExistingVet() {
            when(vetRepository.findById(1)).thenReturn(Optional.of(sampleVet));
            vetService.deleteVet(1);
            verify(vetRepository).delete(sampleVet);
        }

        @Test
        void throwsNotFoundForInvalidId() {
            when(vetRepository.findById(99)).thenReturn(Optional.empty());
            assertThatThrownBy(() -> vetService.deleteVet(99))
                .isInstanceOf(ResourceNotFoundException.class);
        }
    }

    @Nested
    class SearchAndFilter {
        @Test
        void searchVetsByName() {
            when(vetRepository.searchByName("James")).thenReturn(List.of(sampleVet));
            List<Vet> result = vetService.searchVetsByName("James");
            assertThat(result).hasSize(1);
        }

        @Test
        void findVetsBySpecialty() {
            when(vetRepository.findBySpecialtyName("radiology")).thenReturn(List.of(sampleVet));
            List<Vet> result = vetService.findVetsBySpecialty("radiology");
            assertThat(result).hasSize(1);
        }
    }

    @Nested
    class SpecialtyOperations {
        @Test
        void findAllSpecialties() {
            when(specialtyRepository.findAll()).thenReturn(List.of(radiology));
            List<Specialty> result = vetService.findAllSpecialties();
            assertThat(result).hasSize(1);
        }

        @Test
        void findSpecialtyById() {
            when(specialtyRepository.findById(1)).thenReturn(Optional.of(radiology));
            Specialty result = vetService.findSpecialtyById(1);
            assertThat(result.getName()).isEqualTo("radiology");
        }

        @Test
        void findSpecialtyByIdThrowsNotFound() {
            when(specialtyRepository.findById(99)).thenReturn(Optional.empty());
            assertThatThrownBy(() -> vetService.findSpecialtyById(99))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Specialty not found with id 99");
        }

        @Test
        void saveSpecialty() {
            when(specialtyRepository.save(any(Specialty.class))).thenReturn(radiology);
            Specialty result = vetService.saveSpecialty(radiology);
            assertThat(result).isNotNull();
        }

        @Test
        void updateSpecialty() {
            Specialty updated = new Specialty();
            updated.setName("updated-radiology");

            when(specialtyRepository.findById(1)).thenReturn(Optional.of(radiology));
            when(specialtyRepository.save(any(Specialty.class))).thenReturn(radiology);

            Specialty result = vetService.updateSpecialty(1, updated);
            assertThat(result).isNotNull();
            verify(specialtyRepository).save(any(Specialty.class));
        }

        @Test
        void updateSpecialtyThrowsNotFound() {
            when(specialtyRepository.findById(99)).thenReturn(Optional.empty());
            Specialty updated = new Specialty();
            assertThatThrownBy(() -> vetService.updateSpecialty(99, updated))
                .isInstanceOf(ResourceNotFoundException.class);
        }

        @Test
        void deleteSpecialty() {
            when(specialtyRepository.findById(1)).thenReturn(Optional.of(radiology));
            vetService.deleteSpecialty(1);
            verify(specialtyRepository).delete(radiology);
        }

        @Test
        void deleteSpecialtyThrowsNotFound() {
            when(specialtyRepository.findById(99)).thenReturn(Optional.empty());
            assertThatThrownBy(() -> vetService.deleteSpecialty(99))
                .isInstanceOf(ResourceNotFoundException.class);
        }
    }
}
