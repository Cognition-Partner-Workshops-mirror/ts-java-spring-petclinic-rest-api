package com.petclinic.vet.service;

import com.petclinic.vet.dto.SpecialtyDto;
import com.petclinic.vet.dto.VetDto;
import com.petclinic.vet.dto.VetRequestDto;
import com.petclinic.vet.entity.Specialty;
import com.petclinic.vet.entity.Vet;
import com.petclinic.vet.exception.ResourceNotFoundException;
import com.petclinic.vet.mapper.VetMapper;
import com.petclinic.vet.repository.SpecialtyRepository;
import com.petclinic.vet.repository.VetRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit tests for VetServiceImpl.
 * Uses Mockito to isolate service logic from the repository and mapper layers.
 */
@ExtendWith(MockitoExtension.class)
class VetServiceImplTest {

    @Mock
    private VetRepository vetRepository;

    @Mock
    private SpecialtyRepository specialtyRepository;

    @Mock
    private VetMapper vetMapper;

    @InjectMocks
    private VetServiceImpl vetService;

    private Vet vet;
    private VetDto vetDto;
    private Specialty radiology;

    @BeforeEach
    void setUp() {
        radiology = new Specialty();
        radiology.setId(1);
        radiology.setName("radiology");
        radiology.setCreatedAt(Instant.now());
        radiology.setUpdatedAt(Instant.now());

        vet = new Vet();
        vet.setId(1);
        vet.setFirstName("James");
        vet.setLastName("Carter");
        vet.setSpecialties(Set.of(radiology));
        vet.setCreatedAt(Instant.now());
        vet.setUpdatedAt(Instant.now());

        vetDto = new VetDto(1, "James", "Carter",
            List.of(new SpecialtyDto(1, "radiology")));
    }

    @Test
    void listVets_returnsAllVets() {
        when(vetRepository.findAll()).thenReturn(List.of(vet));
        when(vetMapper.toDtoList(List.of(vet))).thenReturn(List.of(vetDto));

        List<VetDto> result = vetService.listVets();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getFirstName()).isEqualTo("James");
    }

    @Test
    void getVet_existingId_returnsDto() {
        when(vetRepository.findById(1)).thenReturn(Optional.of(vet));
        when(vetMapper.toDto(vet)).thenReturn(vetDto);

        VetDto result = vetService.getVet(1);

        assertThat(result.getId()).isEqualTo(1);
        assertThat(result.getFirstName()).isEqualTo("James");
    }

    @Test
    void getVet_nonExistingId_throwsNotFound() {
        when(vetRepository.findById(999)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> vetService.getVet(999))
            .isInstanceOf(ResourceNotFoundException.class)
            .hasMessageContaining("Vet not found with id: 999");
    }

    @Test
    void createVet_withSpecialties_savesAndReturnsDto() {
        VetRequestDto request = new VetRequestDto("Helen", "Leary", List.of(1));
        Vet savedVet = new Vet();
        savedVet.setId(2);
        savedVet.setFirstName("Helen");
        savedVet.setLastName("Leary");
        savedVet.setSpecialties(Set.of(radiology));
        VetDto expectedDto = new VetDto(2, "Helen", "Leary",
            List.of(new SpecialtyDto(1, "radiology")));

        when(specialtyRepository.findById(1)).thenReturn(Optional.of(radiology));
        when(vetRepository.save(any(Vet.class))).thenReturn(savedVet);
        when(vetMapper.toDto(savedVet)).thenReturn(expectedDto);

        VetDto result = vetService.createVet(request);

        assertThat(result.getFirstName()).isEqualTo("Helen");
        assertThat(result.getSpecialties()).hasSize(1);
    }

    @Test
    void createVet_withNoSpecialties_savesSuccessfully() {
        VetRequestDto request = new VetRequestDto("Solo", "Vet", null);
        Vet savedVet = new Vet();
        savedVet.setId(3);
        savedVet.setFirstName("Solo");
        savedVet.setLastName("Vet");
        VetDto expectedDto = new VetDto(3, "Solo", "Vet", List.of());

        when(vetRepository.save(any(Vet.class))).thenReturn(savedVet);
        when(vetMapper.toDto(savedVet)).thenReturn(expectedDto);

        VetDto result = vetService.createVet(request);

        assertThat(result.getFirstName()).isEqualTo("Solo");
        assertThat(result.getSpecialties()).isEmpty();
    }

    @Test
    void createVet_withEmptySpecialtyList_savesSuccessfully() {
        VetRequestDto request = new VetRequestDto("Solo", "Vet", List.of());
        Vet savedVet = new Vet();
        savedVet.setId(3);
        savedVet.setFirstName("Solo");
        savedVet.setLastName("Vet");
        VetDto expectedDto = new VetDto(3, "Solo", "Vet", List.of());

        when(vetRepository.save(any(Vet.class))).thenReturn(savedVet);
        when(vetMapper.toDto(savedVet)).thenReturn(expectedDto);

        VetDto result = vetService.createVet(request);

        assertThat(result.getSpecialties()).isEmpty();
    }

    @Test
    void createVet_withInvalidSpecialtyId_throwsNotFound() {
        VetRequestDto request = new VetRequestDto("Bad", "Vet", List.of(999));
        when(specialtyRepository.findById(999)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> vetService.createVet(request))
            .isInstanceOf(ResourceNotFoundException.class)
            .hasMessageContaining("Specialty not found with id: 999");
    }

    @Test
    void updateVet_existingId_updatesAndReturnsDto() {
        VetRequestDto request = new VetRequestDto("Updated", "Name", List.of(1));
        Vet updatedVet = new Vet();
        updatedVet.setId(1);
        updatedVet.setFirstName("Updated");
        updatedVet.setLastName("Name");
        updatedVet.setSpecialties(Set.of(radiology));
        VetDto expectedDto = new VetDto(1, "Updated", "Name",
            List.of(new SpecialtyDto(1, "radiology")));

        when(vetRepository.findById(1)).thenReturn(Optional.of(vet));
        when(specialtyRepository.findById(1)).thenReturn(Optional.of(radiology));
        when(vetRepository.save(vet)).thenReturn(updatedVet);
        when(vetMapper.toDto(updatedVet)).thenReturn(expectedDto);

        VetDto result = vetService.updateVet(1, request);

        assertThat(result.getFirstName()).isEqualTo("Updated");
    }

    @Test
    void updateVet_nonExistingId_throwsNotFound() {
        VetRequestDto request = new VetRequestDto("Any", "Name", List.of());
        when(vetRepository.findById(999)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> vetService.updateVet(999, request))
            .isInstanceOf(ResourceNotFoundException.class)
            .hasMessageContaining("Vet not found with id: 999");
    }

    @Test
    void deleteVet_existingId_deletesAndReturnsDto() {
        when(vetRepository.findById(1)).thenReturn(Optional.of(vet));
        when(vetMapper.toDto(vet)).thenReturn(vetDto);

        VetDto result = vetService.deleteVet(1);

        assertThat(result.getId()).isEqualTo(1);
        verify(vetRepository).delete(vet);
    }

    @Test
    void deleteVet_nonExistingId_throwsNotFound() {
        when(vetRepository.findById(999)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> vetService.deleteVet(999))
            .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void findBySpecialty_delegatesToRepository() {
        when(vetRepository.findBySpecialtyId(1)).thenReturn(List.of(vet));
        when(vetMapper.toDtoList(List.of(vet))).thenReturn(List.of(vetDto));

        List<VetDto> result = vetService.findBySpecialty(1);

        assertThat(result).hasSize(1);
    }

    @Test
    void searchByLastName_delegatesToRepository() {
        when(vetRepository.findByLastNameContainingIgnoreCase("Carter"))
            .thenReturn(List.of(vet));
        when(vetMapper.toDtoList(List.of(vet))).thenReturn(List.of(vetDto));

        List<VetDto> result = vetService.searchByLastName("Carter");

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getLastName()).isEqualTo("Carter");
    }
}
