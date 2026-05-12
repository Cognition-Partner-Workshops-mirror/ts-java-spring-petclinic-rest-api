package com.petclinic.vet.service;

import com.petclinic.vet.dto.SpecialtyResponseDto;
import com.petclinic.vet.dto.VetRequestDto;
import com.petclinic.vet.dto.VetResponseDto;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit tests for {@link VetServiceImpl} with mocked dependencies.
 */
@ExtendWith(MockitoExtension.class)
class VetServiceTest {

    @Mock
    private VetRepository vetRepository;

    @Mock
    private SpecialtyRepository specialtyRepository;

    @Mock
    private VetMapper vetMapper;

    @InjectMocks
    private VetServiceImpl vetService;

    private Vet vet;
    private Specialty radiology;
    private VetResponseDto vetDto;

    @BeforeEach
    void setUp() {
        radiology = new Specialty("radiology");
        radiology.setId(1);

        vet = new Vet("James", "Carter");
        vet.setId(1);

        vetDto = new VetResponseDto(1, "James", "Carter",
                List.of(new SpecialtyResponseDto(1, "radiology")),
                Instant.now(), Instant.now());
    }

    @Test
    void getAllVets_shouldReturnAllVets() {
        when(vetRepository.findAll()).thenReturn(List.of(vet));
        when(vetMapper.toResponseDtoList(any())).thenReturn(List.of(vetDto));

        List<VetResponseDto> result = vetService.getAllVets();

        assertThat(result).hasSize(1);
        verify(vetRepository).findAll();
    }

    @Test
    void getVetById_shouldReturnVet() {
        when(vetRepository.findById(1)).thenReturn(Optional.of(vet));
        when(vetMapper.toResponseDto(vet)).thenReturn(vetDto);

        VetResponseDto result = vetService.getVetById(1);

        assertThat(result.getFirstName()).isEqualTo("James");
        assertThat(result.getLastName()).isEqualTo("Carter");
    }

    @Test
    void getVetById_shouldThrowWhenNotFound() {
        when(vetRepository.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> vetService.getVetById(99))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Vet not found with id: 99");
    }

    @Test
    void createVet_shouldSaveVetWithoutSpecialties() {
        VetRequestDto request = new VetRequestDto("James", "Carter", List.of());
        when(vetRepository.save(any(Vet.class))).thenReturn(vet);
        when(vetMapper.toResponseDto(vet)).thenReturn(vetDto);

        VetResponseDto result = vetService.createVet(request);

        assertThat(result.getFirstName()).isEqualTo("James");
        verify(vetRepository).save(any(Vet.class));
    }

    @Test
    void createVet_shouldSaveVetWithSpecialties() {
        VetRequestDto request = new VetRequestDto("James", "Carter", List.of(1));
        when(specialtyRepository.findById(1)).thenReturn(Optional.of(radiology));
        when(vetRepository.save(any(Vet.class))).thenReturn(vet);
        when(vetMapper.toResponseDto(vet)).thenReturn(vetDto);

        VetResponseDto result = vetService.createVet(request);

        assertThat(result.getFirstName()).isEqualTo("James");
        verify(specialtyRepository).findById(1);
    }

    @Test
    void createVet_shouldThrowWhenSpecialtyNotFound() {
        VetRequestDto request = new VetRequestDto("James", "Carter", List.of(99));
        when(specialtyRepository.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> vetService.createVet(request))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Specialty not found with id: 99");
    }

    @Test
    void createVet_shouldHandleNullSpecialtyIds() {
        VetRequestDto request = new VetRequestDto("James", "Carter", null);
        when(vetRepository.save(any(Vet.class))).thenReturn(vet);
        when(vetMapper.toResponseDto(vet)).thenReturn(vetDto);

        VetResponseDto result = vetService.createVet(request);

        assertThat(result).isNotNull();
        verify(specialtyRepository, never()).findById(any());
    }

    @Test
    void updateVet_shouldUpdateVetFields() {
        VetRequestDto request = new VetRequestDto("Helen", "Leary", List.of());
        when(vetRepository.findById(1)).thenReturn(Optional.of(vet));
        when(vetRepository.save(vet)).thenReturn(vet);
        when(vetMapper.toResponseDto(vet)).thenReturn(vetDto);

        VetResponseDto result = vetService.updateVet(1, request);

        assertThat(result).isNotNull();
        verify(vetRepository).save(vet);
    }

    @Test
    void updateVet_shouldThrowWhenNotFound() {
        VetRequestDto request = new VetRequestDto("Helen", "Leary", List.of());
        when(vetRepository.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> vetService.updateVet(99, request))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void updateVet_shouldReassignSpecialties() {
        VetRequestDto request = new VetRequestDto("James", "Carter", List.of(1));
        when(vetRepository.findById(1)).thenReturn(Optional.of(vet));
        when(specialtyRepository.findById(1)).thenReturn(Optional.of(radiology));
        when(vetRepository.save(vet)).thenReturn(vet);
        when(vetMapper.toResponseDto(vet)).thenReturn(vetDto);

        VetResponseDto result = vetService.updateVet(1, request);

        assertThat(result).isNotNull();
        verify(specialtyRepository).findById(1);
    }

    @Test
    void deleteVet_shouldDeleteWhenExists() {
        when(vetRepository.existsById(1)).thenReturn(true);

        vetService.deleteVet(1);

        verify(vetRepository).deleteById(1);
    }

    @Test
    void deleteVet_shouldThrowWhenNotFound() {
        when(vetRepository.existsById(99)).thenReturn(false);

        assertThatThrownBy(() -> vetService.deleteVet(99))
                .isInstanceOf(ResourceNotFoundException.class);
        verify(vetRepository, never()).deleteById(any());
    }

    @Test
    void searchByLastName_shouldReturnMatchingVets() {
        when(vetRepository.findByLastNameContainingIgnoreCase("Car"))
                .thenReturn(List.of(vet));
        when(vetMapper.toResponseDtoList(any())).thenReturn(List.of(vetDto));

        List<VetResponseDto> result = vetService.searchByLastName("Car");

        assertThat(result).hasSize(1);
    }

    @Test
    void filterBySpecialty_shouldReturnVetsWithSpecialty() {
        when(specialtyRepository.existsById(1)).thenReturn(true);
        when(vetRepository.findBySpecialtyId(1)).thenReturn(List.of(vet));
        when(vetMapper.toResponseDtoList(any())).thenReturn(List.of(vetDto));

        List<VetResponseDto> result = vetService.filterBySpecialty(1);

        assertThat(result).hasSize(1);
    }

    @Test
    void filterBySpecialty_shouldThrowWhenSpecialtyNotFound() {
        when(specialtyRepository.existsById(99)).thenReturn(false);

        assertThatThrownBy(() -> vetService.filterBySpecialty(99))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void filterByLastNameAndSpecialty_shouldReturnFilteredVets() {
        when(specialtyRepository.existsById(1)).thenReturn(true);
        when(vetRepository.findByLastNameAndSpecialtyId("Car", 1))
                .thenReturn(List.of(vet));
        when(vetMapper.toResponseDtoList(any())).thenReturn(List.of(vetDto));

        List<VetResponseDto> result = vetService.filterByLastNameAndSpecialty("Car", 1);

        assertThat(result).hasSize(1);
    }

    @Test
    void filterByLastNameAndSpecialty_shouldThrowWhenSpecialtyNotFound() {
        when(specialtyRepository.existsById(99)).thenReturn(false);

        assertThatThrownBy(() -> vetService.filterByLastNameAndSpecialty("Car", 99))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void searchByLastName_shouldReturnEmptyListWhenNoMatch() {
        when(vetRepository.findByLastNameContainingIgnoreCase("xyz"))
                .thenReturn(List.of());
        when(vetMapper.toResponseDtoList(any())).thenReturn(List.of());

        List<VetResponseDto> result = vetService.searchByLastName("xyz");

        assertThat(result).isEmpty();
    }
}
