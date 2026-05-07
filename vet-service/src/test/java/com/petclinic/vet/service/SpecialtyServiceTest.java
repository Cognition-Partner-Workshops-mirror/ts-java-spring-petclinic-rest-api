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

import java.time.LocalDateTime;
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

    private Specialty specialty;
    private SpecialtyResponseDto responseDto;
    private SpecialtyRequestDto requestDto;

    @BeforeEach
    void setUp() {
        specialty = new Specialty("radiology");
        specialty.setId(1);
        specialty.setCreatedAt(LocalDateTime.now());
        specialty.setUpdatedAt(LocalDateTime.now());

        responseDto = new SpecialtyResponseDto(1, "radiology");
        requestDto = new SpecialtyRequestDto("radiology");
    }

    @Test
    void findAll_shouldReturnAllSpecialties() {
        when(specialtyRepository.findAll()).thenReturn(List.of(specialty));
        when(specialtyMapper.toResponseDtoList(List.of(specialty))).thenReturn(List.of(responseDto));

        List<SpecialtyResponseDto> result = specialtyService.findAll();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).name()).isEqualTo("radiology");
    }

    @Test
    void findById_shouldReturnSpecialty() {
        when(specialtyRepository.findById(1)).thenReturn(Optional.of(specialty));
        when(specialtyMapper.toResponseDto(specialty)).thenReturn(responseDto);

        SpecialtyResponseDto result = specialtyService.findById(1);

        assertThat(result.id()).isEqualTo(1);
        assertThat(result.name()).isEqualTo("radiology");
    }

    @Test
    void findById_shouldThrowWhenNotFound() {
        when(specialtyRepository.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> specialtyService.findById(99))
            .isInstanceOf(ResourceNotFoundException.class)
            .hasMessageContaining("Specialty not found with id: 99");
    }

    @Test
    void create_shouldSaveAndReturnSpecialty() {
        when(specialtyMapper.toEntity(requestDto)).thenReturn(specialty);
        when(specialtyRepository.save(specialty)).thenReturn(specialty);
        when(specialtyMapper.toResponseDto(specialty)).thenReturn(responseDto);

        SpecialtyResponseDto result = specialtyService.create(requestDto);

        assertThat(result.name()).isEqualTo("radiology");
        verify(specialtyRepository).save(specialty);
    }

    @Test
    void update_shouldUpdateAndReturnSpecialty() {
        when(specialtyRepository.findById(1)).thenReturn(Optional.of(specialty));
        when(specialtyRepository.save(specialty)).thenReturn(specialty);
        when(specialtyMapper.toResponseDto(specialty)).thenReturn(responseDto);

        SpecialtyResponseDto result = specialtyService.update(1, requestDto);

        assertThat(result.name()).isEqualTo("radiology");
        verify(specialtyMapper).updateEntity(requestDto, specialty);
    }

    @Test
    void update_shouldThrowWhenNotFound() {
        when(specialtyRepository.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> specialtyService.update(99, requestDto))
            .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void delete_shouldDeleteAndReturnSpecialty() {
        when(specialtyRepository.findById(1)).thenReturn(Optional.of(specialty));
        when(specialtyMapper.toResponseDto(specialty)).thenReturn(responseDto);

        SpecialtyResponseDto result = specialtyService.delete(1);

        assertThat(result.id()).isEqualTo(1);
        verify(specialtyRepository).delete(specialty);
    }

    @Test
    void delete_shouldThrowWhenNotFound() {
        when(specialtyRepository.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> specialtyService.delete(99))
            .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void searchByName_shouldReturnMatchingSpecialties() {
        when(specialtyRepository.findByNameContainingIgnoreCase("rad")).thenReturn(List.of(specialty));
        when(specialtyMapper.toResponseDtoList(List.of(specialty))).thenReturn(List.of(responseDto));

        List<SpecialtyResponseDto> result = specialtyService.searchByName("rad");

        assertThat(result).hasSize(1);
        assertThat(result.get(0).name()).isEqualTo("radiology");
    }
}
