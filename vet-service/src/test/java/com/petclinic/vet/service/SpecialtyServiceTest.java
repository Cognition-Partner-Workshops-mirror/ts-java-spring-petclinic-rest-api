package com.petclinic.vet.service;

import com.petclinic.vet.dto.SpecialtyRequestDto;
import com.petclinic.vet.dto.SpecialtyResponseDto;
import com.petclinic.vet.entity.Specialty;
import com.petclinic.vet.exception.DuplicateResourceException;
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
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit tests for {@link SpecialtyServiceImpl} with mocked dependencies.
 */
@ExtendWith(MockitoExtension.class)
class SpecialtyServiceTest {

    @Mock
    private SpecialtyRepository specialtyRepository;

    @Mock
    private SpecialtyMapper specialtyMapper;

    @InjectMocks
    private SpecialtyServiceImpl specialtyService;

    private Specialty radiology;
    private SpecialtyResponseDto radiologyDto;

    @BeforeEach
    void setUp() {
        radiology = new Specialty("radiology");
        radiology.setId(1);

        radiologyDto = new SpecialtyResponseDto(1, "radiology");
    }

    @Test
    void getAllSpecialties_shouldReturnAllSpecialties() {
        Specialty surgery = new Specialty("surgery");
        surgery.setId(2);
        when(specialtyRepository.findAll()).thenReturn(List.of(radiology, surgery));
        when(specialtyMapper.toResponseDtoList(any())).thenReturn(
                List.of(radiologyDto, new SpecialtyResponseDto(2, "surgery")));

        List<SpecialtyResponseDto> result = specialtyService.getAllSpecialties();

        assertThat(result).hasSize(2);
        verify(specialtyRepository).findAll();
    }

    @Test
    void getSpecialtyById_shouldReturnSpecialty() {
        when(specialtyRepository.findById(1)).thenReturn(Optional.of(radiology));
        when(specialtyMapper.toResponseDto(radiology)).thenReturn(radiologyDto);

        SpecialtyResponseDto result = specialtyService.getSpecialtyById(1);

        assertThat(result.getId()).isEqualTo(1);
        assertThat(result.getName()).isEqualTo("radiology");
    }

    @Test
    void getSpecialtyById_shouldThrowWhenNotFound() {
        when(specialtyRepository.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> specialtyService.getSpecialtyById(99))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Specialty not found with id: 99");
    }

    @Test
    void createSpecialty_shouldSaveAndReturnSpecialty() {
        SpecialtyRequestDto request = new SpecialtyRequestDto("radiology");
        when(specialtyRepository.existsByNameIgnoreCase("radiology")).thenReturn(false);
        when(specialtyMapper.toEntity(request)).thenReturn(radiology);
        when(specialtyRepository.save(radiology)).thenReturn(radiology);
        when(specialtyMapper.toResponseDto(radiology)).thenReturn(radiologyDto);

        SpecialtyResponseDto result = specialtyService.createSpecialty(request);

        assertThat(result.getName()).isEqualTo("radiology");
        verify(specialtyRepository).save(radiology);
    }

    @Test
    void createSpecialty_shouldThrowWhenDuplicateName() {
        SpecialtyRequestDto request = new SpecialtyRequestDto("radiology");
        when(specialtyRepository.existsByNameIgnoreCase("radiology")).thenReturn(true);

        assertThatThrownBy(() -> specialtyService.createSpecialty(request))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessageContaining("Specialty already exists with name: radiology");
    }

    @Test
    void updateSpecialty_shouldUpdateAndReturnSpecialty() {
        SpecialtyRequestDto request = new SpecialtyRequestDto("updated-radiology");
        when(specialtyRepository.findById(1)).thenReturn(Optional.of(radiology));
        when(specialtyRepository.findByNameIgnoreCase("updated-radiology")).thenReturn(Optional.empty());
        when(specialtyRepository.save(radiology)).thenReturn(radiology);
        when(specialtyMapper.toResponseDto(radiology)).thenReturn(
                new SpecialtyResponseDto(1, "updated-radiology"));

        SpecialtyResponseDto result = specialtyService.updateSpecialty(1, request);

        assertThat(result.getName()).isEqualTo("updated-radiology");
        verify(specialtyMapper).updateEntityFromDto(request, radiology);
    }

    @Test
    void updateSpecialty_shouldThrowWhenNotFound() {
        SpecialtyRequestDto request = new SpecialtyRequestDto("surgery");
        when(specialtyRepository.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> specialtyService.updateSpecialty(99, request))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void updateSpecialty_shouldThrowWhenNameConflicts() {
        Specialty surgery = new Specialty("surgery");
        surgery.setId(2);
        SpecialtyRequestDto request = new SpecialtyRequestDto("surgery");

        when(specialtyRepository.findById(1)).thenReturn(Optional.of(radiology));
        when(specialtyRepository.findByNameIgnoreCase("surgery")).thenReturn(Optional.of(surgery));

        assertThatThrownBy(() -> specialtyService.updateSpecialty(1, request))
                .isInstanceOf(DuplicateResourceException.class);
    }

    @Test
    void updateSpecialty_shouldAllowSameNameForSameEntity() {
        SpecialtyRequestDto request = new SpecialtyRequestDto("radiology");
        when(specialtyRepository.findById(1)).thenReturn(Optional.of(radiology));
        when(specialtyRepository.findByNameIgnoreCase("radiology")).thenReturn(Optional.of(radiology));
        when(specialtyRepository.save(radiology)).thenReturn(radiology);
        when(specialtyMapper.toResponseDto(radiology)).thenReturn(radiologyDto);

        SpecialtyResponseDto result = specialtyService.updateSpecialty(1, request);

        assertThat(result.getId()).isEqualTo(1);
    }

    @Test
    void deleteSpecialty_shouldDeleteWhenExists() {
        when(specialtyRepository.existsById(1)).thenReturn(true);

        specialtyService.deleteSpecialty(1);

        verify(specialtyRepository).deleteById(1);
    }

    @Test
    void deleteSpecialty_shouldThrowWhenNotFound() {
        when(specialtyRepository.existsById(99)).thenReturn(false);

        assertThatThrownBy(() -> specialtyService.deleteSpecialty(99))
                .isInstanceOf(ResourceNotFoundException.class);
        verify(specialtyRepository, never()).deleteById(any());
    }

    @Test
    void searchByName_shouldReturnMatchingSpecialties() {
        when(specialtyRepository.findByNameContainingIgnoreCase("radio"))
                .thenReturn(List.of(radiology));
        when(specialtyMapper.toResponseDtoList(any())).thenReturn(List.of(radiologyDto));

        List<SpecialtyResponseDto> result = specialtyService.searchByName("radio");

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("radiology");
    }

    @Test
    void searchByName_shouldReturnEmptyWhenNoMatch() {
        when(specialtyRepository.findByNameContainingIgnoreCase("xyz"))
                .thenReturn(List.of());
        when(specialtyMapper.toResponseDtoList(any())).thenReturn(List.of());

        List<SpecialtyResponseDto> result = specialtyService.searchByName("xyz");

        assertThat(result).isEmpty();
    }
}
