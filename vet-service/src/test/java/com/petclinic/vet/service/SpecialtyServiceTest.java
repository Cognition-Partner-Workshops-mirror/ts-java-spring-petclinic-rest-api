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
    void findAll_returnsAllSpecialties() {
        Specialty surgery = new Specialty(2, "surgery");
        SpecialtyResponseDto surgeryDto = new SpecialtyResponseDto(2, "surgery");

        when(specialtyRepository.findAll()).thenReturn(List.of(radiology, surgery));
        when(specialtyMapper.toResponseDto(radiology)).thenReturn(radiologyDto);
        when(specialtyMapper.toResponseDto(surgery)).thenReturn(surgeryDto);

        List<SpecialtyResponseDto> result = specialtyService.findAll();

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getName()).isEqualTo("radiology");
        assertThat(result.get(1).getName()).isEqualTo("surgery");
    }

    @Test
    void findById_existingId_returnsSpecialty() {
        when(specialtyRepository.findById(1)).thenReturn(Optional.of(radiology));
        when(specialtyMapper.toResponseDto(radiology)).thenReturn(radiologyDto);

        SpecialtyResponseDto result = specialtyService.findById(1);

        assertThat(result.getId()).isEqualTo(1);
        assertThat(result.getName()).isEqualTo("radiology");
    }

    @Test
    void findById_nonExistingId_throwsResourceNotFoundException() {
        when(specialtyRepository.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> specialtyService.findById(99))
            .isInstanceOf(ResourceNotFoundException.class)
            .hasMessageContaining("Specialty not found with id: 99");
    }

    @Test
    void create_validDto_returnsCreatedSpecialty() {
        SpecialtyRequestDto request = new SpecialtyRequestDto("dentistry");
        Specialty entity = new Specialty(null, "dentistry");
        Specialty saved = new Specialty(3, "dentistry");
        SpecialtyResponseDto response = new SpecialtyResponseDto(3, "dentistry");

        when(specialtyMapper.toEntity(request)).thenReturn(entity);
        when(specialtyRepository.save(entity)).thenReturn(saved);
        when(specialtyMapper.toResponseDto(saved)).thenReturn(response);

        SpecialtyResponseDto result = specialtyService.create(request);

        assertThat(result.getId()).isEqualTo(3);
        assertThat(result.getName()).isEqualTo("dentistry");
    }

    @Test
    void update_existingId_returnsUpdatedSpecialty() {
        SpecialtyRequestDto request = new SpecialtyRequestDto("updated-radiology");
        SpecialtyResponseDto response = new SpecialtyResponseDto(1, "updated-radiology");

        when(specialtyRepository.findById(1)).thenReturn(Optional.of(radiology));
        when(specialtyRepository.save(radiology)).thenReturn(radiology);
        when(specialtyMapper.toResponseDto(radiology)).thenReturn(response);

        SpecialtyResponseDto result = specialtyService.update(1, request);

        assertThat(result.getName()).isEqualTo("updated-radiology");
        verify(specialtyMapper).updateEntity(request, radiology);
    }

    @Test
    void update_nonExistingId_throwsResourceNotFoundException() {
        SpecialtyRequestDto request = new SpecialtyRequestDto("updated");

        when(specialtyRepository.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> specialtyService.update(99, request))
            .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void delete_existingId_returnsDeletedSpecialty() {
        when(specialtyRepository.findById(1)).thenReturn(Optional.of(radiology));
        when(specialtyMapper.toResponseDto(radiology)).thenReturn(radiologyDto);

        SpecialtyResponseDto result = specialtyService.delete(1);

        assertThat(result.getId()).isEqualTo(1);
        verify(specialtyRepository).delete(radiology);
    }

    @Test
    void delete_nonExistingId_throwsResourceNotFoundException() {
        when(specialtyRepository.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> specialtyService.delete(99))
            .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void searchByName_returnsMatchingSpecialties() {
        when(specialtyRepository.findByNameContainingIgnoreCase("rad")).thenReturn(List.of(radiology));
        when(specialtyMapper.toResponseDto(radiology)).thenReturn(radiologyDto);

        List<SpecialtyResponseDto> result = specialtyService.searchByName("rad");

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("radiology");
    }
}
