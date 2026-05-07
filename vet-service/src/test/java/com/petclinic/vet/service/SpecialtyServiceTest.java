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
        radiology = new Specialty(1, "radiology");
        radiologyDto = new SpecialtyResponseDto(1, "radiology");
    }

    @Test
    void getAllSpecialties_returnsAll() {
        List<Specialty> entities = List.of(radiology);
        when(specialtyRepository.findAll()).thenReturn(entities);
        when(specialtyMapper.toResponseDtoList(entities)).thenReturn(List.of(radiologyDto));

        List<SpecialtyResponseDto> result = specialtyService.getAllSpecialties();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("radiology");
    }

    @Test
    void getSpecialtyById_found() {
        when(specialtyRepository.findById(1)).thenReturn(Optional.of(radiology));
        when(specialtyMapper.toResponseDto(radiology)).thenReturn(radiologyDto);

        SpecialtyResponseDto result = specialtyService.getSpecialtyById(1);

        assertThat(result.getId()).isEqualTo(1);
        assertThat(result.getName()).isEqualTo("radiology");
    }

    @Test
    void getSpecialtyById_notFound() {
        when(specialtyRepository.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> specialtyService.getSpecialtyById(99))
            .isInstanceOf(ResourceNotFoundException.class)
            .hasMessageContaining("Specialty not found with id: 99");
    }

    @Test
    void createSpecialty_success() {
        SpecialtyRequestDto request = new SpecialtyRequestDto("surgery");
        Specialty entity = new Specialty(null, "surgery");
        Specialty saved = new Specialty(2, "surgery");
        SpecialtyResponseDto responseDto = new SpecialtyResponseDto(2, "surgery");

        when(specialtyMapper.toEntity(request)).thenReturn(entity);
        when(specialtyRepository.save(entity)).thenReturn(saved);
        when(specialtyMapper.toResponseDto(saved)).thenReturn(responseDto);

        SpecialtyResponseDto result = specialtyService.createSpecialty(request);

        assertThat(result.getId()).isEqualTo(2);
        assertThat(result.getName()).isEqualTo("surgery");
    }

    @Test
    void updateSpecialty_success() {
        SpecialtyRequestDto request = new SpecialtyRequestDto("dentistry");
        Specialty updated = new Specialty(1, "dentistry");
        SpecialtyResponseDto responseDto = new SpecialtyResponseDto(1, "dentistry");

        when(specialtyRepository.findById(1)).thenReturn(Optional.of(radiology));
        when(specialtyRepository.save(radiology)).thenReturn(updated);
        when(specialtyMapper.toResponseDto(updated)).thenReturn(responseDto);

        SpecialtyResponseDto result = specialtyService.updateSpecialty(1, request);

        assertThat(result.getName()).isEqualTo("dentistry");
        verify(specialtyMapper).updateEntityFromDto(request, radiology);
    }

    @Test
    void updateSpecialty_notFound() {
        SpecialtyRequestDto request = new SpecialtyRequestDto("dentistry");
        when(specialtyRepository.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> specialtyService.updateSpecialty(99, request))
            .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void deleteSpecialty_success() {
        when(specialtyRepository.findById(1)).thenReturn(Optional.of(radiology));
        when(specialtyMapper.toResponseDto(radiology)).thenReturn(radiologyDto);

        SpecialtyResponseDto result = specialtyService.deleteSpecialty(1);

        assertThat(result.getId()).isEqualTo(1);
        verify(specialtyRepository).delete(radiology);
    }

    @Test
    void deleteSpecialty_notFound() {
        when(specialtyRepository.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> specialtyService.deleteSpecialty(99))
            .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void searchByName_returnsMatches() {
        List<Specialty> entities = List.of(radiology);
        when(specialtyRepository.findByNameContainingIgnoreCase("rad")).thenReturn(entities);
        when(specialtyMapper.toResponseDtoList(entities)).thenReturn(List.of(radiologyDto));

        List<SpecialtyResponseDto> result = specialtyService.searchByName("rad");

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("radiology");
    }
}
