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
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit tests for SpecialtyServiceImpl using Mockito mocks.
 * Tests business logic without database or Spring context.
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
        radiology = new Specialty();
        radiology.setId(1);
        radiology.setName("radiology");

        radiologyDto = new SpecialtyResponseDto(1, "radiology");
    }

    @Test
    void findAll_shouldReturnAllSpecialties() {
        when(specialtyRepository.findAll()).thenReturn(List.of(radiology));
        when(specialtyMapper.toResponseDtos(List.of(radiology))).thenReturn(List.of(radiologyDto));

        List<SpecialtyResponseDto> result = specialtyService.findAll();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("radiology");
    }

    @Test
    void findById_shouldReturnSpecialty() {
        when(specialtyRepository.findById(1)).thenReturn(Optional.of(radiology));
        when(specialtyMapper.toResponseDto(radiology)).thenReturn(radiologyDto);

        SpecialtyResponseDto result = specialtyService.findById(1);

        assertThat(result.getName()).isEqualTo("radiology");
    }

    @Test
    void findById_shouldThrowWhenNotFound() {
        when(specialtyRepository.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> specialtyService.findById(99))
            .isInstanceOf(ResourceNotFoundException.class)
            .hasMessageContaining("Specialty not found with id: 99");
    }

    @Test
    void create_shouldPersistAndReturnSpecialty() {
        SpecialtyRequestDto request = new SpecialtyRequestDto("surgery");
        Specialty surgery = new Specialty();
        surgery.setName("surgery");
        Specialty savedSurgery = new Specialty();
        savedSurgery.setId(2);
        savedSurgery.setName("surgery");
        SpecialtyResponseDto surgeryDto = new SpecialtyResponseDto(2, "surgery");

        when(specialtyMapper.toEntity(request)).thenReturn(surgery);
        when(specialtyRepository.save(surgery)).thenReturn(savedSurgery);
        when(specialtyMapper.toResponseDto(savedSurgery)).thenReturn(surgeryDto);

        SpecialtyResponseDto result = specialtyService.create(request);

        assertThat(result.getId()).isEqualTo(2);
        assertThat(result.getName()).isEqualTo("surgery");
        verify(specialtyRepository).save(surgery);
    }

    @Test
    void update_shouldModifyAndReturnSpecialty() {
        SpecialtyRequestDto request = new SpecialtyRequestDto("updated-radiology");
        SpecialtyResponseDto updatedDto = new SpecialtyResponseDto(1, "updated-radiology");

        when(specialtyRepository.findById(1)).thenReturn(Optional.of(radiology));
        when(specialtyRepository.save(radiology)).thenReturn(radiology);
        when(specialtyMapper.toResponseDto(radiology)).thenReturn(updatedDto);

        SpecialtyResponseDto result = specialtyService.update(1, request);

        assertThat(result.getName()).isEqualTo("updated-radiology");
        verify(specialtyMapper).updateEntityFromDto(eq(request), eq(radiology));
    }

    @Test
    void update_shouldThrowWhenNotFound() {
        SpecialtyRequestDto request = new SpecialtyRequestDto("test");
        when(specialtyRepository.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> specialtyService.update(99, request))
            .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void delete_shouldRemoveSpecialty() {
        when(specialtyRepository.findById(1)).thenReturn(Optional.of(radiology));

        specialtyService.delete(1);

        verify(specialtyRepository).delete(radiology);
    }

    @Test
    void delete_shouldThrowWhenNotFound() {
        when(specialtyRepository.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> specialtyService.delete(99))
            .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void searchByName_shouldDelegateToRepository() {
        when(specialtyRepository.findByNameContainingIgnoreCase("rad")).thenReturn(List.of(radiology));
        when(specialtyMapper.toResponseDtos(List.of(radiology))).thenReturn(List.of(radiologyDto));

        List<SpecialtyResponseDto> result = specialtyService.searchByName("rad");

        assertThat(result).hasSize(1);
    }
}
