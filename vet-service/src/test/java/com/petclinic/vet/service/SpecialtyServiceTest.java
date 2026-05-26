package com.petclinic.vet.service;

import com.petclinic.vet.dto.SpecialtyRequestDto;
import com.petclinic.vet.dto.SpecialtyResponseDto;
import com.petclinic.vet.entity.Specialty;
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
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit tests for {@link SpecialtyServiceImpl}.
 * Uses Mockito to isolate the service layer from the repository and mapper.
 */
@ExtendWith(MockitoExtension.class)
class SpecialtyServiceTest {

    @Mock
    private SpecialtyRepository specialtyRepository;

    @Mock
    private SpecialtyMapper specialtyMapper;

    @InjectMocks
    private SpecialtyServiceImpl specialtyService;

    private Specialty radiologyEntity;
    private SpecialtyResponseDto radiologyDto;

    @BeforeEach
    void setUp() {
        // Prepare a sample entity and DTO used across multiple tests
        radiologyEntity = new Specialty();
        radiologyEntity.setId(1);
        radiologyEntity.setName("radiology");

        radiologyDto = new SpecialtyResponseDto(1, "radiology");
    }

    @Test
    @DisplayName("getAllSpecialties returns all specialties from repository")
    void getAllSpecialties_returnsList() {
        when(specialtyRepository.findAll()).thenReturn(List.of(radiologyEntity));
        when(specialtyMapper.toResponseDto(radiologyEntity)).thenReturn(radiologyDto);

        List<SpecialtyResponseDto> result = specialtyService.getAllSpecialties();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("radiology");
    }

    @Test
    @DisplayName("getAllSpecialties returns empty list when no specialties exist")
    void getAllSpecialties_returnsEmptyList() {
        when(specialtyRepository.findAll()).thenReturn(List.of());

        List<SpecialtyResponseDto> result = specialtyService.getAllSpecialties();

        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("getSpecialtyById returns the specialty when found")
    void getSpecialtyById_found() {
        when(specialtyRepository.findById(1)).thenReturn(Optional.of(radiologyEntity));
        when(specialtyMapper.toResponseDto(radiologyEntity)).thenReturn(radiologyDto);

        SpecialtyResponseDto result = specialtyService.getSpecialtyById(1);

        assertThat(result.getId()).isEqualTo(1);
        assertThat(result.getName()).isEqualTo("radiology");
    }

    @Test
    @DisplayName("getSpecialtyById throws ResourceNotFoundException when not found")
    void getSpecialtyById_notFound() {
        when(specialtyRepository.findById(999)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> specialtyService.getSpecialtyById(999))
            .isInstanceOf(ResourceNotFoundException.class)
            .hasMessageContaining("999");
    }

    @Test
    @DisplayName("createSpecialty saves and returns the new specialty")
    void createSpecialty_savesAndReturns() {
        SpecialtyRequestDto request = new SpecialtyRequestDto("surgery");
        Specialty newEntity = new Specialty();
        newEntity.setName("surgery");

        Specialty savedEntity = new Specialty();
        savedEntity.setId(2);
        savedEntity.setName("surgery");

        SpecialtyResponseDto expectedDto = new SpecialtyResponseDto(2, "surgery");

        when(specialtyMapper.toEntity(request)).thenReturn(newEntity);
        when(specialtyRepository.save(newEntity)).thenReturn(savedEntity);
        when(specialtyMapper.toResponseDto(savedEntity)).thenReturn(expectedDto);

        SpecialtyResponseDto result = specialtyService.createSpecialty(request);

        assertThat(result.getId()).isEqualTo(2);
        assertThat(result.getName()).isEqualTo("surgery");
        verify(specialtyRepository).save(newEntity);
    }

    @Test
    @DisplayName("updateSpecialty updates and returns the modified specialty")
    void updateSpecialty_updatesAndReturns() {
        SpecialtyRequestDto request = new SpecialtyRequestDto("updated-radiology");
        SpecialtyResponseDto updatedDto = new SpecialtyResponseDto(1, "updated-radiology");

        when(specialtyRepository.findById(1)).thenReturn(Optional.of(radiologyEntity));
        doNothing().when(specialtyMapper).updateEntityFromDto(request, radiologyEntity);
        when(specialtyRepository.save(radiologyEntity)).thenReturn(radiologyEntity);
        when(specialtyMapper.toResponseDto(radiologyEntity)).thenReturn(updatedDto);

        SpecialtyResponseDto result = specialtyService.updateSpecialty(1, request);

        assertThat(result.getName()).isEqualTo("updated-radiology");
        verify(specialtyMapper).updateEntityFromDto(request, radiologyEntity);
    }

    @Test
    @DisplayName("updateSpecialty throws ResourceNotFoundException when ID not found")
    void updateSpecialty_notFound() {
        when(specialtyRepository.findById(999)).thenReturn(Optional.empty());

        assertThatThrownBy(() ->
            specialtyService.updateSpecialty(999, new SpecialtyRequestDto("x")))
            .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    @DisplayName("deleteSpecialty removes the entity and returns it")
    void deleteSpecialty_deletesAndReturns() {
        when(specialtyRepository.findById(1)).thenReturn(Optional.of(radiologyEntity));
        when(specialtyMapper.toResponseDto(radiologyEntity)).thenReturn(radiologyDto);

        SpecialtyResponseDto result = specialtyService.deleteSpecialty(1);

        assertThat(result.getId()).isEqualTo(1);
        verify(specialtyRepository).delete(radiologyEntity);
    }

    @Test
    @DisplayName("deleteSpecialty throws ResourceNotFoundException when ID not found")
    void deleteSpecialty_notFound() {
        when(specialtyRepository.findById(999)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> specialtyService.deleteSpecialty(999))
            .isInstanceOf(ResourceNotFoundException.class);
    }
}
