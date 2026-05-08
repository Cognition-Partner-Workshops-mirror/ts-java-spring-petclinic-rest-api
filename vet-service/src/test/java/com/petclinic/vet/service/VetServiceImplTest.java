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

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for VetServiceImpl.
 * Mocks dependencies to test vet business logic in isolation.
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
    private Specialty specialty;
    private VetResponseDto vetResponseDto;
    private VetRequestDto vetRequestDto;

    @BeforeEach
    void setUp() {
        specialty = new Specialty(1, "radiology");
        vet = new Vet(1, "James", "Carter");
        vet.setSpecialties(Set.of(specialty));

        SpecialtyResponseDto specialtyDto = new SpecialtyResponseDto(1, "radiology");
        vetResponseDto = new VetResponseDto(1, "James", "Carter", List.of(specialtyDto));
        vetRequestDto = new VetRequestDto("James", "Carter", List.of(specialtyDto));
    }

    @Test
    void getAllVets_returnsListOfVets() {
        when(vetRepository.findAll()).thenReturn(List.of(vet));
        when(vetMapper.toResponseDto(vet)).thenReturn(vetResponseDto);

        List<VetResponseDto> result = vetService.getAllVets();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getFirstName()).isEqualTo("James");
        assertThat(result.get(0).getSpecialties()).hasSize(1);
    }

    @Test
    void getAllVets_returnsEmptyListWhenNoneExist() {
        when(vetRepository.findAll()).thenReturn(List.of());

        List<VetResponseDto> result = vetService.getAllVets();

        assertThat(result).isEmpty();
    }

    @Test
    void getVetById_returnsVetWhenFound() {
        when(vetRepository.findById(1)).thenReturn(Optional.of(vet));
        when(vetMapper.toResponseDto(vet)).thenReturn(vetResponseDto);

        VetResponseDto result = vetService.getVetById(1);

        assertThat(result.getId()).isEqualTo(1);
        assertThat(result.getFirstName()).isEqualTo("James");
        assertThat(result.getLastName()).isEqualTo("Carter");
    }

    @Test
    void getVetById_throwsNotFoundWhenMissing() {
        when(vetRepository.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> vetService.getVetById(99))
            .isInstanceOf(ResourceNotFoundException.class)
            .hasMessageContaining("Vet not found with id: 99");
    }

    @Test
    void createVet_savesVetWithSpecialties() {
        when(vetMapper.toEntity(vetRequestDto)).thenReturn(vet);
        when(specialtyRepository.findById(1)).thenReturn(Optional.of(specialty));
        when(vetRepository.save(vet)).thenReturn(vet);
        when(vetMapper.toResponseDto(vet)).thenReturn(vetResponseDto);

        VetResponseDto result = vetService.createVet(vetRequestDto);

        assertThat(result.getFirstName()).isEqualTo("James");
        assertThat(result.getSpecialties()).hasSize(1);
        verify(vetRepository).save(vet);
    }

    @Test
    void createVet_withEmptySpecialtiesList() {
        VetRequestDto noSpecialtiesRequest = new VetRequestDto("James", "Carter", Collections.emptyList());
        Vet vetNoSpec = new Vet(1, "James", "Carter");
        VetResponseDto responseNoSpec = new VetResponseDto(1, "James", "Carter", List.of());

        when(vetMapper.toEntity(noSpecialtiesRequest)).thenReturn(vetNoSpec);
        when(vetRepository.save(vetNoSpec)).thenReturn(vetNoSpec);
        when(vetMapper.toResponseDto(vetNoSpec)).thenReturn(responseNoSpec);

        VetResponseDto result = vetService.createVet(noSpecialtiesRequest);

        assertThat(result.getSpecialties()).isEmpty();
    }

    @Test
    void createVet_throwsWhenSpecialtyNotFound() {
        SpecialtyResponseDto missingSpecialtyDto = new SpecialtyResponseDto(99, "nonexistent");
        VetRequestDto requestWithBadSpecialty = new VetRequestDto("James", "Carter", List.of(missingSpecialtyDto));
        Vet newVet = new Vet(null, "James", "Carter");

        when(vetMapper.toEntity(requestWithBadSpecialty)).thenReturn(newVet);
        when(specialtyRepository.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> vetService.createVet(requestWithBadSpecialty))
            .isInstanceOf(ResourceNotFoundException.class)
            .hasMessageContaining("Specialty not found with id: 99");
    }

    @Test
    void updateVet_updatesExistingVet() {
        SpecialtyResponseDto specialtyDto = new SpecialtyResponseDto(1, "radiology");
        VetRequestDto updateRequest = new VetRequestDto("Helen", "Leary", List.of(specialtyDto));
        VetResponseDto updatedResponse = new VetResponseDto(1, "Helen", "Leary", List.of(specialtyDto));

        when(vetRepository.findById(1)).thenReturn(Optional.of(vet));
        doNothing().when(vetMapper).updateEntityFromDto(updateRequest, vet);
        when(specialtyRepository.findById(1)).thenReturn(Optional.of(specialty));
        when(vetRepository.save(vet)).thenReturn(vet);
        when(vetMapper.toResponseDto(vet)).thenReturn(updatedResponse);

        VetResponseDto result = vetService.updateVet(1, updateRequest);

        assertThat(result.getFirstName()).isEqualTo("Helen");
        verify(vetMapper).updateEntityFromDto(updateRequest, vet);
    }

    @Test
    void updateVet_throwsNotFoundForMissingId() {
        when(vetRepository.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> vetService.updateVet(99, vetRequestDto))
            .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void deleteVet_deletesAndReturnsVet() {
        when(vetRepository.findById(1)).thenReturn(Optional.of(vet));
        when(vetMapper.toResponseDto(vet)).thenReturn(vetResponseDto);

        VetResponseDto result = vetService.deleteVet(1);

        assertThat(result.getId()).isEqualTo(1);
        verify(vetRepository).delete(vet);
    }

    @Test
    void deleteVet_throwsNotFoundForMissingId() {
        when(vetRepository.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> vetService.deleteVet(99))
            .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void searchByLastName_returnsMatchingVets() {
        when(vetRepository.findByLastNameContainingIgnoreCase("Carter")).thenReturn(List.of(vet));
        when(vetMapper.toResponseDto(vet)).thenReturn(vetResponseDto);

        List<VetResponseDto> result = vetService.searchByLastName("Carter");

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getLastName()).isEqualTo("Carter");
    }

    @Test
    void filterBySpecialty_returnsVetsWithSpecialty() {
        when(vetRepository.findBySpecialtyId(1)).thenReturn(List.of(vet));
        when(vetMapper.toResponseDto(vet)).thenReturn(vetResponseDto);

        List<VetResponseDto> result = vetService.filterBySpecialty(1);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getSpecialties()).hasSize(1);
    }

    @Test
    void createVet_withNullSpecialtiesList() {
        VetRequestDto nullSpecialtiesRequest = new VetRequestDto("James", "Carter", null);
        Vet vetNoSpec = new Vet(1, "James", "Carter");
        VetResponseDto responseNoSpec = new VetResponseDto(1, "James", "Carter", List.of());

        when(vetMapper.toEntity(nullSpecialtiesRequest)).thenReturn(vetNoSpec);
        when(vetRepository.save(vetNoSpec)).thenReturn(vetNoSpec);
        when(vetMapper.toResponseDto(vetNoSpec)).thenReturn(responseNoSpec);

        VetResponseDto result = vetService.createVet(nullSpecialtiesRequest);

        assertThat(result.getSpecialties()).isEmpty();
    }
}
