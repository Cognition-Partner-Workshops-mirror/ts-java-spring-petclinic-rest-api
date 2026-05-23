package com.petclinic.vet.service;

import com.petclinic.vet.dto.SpecialtyDto;
import com.petclinic.vet.dto.VetRequestDto;
import com.petclinic.vet.entity.Specialty;
import com.petclinic.vet.entity.Vet;
import com.petclinic.vet.exception.ResourceNotFoundException;
import com.petclinic.vet.repository.SpecialtyRepository;
import com.petclinic.vet.repository.VetRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
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

/**
 * Unit tests for VetServiceImpl using Mockito.
 */
@ExtendWith(MockitoExtension.class)
class VetServiceTest {

    @Mock
    private VetRepository vetRepository;

    @Mock
    private SpecialtyRepository specialtyRepository;

    @InjectMocks
    private VetServiceImpl vetService;

    private Vet vet;
    private Specialty radiology;

    @BeforeEach
    void setUp() {
        radiology = new Specialty(1, "radiology");
        vet = new Vet(1, "James", "Carter");
        vet.setSpecialties(Set.of(radiology));
    }

    @Test
    void findAll_returnsAllVets() {
        when(vetRepository.findAll()).thenReturn(List.of(vet));
        List<Vet> result = vetService.findAll();
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getFirstName()).isEqualTo("James");
        verify(vetRepository).findAll();
    }

    @Test
    void findAll_returnsEmptyList() {
        when(vetRepository.findAll()).thenReturn(Collections.emptyList());
        List<Vet> result = vetService.findAll();
        assertThat(result).isEmpty();
    }

    @Test
    void findById_existingId_returnsVet() {
        when(vetRepository.findById(1)).thenReturn(Optional.of(vet));
        Vet result = vetService.findById(1);
        assertThat(result.getFirstName()).isEqualTo("James");
        assertThat(result.getLastName()).isEqualTo("Carter");
    }

    @Test
    void findById_nonExistingId_throwsException() {
        when(vetRepository.findById(99)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> vetService.findById(99))
            .isInstanceOf(ResourceNotFoundException.class)
            .hasMessageContaining("Vet not found with id: 99");
    }

    @Test
    void save_withSpecialties_createsVet() {
        SpecialtyDto specialtyDto = new SpecialtyDto(1, "radiology");
        VetRequestDto dto = new VetRequestDto("James", "Carter", List.of(specialtyDto));
        when(specialtyRepository.findByNameIn(anySet())).thenReturn(List.of(radiology));
        when(vetRepository.save(any(Vet.class))).thenReturn(vet);

        Vet result = vetService.save(dto);
        assertThat(result.getFirstName()).isEqualTo("James");
        verify(vetRepository).save(any(Vet.class));
    }

    @Test
    void save_withEmptySpecialties_createsVet() {
        VetRequestDto dto = new VetRequestDto("James", "Carter", Collections.emptyList());
        Vet vetNoSpecialties = new Vet(2, "James", "Carter");
        when(vetRepository.save(any(Vet.class))).thenReturn(vetNoSpecialties);

        Vet result = vetService.save(dto);
        assertThat(result.getFirstName()).isEqualTo("James");
        verify(specialtyRepository, never()).findByNameIn(anySet());
    }

    @Test
    void save_withUnknownSpecialty_throwsException() {
        SpecialtyDto specialtyDto = new SpecialtyDto(null, "unknown");
        VetRequestDto dto = new VetRequestDto("James", "Carter", List.of(specialtyDto));
        when(specialtyRepository.findByNameIn(anySet())).thenReturn(Collections.emptyList());

        assertThatThrownBy(() -> vetService.save(dto))
            .isInstanceOf(ResourceNotFoundException.class)
            .hasMessageContaining("Specialties not found");
    }

    @Test
    void update_existingVet_updatesFields() {
        SpecialtyDto specialtyDto = new SpecialtyDto(1, "radiology");
        VetRequestDto dto = new VetRequestDto("Helen", "Leary", List.of(specialtyDto));
        when(vetRepository.findById(1)).thenReturn(Optional.of(vet));
        when(specialtyRepository.findByNameIn(anySet())).thenReturn(List.of(radiology));
        when(vetRepository.save(any(Vet.class))).thenReturn(vet);

        Vet result = vetService.update(1, dto);
        assertThat(result).isNotNull();
        verify(vetRepository).save(any(Vet.class));
    }

    @Test
    void update_nonExistingVet_throwsException() {
        VetRequestDto dto = new VetRequestDto("Helen", "Leary", Collections.emptyList());
        when(vetRepository.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> vetService.update(99, dto))
            .isInstanceOf(ResourceNotFoundException.class)
            .hasMessageContaining("Vet not found with id: 99");
    }

    @Test
    void delete_existingVet_deletesSuccessfully() {
        when(vetRepository.findById(1)).thenReturn(Optional.of(vet));
        vetService.delete(1);
        verify(vetRepository).delete(vet);
    }

    @Test
    void delete_nonExistingVet_throwsException() {
        when(vetRepository.findById(99)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> vetService.delete(99))
            .isInstanceOf(ResourceNotFoundException.class)
            .hasMessageContaining("Vet not found with id: 99");
    }

    @Test
    void findBySpecialty_returnsMatchingVets() {
        when(vetRepository.findBySpecialtyName("radiology")).thenReturn(List.of(vet));
        List<Vet> result = vetService.findBySpecialty("radiology");
        assertThat(result).hasSize(1);
    }

    @Test
    void searchByLastName_returnsMatchingVets() {
        when(vetRepository.findByLastNameContainingIgnoreCase("Carter")).thenReturn(List.of(vet));
        List<Vet> result = vetService.searchByLastName("Carter");
        assertThat(result).hasSize(1);
    }
}
