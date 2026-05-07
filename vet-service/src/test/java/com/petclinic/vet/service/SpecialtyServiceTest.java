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

@ExtendWith(MockitoExtension.class)
class SpecialtyServiceTest {

    @Mock
    private SpecialtyRepository specialtyRepository;

    @Mock
    private SpecialtyMapper specialtyMapper;

    @InjectMocks
    private SpecialtyService specialtyService;

    private Specialty radiology;
    private SpecialtyResponseDto radiologyDto;

    @BeforeEach
    void setUp() {
        radiology = new Specialty(1, "radiology");
        radiologyDto = new SpecialtyResponseDto(1, "radiology");
    }

    @Test
    void listSpecialties_returnsAll() {
        List<Specialty> entities = List.of(radiology);
        when(specialtyRepository.findAll()).thenReturn(entities);
        when(specialtyMapper.toResponseDtoList(entities)).thenReturn(List.of(radiologyDto));

        List<SpecialtyResponseDto> result = specialtyService.listSpecialties();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).name()).isEqualTo("radiology");
    }

    @Test
    void getSpecialty_existingId_returnsDto() {
        when(specialtyRepository.findById(1)).thenReturn(Optional.of(radiology));
        when(specialtyMapper.toResponseDto(radiology)).thenReturn(radiologyDto);

        SpecialtyResponseDto result = specialtyService.getSpecialty(1);

        assertThat(result.id()).isEqualTo(1);
        assertThat(result.name()).isEqualTo("radiology");
    }

    @Test
    void getSpecialty_nonExistingId_throwsException() {
        when(specialtyRepository.findById(999)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> specialtyService.getSpecialty(999))
            .isInstanceOf(ResourceNotFoundException.class)
            .hasMessageContaining("Specialty")
            .hasMessageContaining("999");
    }

    @Test
    void addSpecialty_validDto_returnsSaved() {
        SpecialtyRequestDto request = new SpecialtyRequestDto("cardiology");
        Specialty newEntity = new Specialty(null, "cardiology");
        Specialty savedEntity = new Specialty(4, "cardiology");
        SpecialtyResponseDto savedDto = new SpecialtyResponseDto(4, "cardiology");

        when(specialtyMapper.toEntity(request)).thenReturn(newEntity);
        when(specialtyRepository.save(newEntity)).thenReturn(savedEntity);
        when(specialtyMapper.toResponseDto(savedEntity)).thenReturn(savedDto);

        SpecialtyResponseDto result = specialtyService.addSpecialty(request);

        assertThat(result.id()).isEqualTo(4);
        assertThat(result.name()).isEqualTo("cardiology");
    }

    @Test
    void updateSpecialty_existingId_returnsUpdated() {
        SpecialtyRequestDto request = new SpecialtyRequestDto("updated-radiology");
        Specialty updatedEntity = new Specialty(1, "updated-radiology");
        SpecialtyResponseDto updatedDto = new SpecialtyResponseDto(1, "updated-radiology");

        when(specialtyRepository.findById(1)).thenReturn(Optional.of(radiology));
        when(specialtyRepository.save(radiology)).thenReturn(updatedEntity);
        when(specialtyMapper.toResponseDto(updatedEntity)).thenReturn(updatedDto);

        SpecialtyResponseDto result = specialtyService.updateSpecialty(1, request);

        assertThat(result.name()).isEqualTo("updated-radiology");
        verify(specialtyMapper).updateEntity(request, radiology);
    }

    @Test
    void updateSpecialty_nonExistingId_throwsException() {
        SpecialtyRequestDto request = new SpecialtyRequestDto("updated");
        when(specialtyRepository.findById(999)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> specialtyService.updateSpecialty(999, request))
            .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void deleteSpecialty_existingId_returnsDeleted() {
        when(specialtyRepository.findById(1)).thenReturn(Optional.of(radiology));
        when(specialtyMapper.toResponseDto(radiology)).thenReturn(radiologyDto);

        SpecialtyResponseDto result = specialtyService.deleteSpecialty(1);

        assertThat(result.id()).isEqualTo(1);
        verify(specialtyRepository).delete(radiology);
    }

    @Test
    void deleteSpecialty_nonExistingId_throwsException() {
        when(specialtyRepository.findById(999)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> specialtyService.deleteSpecialty(999))
            .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void searchByName_returnsMatches() {
        List<Specialty> matches = List.of(radiology);
        when(specialtyRepository.findByNameContainingIgnoreCase("radio")).thenReturn(matches);
        when(specialtyMapper.toResponseDtoList(matches)).thenReturn(List.of(radiologyDto));

        List<SpecialtyResponseDto> result = specialtyService.searchByName("radio");

        assertThat(result).hasSize(1);
    }
}
