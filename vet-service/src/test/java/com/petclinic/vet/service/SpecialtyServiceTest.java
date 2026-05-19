package com.petclinic.vet.service;

import com.petclinic.vet.dto.SpecialtyRequestDto;
import com.petclinic.vet.dto.SpecialtyResponseDto;
import com.petclinic.vet.entity.Specialty;
import com.petclinic.vet.exception.DuplicateResourceException;
import com.petclinic.vet.exception.ResourceNotFoundException;
import com.petclinic.vet.mapper.SpecialtyMapper;
import com.petclinic.vet.repository.SpecialtyRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
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
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit tests for SpecialtyServiceImpl using Mockito.
 * Covers all CRUD operations and edge cases.
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
        radiology = new Specialty(1, "radiology");
        radiologyDto = new SpecialtyResponseDto(1, "radiology");
    }

    @Test
    @DisplayName("getAllSpecialties returns all specialties")
    void getAllSpecialties_returnsAll() {
        Specialty surgery = new Specialty(2, "surgery");
        SpecialtyResponseDto surgeryDto = new SpecialtyResponseDto(2, "surgery");

        when(specialtyRepository.findAll()).thenReturn(List.of(radiology, surgery));
        when(specialtyMapper.toResponseDto(radiology)).thenReturn(radiologyDto);
        when(specialtyMapper.toResponseDto(surgery)).thenReturn(surgeryDto);

        List<SpecialtyResponseDto> result = specialtyService.getAllSpecialties();

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getName()).isEqualTo("radiology");
        assertThat(result.get(1).getName()).isEqualTo("surgery");
    }

    @Test
    @DisplayName("getAllSpecialties returns empty list when none exist")
    void getAllSpecialties_emptyList() {
        when(specialtyRepository.findAll()).thenReturn(List.of());

        List<SpecialtyResponseDto> result = specialtyService.getAllSpecialties();

        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("getSpecialtyById returns specialty when found")
    void getSpecialtyById_found() {
        when(specialtyRepository.findById(1)).thenReturn(Optional.of(radiology));
        when(specialtyMapper.toResponseDto(radiology)).thenReturn(radiologyDto);

        SpecialtyResponseDto result = specialtyService.getSpecialtyById(1);

        assertThat(result.getId()).isEqualTo(1);
        assertThat(result.getName()).isEqualTo("radiology");
    }

    @Test
    @DisplayName("getSpecialtyById throws ResourceNotFoundException when not found")
    void getSpecialtyById_notFound() {
        when(specialtyRepository.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> specialtyService.getSpecialtyById(99))
            .isInstanceOf(ResourceNotFoundException.class)
            .hasMessageContaining("Specialty not found with id: 99");
    }

    @Test
    @DisplayName("createSpecialty creates and returns a new specialty")
    void createSpecialty_success() {
        SpecialtyRequestDto request = new SpecialtyRequestDto("dentistry");
        Specialty newEntity = new Specialty(null, "dentistry");
        Specialty savedEntity = new Specialty(3, "dentistry");
        SpecialtyResponseDto expectedDto = new SpecialtyResponseDto(3, "dentistry");

        when(specialtyRepository.existsByNameIgnoreCase("dentistry")).thenReturn(false);
        when(specialtyMapper.toEntity(request)).thenReturn(newEntity);
        when(specialtyRepository.save(newEntity)).thenReturn(savedEntity);
        when(specialtyMapper.toResponseDto(savedEntity)).thenReturn(expectedDto);

        SpecialtyResponseDto result = specialtyService.createSpecialty(request);

        assertThat(result.getId()).isEqualTo(3);
        assertThat(result.getName()).isEqualTo("dentistry");
    }

    @Test
    @DisplayName("createSpecialty throws DuplicateResourceException for duplicate name")
    void createSpecialty_duplicate() {
        SpecialtyRequestDto request = new SpecialtyRequestDto("radiology");
        when(specialtyRepository.existsByNameIgnoreCase("radiology")).thenReturn(true);

        assertThatThrownBy(() -> specialtyService.createSpecialty(request))
            .isInstanceOf(DuplicateResourceException.class)
            .hasMessageContaining("already exists");

        verify(specialtyRepository, never()).save(any());
    }

    @Test
    @DisplayName("updateSpecialty updates and returns the specialty")
    void updateSpecialty_success() {
        SpecialtyRequestDto request = new SpecialtyRequestDto("updated-radiology");
        SpecialtyResponseDto expectedDto = new SpecialtyResponseDto(1, "updated-radiology");

        when(specialtyRepository.findById(1)).thenReturn(Optional.of(radiology));
        when(specialtyRepository.existsByNameIgnoreCase("updated-radiology")).thenReturn(false);
        when(specialtyRepository.save(radiology)).thenReturn(radiology);
        when(specialtyMapper.toResponseDto(radiology)).thenReturn(expectedDto);

        SpecialtyResponseDto result = specialtyService.updateSpecialty(1, request);

        assertThat(result.getName()).isEqualTo("updated-radiology");
        verify(specialtyMapper).updateEntityFromDto(request, radiology);
    }

    @Test
    @DisplayName("updateSpecialty allows updating with the same name (no false duplicate)")
    void updateSpecialty_sameName() {
        SpecialtyRequestDto request = new SpecialtyRequestDto("radiology");
        SpecialtyResponseDto expectedDto = new SpecialtyResponseDto(1, "radiology");

        when(specialtyRepository.findById(1)).thenReturn(Optional.of(radiology));
        when(specialtyRepository.save(radiology)).thenReturn(radiology);
        when(specialtyMapper.toResponseDto(radiology)).thenReturn(expectedDto);

        SpecialtyResponseDto result = specialtyService.updateSpecialty(1, request);

        assertThat(result.getName()).isEqualTo("radiology");
    }

    @Test
    @DisplayName("updateSpecialty throws DuplicateResourceException when renaming to existing name")
    void updateSpecialty_duplicateName() {
        SpecialtyRequestDto request = new SpecialtyRequestDto("surgery");

        when(specialtyRepository.findById(1)).thenReturn(Optional.of(radiology));
        when(specialtyRepository.existsByNameIgnoreCase("surgery")).thenReturn(true);

        assertThatThrownBy(() -> specialtyService.updateSpecialty(1, request))
            .isInstanceOf(DuplicateResourceException.class)
            .hasMessageContaining("already exists");

        verify(specialtyRepository, never()).save(any());
    }

    @Test
    @DisplayName("updateSpecialty throws ResourceNotFoundException when not found")
    void updateSpecialty_notFound() {
        SpecialtyRequestDto request = new SpecialtyRequestDto("test");
        when(specialtyRepository.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> specialtyService.updateSpecialty(99, request))
            .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    @DisplayName("deleteSpecialty deletes when specialty exists")
    void deleteSpecialty_success() {
        when(specialtyRepository.existsById(1)).thenReturn(true);

        specialtyService.deleteSpecialty(1);

        verify(specialtyRepository).deleteById(1);
    }

    @Test
    @DisplayName("deleteSpecialty throws ResourceNotFoundException when not found")
    void deleteSpecialty_notFound() {
        when(specialtyRepository.existsById(99)).thenReturn(false);

        assertThatThrownBy(() -> specialtyService.deleteSpecialty(99))
            .isInstanceOf(ResourceNotFoundException.class);
    }
}
