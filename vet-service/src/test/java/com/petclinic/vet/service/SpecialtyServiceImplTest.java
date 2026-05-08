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
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit tests for SpecialtyServiceImpl business logic.
 * Uses Mockito to isolate from repository and mapper dependencies.
 */
@ExtendWith(MockitoExtension.class)
class SpecialtyServiceImplTest {

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
    void getAllSpecialties_returnsList() {
        List<Specialty> entities = List.of(radiology, new Specialty(2, "surgery"));
        List<SpecialtyResponseDto> dtos = List.of(radiologyDto, new SpecialtyResponseDto(2, "surgery"));
        when(specialtyRepository.findAll()).thenReturn(entities);
        when(specialtyMapper.toResponseDtos(entities)).thenReturn(dtos);

        List<SpecialtyResponseDto> result = specialtyService.getAllSpecialties();

        assertThat(result).hasSize(2);
        verify(specialtyRepository).findAll();
    }

    @Test
    void getSpecialtyById_found_returnsDto() {
        when(specialtyRepository.findById(1)).thenReturn(Optional.of(radiology));
        when(specialtyMapper.toResponseDto(radiology)).thenReturn(radiologyDto);

        SpecialtyResponseDto result = specialtyService.getSpecialtyById(1);

        assertThat(result.getName()).isEqualTo("radiology");
    }

    @Test
    void getSpecialtyById_notFound_throwsException() {
        when(specialtyRepository.findById(999)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> specialtyService.getSpecialtyById(999))
            .isInstanceOf(ResourceNotFoundException.class)
            .hasMessageContaining("Specialty not found with id: 999");
    }

    @Test
    void createSpecialty_savesAndReturnsDto() {
        SpecialtyRequestDto request = new SpecialtyRequestDto("orthopedics");
        Specialty newEntity = new Specialty();
        newEntity.setName("orthopedics");
        Specialty saved = new Specialty(4, "orthopedics");
        SpecialtyResponseDto savedDto = new SpecialtyResponseDto(4, "orthopedics");

        when(specialtyMapper.toEntity(request)).thenReturn(newEntity);
        when(specialtyRepository.save(newEntity)).thenReturn(saved);
        when(specialtyMapper.toResponseDto(saved)).thenReturn(savedDto);

        SpecialtyResponseDto result = specialtyService.createSpecialty(request);

        assertThat(result.getId()).isEqualTo(4);
        assertThat(result.getName()).isEqualTo("orthopedics");
    }

    @Test
    void updateSpecialty_found_updatesAndReturnsDto() {
        SpecialtyRequestDto request = new SpecialtyRequestDto("updated-radiology");
        Specialty updated = new Specialty(1, "updated-radiology");
        SpecialtyResponseDto updatedDto = new SpecialtyResponseDto(1, "updated-radiology");

        when(specialtyRepository.findById(1)).thenReturn(Optional.of(radiology));
        when(specialtyRepository.save(radiology)).thenReturn(updated);
        when(specialtyMapper.toResponseDto(updated)).thenReturn(updatedDto);

        SpecialtyResponseDto result = specialtyService.updateSpecialty(1, request);

        assertThat(result.getName()).isEqualTo("updated-radiology");
        verify(specialtyMapper).updateEntity(request, radiology);
    }

    @Test
    void updateSpecialty_notFound_throwsException() {
        SpecialtyRequestDto request = new SpecialtyRequestDto("updated");
        when(specialtyRepository.findById(999)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> specialtyService.updateSpecialty(999, request))
            .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void deleteSpecialty_found_deletesAndReturnsDto() {
        when(specialtyRepository.findById(1)).thenReturn(Optional.of(radiology));
        when(specialtyMapper.toResponseDto(radiology)).thenReturn(radiologyDto);

        SpecialtyResponseDto result = specialtyService.deleteSpecialty(1);

        assertThat(result.getName()).isEqualTo("radiology");
        verify(specialtyRepository).delete(radiology);
    }

    @Test
    void deleteSpecialty_notFound_throwsException() {
        when(specialtyRepository.findById(999)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> specialtyService.deleteSpecialty(999))
            .isInstanceOf(ResourceNotFoundException.class);
    }
}
