package com.petclinic.vet.service;

import com.petclinic.vet.dto.SpecialtyDto;
import com.petclinic.vet.dto.SpecialtyRequestDto;
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
import static org.mockito.Mockito.doNothing;
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

    private Specialty specialty;
    private SpecialtyDto specialtyDto;

    @BeforeEach
    void setUp() {
        specialty = new Specialty(1, "radiology");
        specialtyDto = new SpecialtyDto(1, "radiology");
    }

    @Test
    void findAll_shouldReturnAllSpecialties() {
        when(specialtyRepository.findAll()).thenReturn(List.of(specialty));
        when(specialtyMapper.toDtoList(any())).thenReturn(List.of(specialtyDto));

        List<SpecialtyDto> result = specialtyService.findAll();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).name()).isEqualTo("radiology");
    }

    @Test
    void findById_shouldReturnSpecialty() {
        when(specialtyRepository.findById(1)).thenReturn(Optional.of(specialty));
        when(specialtyMapper.toDto(specialty)).thenReturn(specialtyDto);

        SpecialtyDto result = specialtyService.findById(1);

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
        SpecialtyRequestDto request = new SpecialtyRequestDto("surgery");
        Specialty newSpecialty = new Specialty();
        newSpecialty.setName("surgery");

        when(specialtyMapper.toEntity(request)).thenReturn(newSpecialty);
        when(specialtyRepository.save(newSpecialty)).thenReturn(new Specialty(2, "surgery"));
        when(specialtyMapper.toDto(any(Specialty.class))).thenReturn(new SpecialtyDto(2, "surgery"));

        SpecialtyDto result = specialtyService.create(request);

        assertThat(result.name()).isEqualTo("surgery");
        verify(specialtyRepository).save(newSpecialty);
    }

    @Test
    void update_shouldUpdateAndReturnSpecialty() {
        SpecialtyRequestDto request = new SpecialtyRequestDto("updated-radiology");

        when(specialtyRepository.findById(1)).thenReturn(Optional.of(specialty));
        doNothing().when(specialtyMapper).updateEntity(request, specialty);
        when(specialtyRepository.save(specialty)).thenReturn(specialty);
        when(specialtyMapper.toDto(specialty)).thenReturn(new SpecialtyDto(1, "updated-radiology"));

        SpecialtyDto result = specialtyService.update(1, request);

        assertThat(result.name()).isEqualTo("updated-radiology");
    }

    @Test
    void update_shouldThrowWhenNotFound() {
        SpecialtyRequestDto request = new SpecialtyRequestDto("updated");
        when(specialtyRepository.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> specialtyService.update(99, request))
            .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void delete_shouldDeleteSpecialty() {
        when(specialtyRepository.findById(1)).thenReturn(Optional.of(specialty));

        specialtyService.delete(1);

        verify(specialtyRepository).delete(specialty);
    }

    @Test
    void delete_shouldThrowWhenNotFound() {
        when(specialtyRepository.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> specialtyService.delete(99))
            .isInstanceOf(ResourceNotFoundException.class);
    }
}
