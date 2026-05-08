package com.petclinic.vet.service;

import com.petclinic.vet.dto.SpecialtyRequestDto;
import com.petclinic.vet.dto.SpecialtyResponseDto;
import com.petclinic.vet.entity.Specialty;
import com.petclinic.vet.exception.ResourceNotFoundException;
import com.petclinic.vet.mapper.SpecialtyMapper;
import com.petclinic.vet.repository.SpecialtyRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for SpecialtyServiceImpl.
 * Mocks the repository and mapper to test business logic in isolation.
 */
@ExtendWith(MockitoExtension.class)
class SpecialtyServiceImplTest {

    @Mock
    private SpecialtyRepository specialtyRepository;

    @Mock
    private SpecialtyMapper specialtyMapper;

    @InjectMocks
    private SpecialtyServiceImpl specialtyService;

    private Specialty specialty;
    private SpecialtyResponseDto responseDto;
    private SpecialtyRequestDto requestDto;

    @BeforeEach
    void setUp() {
        specialty = new Specialty(1, "radiology");
        responseDto = new SpecialtyResponseDto(1, "radiology");
        requestDto = new SpecialtyRequestDto("radiology");
    }

    @Test
    void getAllSpecialties_returnsListOfSpecialties() {
        when(specialtyRepository.findAll()).thenReturn(List.of(specialty));
        when(specialtyMapper.toResponseDto(specialty)).thenReturn(responseDto);

        List<SpecialtyResponseDto> result = specialtyService.getAllSpecialties();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("radiology");
        verify(specialtyRepository).findAll();
    }

    @Test
    void getAllSpecialties_returnsEmptyListWhenNoneExist() {
        when(specialtyRepository.findAll()).thenReturn(List.of());

        List<SpecialtyResponseDto> result = specialtyService.getAllSpecialties();

        assertThat(result).isEmpty();
    }

    @Test
    void getSpecialtyById_returnsSpecialtyWhenFound() {
        when(specialtyRepository.findById(1)).thenReturn(Optional.of(specialty));
        when(specialtyMapper.toResponseDto(specialty)).thenReturn(responseDto);

        SpecialtyResponseDto result = specialtyService.getSpecialtyById(1);

        assertThat(result.getId()).isEqualTo(1);
        assertThat(result.getName()).isEqualTo("radiology");
    }

    @Test
    void getSpecialtyById_throwsNotFoundWhenMissing() {
        when(specialtyRepository.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> specialtyService.getSpecialtyById(99))
            .isInstanceOf(ResourceNotFoundException.class)
            .hasMessageContaining("Specialty not found with id: 99");
    }

    @Test
    void createSpecialty_savesAndReturnsSpecialty() {
        when(specialtyMapper.toEntity(requestDto)).thenReturn(specialty);
        when(specialtyRepository.save(specialty)).thenReturn(specialty);
        when(specialtyMapper.toResponseDto(specialty)).thenReturn(responseDto);

        SpecialtyResponseDto result = specialtyService.createSpecialty(requestDto);

        assertThat(result.getName()).isEqualTo("radiology");
        verify(specialtyRepository).save(specialty);
    }

    @Test
    void updateSpecialty_updatesExistingSpecialty() {
        SpecialtyRequestDto updateRequest = new SpecialtyRequestDto("surgery");
        SpecialtyResponseDto updatedResponse = new SpecialtyResponseDto(1, "surgery");

        when(specialtyRepository.findById(1)).thenReturn(Optional.of(specialty));
        doNothing().when(specialtyMapper).updateEntityFromDto(updateRequest, specialty);
        when(specialtyRepository.save(specialty)).thenReturn(specialty);
        when(specialtyMapper.toResponseDto(specialty)).thenReturn(updatedResponse);

        SpecialtyResponseDto result = specialtyService.updateSpecialty(1, updateRequest);

        assertThat(result.getName()).isEqualTo("surgery");
        verify(specialtyMapper).updateEntityFromDto(updateRequest, specialty);
    }

    @Test
    void updateSpecialty_throwsNotFoundForMissingId() {
        when(specialtyRepository.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> specialtyService.updateSpecialty(99, requestDto))
            .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void deleteSpecialty_deletesAndReturnsSpecialty() {
        when(specialtyRepository.findById(1)).thenReturn(Optional.of(specialty));
        when(specialtyMapper.toResponseDto(specialty)).thenReturn(responseDto);

        SpecialtyResponseDto result = specialtyService.deleteSpecialty(1);

        assertThat(result.getId()).isEqualTo(1);
        verify(specialtyRepository).delete(specialty);
    }

    @Test
    void deleteSpecialty_throwsNotFoundForMissingId() {
        when(specialtyRepository.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> specialtyService.deleteSpecialty(99))
            .isInstanceOf(ResourceNotFoundException.class);
    }
}
