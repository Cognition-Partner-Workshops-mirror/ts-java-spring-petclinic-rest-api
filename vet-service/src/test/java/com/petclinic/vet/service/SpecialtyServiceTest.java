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
    private SpecialtyServiceImpl specialtyService;

    private Specialty radiology;
    private SpecialtyResponseDto radiologyDto;

    @BeforeEach
    void setUp() {
        radiology = new Specialty();
        radiology.setId(1);
        radiology.setName("radiology");

        radiologyDto = new SpecialtyResponseDto(1, "radiology");
    }

    @Test
    void getAllSpecialties_shouldReturnAllSpecialties() {
        Specialty surgery = new Specialty();
        surgery.setId(2);
        surgery.setName("surgery");
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
    void getSpecialtyById_shouldReturnSpecialtyWhenFound() {
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
        SpecialtyRequestDto requestDto = new SpecialtyRequestDto("radiology");
        when(specialtyMapper.toEntity(requestDto)).thenReturn(radiology);
        when(specialtyRepository.save(radiology)).thenReturn(radiology);
        when(specialtyMapper.toResponseDto(radiology)).thenReturn(radiologyDto);

        SpecialtyResponseDto result = specialtyService.createSpecialty(requestDto);

        assertThat(result.getName()).isEqualTo("radiology");
        verify(specialtyRepository).save(radiology);
    }

    @Test
    void updateSpecialty_shouldUpdateAndReturnSpecialty() {
        SpecialtyRequestDto requestDto = new SpecialtyRequestDto("updated radiology");
        SpecialtyResponseDto updatedDto = new SpecialtyResponseDto(1, "updated radiology");

        when(specialtyRepository.findById(1)).thenReturn(Optional.of(radiology));
        when(specialtyRepository.save(radiology)).thenReturn(radiology);
        when(specialtyMapper.toResponseDto(radiology)).thenReturn(updatedDto);

        SpecialtyResponseDto result = specialtyService.updateSpecialty(1, requestDto);

        assertThat(result.getName()).isEqualTo("updated radiology");
        verify(specialtyMapper).updateEntityFromDto(requestDto, radiology);
    }

    @Test
    void updateSpecialty_shouldThrowWhenNotFound() {
        SpecialtyRequestDto requestDto = new SpecialtyRequestDto("test");
        when(specialtyRepository.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> specialtyService.updateSpecialty(99, requestDto))
            .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void deleteSpecialty_shouldDeleteAndReturnSpecialty() {
        when(specialtyRepository.findById(1)).thenReturn(Optional.of(radiology));
        when(specialtyMapper.toResponseDto(radiology)).thenReturn(radiologyDto);

        SpecialtyResponseDto result = specialtyService.deleteSpecialty(1);

        assertThat(result.getId()).isEqualTo(1);
        verify(specialtyRepository).delete(radiology);
    }

    @Test
    void deleteSpecialty_shouldThrowWhenNotFound() {
        when(specialtyRepository.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> specialtyService.deleteSpecialty(99))
            .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void searchByName_shouldReturnMatchingSpecialties() {
        when(specialtyRepository.findByNameContainingIgnoreCase("radio")).thenReturn(List.of(radiology));
        when(specialtyMapper.toResponseDto(radiology)).thenReturn(radiologyDto);

        List<SpecialtyResponseDto> result = specialtyService.searchByName("radio");

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("radiology");
    }

    @Test
    void searchByName_shouldReturnEmptyListForNoMatch() {
        when(specialtyRepository.findByNameContainingIgnoreCase("xyz")).thenReturn(List.of());

        List<SpecialtyResponseDto> result = specialtyService.searchByName("xyz");

        assertThat(result).isEmpty();
    }
}
