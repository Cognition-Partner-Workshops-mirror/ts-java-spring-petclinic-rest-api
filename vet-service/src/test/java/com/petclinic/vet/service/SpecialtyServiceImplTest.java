package com.petclinic.vet.service;

import com.petclinic.vet.dto.SpecialtyRequestDto;
import com.petclinic.vet.dto.SpecialtyResponseDto;
import com.petclinic.vet.entity.Specialty;
import com.petclinic.vet.exception.ResourceNotFoundException;
import com.petclinic.vet.mapper.SpecialtyMapper;
import com.petclinic.vet.repository.SpecialtyRepository;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
        radiology = new Specialty("radiology");
        radiology.setId(1);
        radiologyDto = new SpecialtyResponseDto(1, "radiology");
    }

    @Test
    void findAll_returnsAllSpecialties() {
        Specialty surgery = new Specialty("surgery");
        surgery.setId(2);
        SpecialtyResponseDto surgeryDto = new SpecialtyResponseDto(2, "surgery");

        when(specialtyRepository.findAll()).thenReturn(List.of(radiology, surgery));
        when(specialtyMapper.toResponseDto(radiology)).thenReturn(radiologyDto);
        when(specialtyMapper.toResponseDto(surgery)).thenReturn(surgeryDto);

        List<SpecialtyResponseDto> result = specialtyService.findAll();

        assertThat(result).hasSize(2);
        assertThat(result.get(0).name()).isEqualTo("radiology");
    }

    @Test
    void findById_existingId_returnsSpecialty() {
        when(specialtyRepository.findById(1)).thenReturn(Optional.of(radiology));
        when(specialtyMapper.toResponseDto(radiology)).thenReturn(radiologyDto);

        SpecialtyResponseDto result = specialtyService.findById(1);

        assertThat(result.id()).isEqualTo(1);
        assertThat(result.name()).isEqualTo("radiology");
    }

    @Test
    void findById_nonExistingId_throwsException() {
        when(specialtyRepository.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> specialtyService.findById(99))
            .isInstanceOf(ResourceNotFoundException.class)
            .hasMessageContaining("Specialty")
            .hasMessageContaining("99");
    }

    @Test
    void create_validDto_returnsCreated() {
        SpecialtyRequestDto request = new SpecialtyRequestDto("dentistry");
        Specialty dentistry = new Specialty("dentistry");
        Specialty saved = new Specialty("dentistry");
        saved.setId(3);
        SpecialtyResponseDto expectedDto = new SpecialtyResponseDto(3, "dentistry");

        when(specialtyMapper.toEntity(request)).thenReturn(dentistry);
        when(specialtyRepository.save(dentistry)).thenReturn(saved);
        when(specialtyMapper.toResponseDto(saved)).thenReturn(expectedDto);

        SpecialtyResponseDto result = specialtyService.create(request);

        assertThat(result.id()).isEqualTo(3);
        assertThat(result.name()).isEqualTo("dentistry");
    }

    @Test
    void update_existingId_returnsUpdated() {
        SpecialtyRequestDto request = new SpecialtyRequestDto("updated-radiology");
        Specialty updated = new Specialty("updated-radiology");
        updated.setId(1);
        SpecialtyResponseDto expectedDto = new SpecialtyResponseDto(1, "updated-radiology");

        when(specialtyRepository.findById(1)).thenReturn(Optional.of(radiology));
        when(specialtyRepository.save(radiology)).thenReturn(updated);
        when(specialtyMapper.toResponseDto(updated)).thenReturn(expectedDto);

        SpecialtyResponseDto result = specialtyService.update(1, request);

        verify(specialtyMapper).updateEntity(eq(request), eq(radiology));
        assertThat(result.name()).isEqualTo("updated-radiology");
    }

    @Test
    void update_nonExistingId_throwsException() {
        SpecialtyRequestDto request = new SpecialtyRequestDto("updated");
        when(specialtyRepository.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> specialtyService.update(99, request))
            .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void delete_existingId_returnsDeleted() {
        when(specialtyRepository.findById(1)).thenReturn(Optional.of(radiology));
        when(specialtyMapper.toResponseDto(radiology)).thenReturn(radiologyDto);

        SpecialtyResponseDto result = specialtyService.delete(1);

        verify(specialtyRepository).delete(radiology);
        assertThat(result.id()).isEqualTo(1);
    }

    @Test
    void delete_nonExistingId_throwsException() {
        when(specialtyRepository.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> specialtyService.delete(99))
            .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void searchByName_returnsMatchingSpecialties() {
        when(specialtyRepository.findByNameContainingIgnoreCase("rad"))
            .thenReturn(List.of(radiology));
        when(specialtyMapper.toResponseDto(radiology)).thenReturn(radiologyDto);

        List<SpecialtyResponseDto> result = specialtyService.searchByName("rad");

        assertThat(result).hasSize(1);
        assertThat(result.get(0).name()).isEqualTo("radiology");
    }
}
