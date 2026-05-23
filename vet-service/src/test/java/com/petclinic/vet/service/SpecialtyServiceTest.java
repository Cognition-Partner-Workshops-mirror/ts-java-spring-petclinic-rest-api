package com.petclinic.vet.service;

import com.petclinic.vet.dto.SpecialtyDto;
import com.petclinic.vet.entity.Specialty;
import com.petclinic.vet.exception.ResourceNotFoundException;
import com.petclinic.vet.repository.SpecialtyRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit tests for SpecialtyServiceImpl using Mockito.
 */
@ExtendWith(MockitoExtension.class)
class SpecialtyServiceTest {

    @Mock
    private SpecialtyRepository specialtyRepository;

    @InjectMocks
    private SpecialtyServiceImpl specialtyService;

    private Specialty radiology;

    @BeforeEach
    void setUp() {
        radiology = new Specialty(1, "radiology");
    }

    @Test
    void findAll_returnsAllSpecialties() {
        Specialty surgery = new Specialty(2, "surgery");
        when(specialtyRepository.findAll()).thenReturn(List.of(radiology, surgery));
        List<Specialty> result = specialtyService.findAll();
        assertThat(result).hasSize(2);
    }

    @Test
    void findAll_returnsEmptyList() {
        when(specialtyRepository.findAll()).thenReturn(Collections.emptyList());
        List<Specialty> result = specialtyService.findAll();
        assertThat(result).isEmpty();
    }

    @Test
    void findById_existingId_returnsSpecialty() {
        when(specialtyRepository.findById(1)).thenReturn(Optional.of(radiology));
        Specialty result = specialtyService.findById(1);
        assertThat(result.getName()).isEqualTo("radiology");
    }

    @Test
    void findById_nonExistingId_throwsException() {
        when(specialtyRepository.findById(99)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> specialtyService.findById(99))
            .isInstanceOf(ResourceNotFoundException.class)
            .hasMessageContaining("Specialty not found with id: 99");
    }

    @Test
    void save_createsSpecialty() {
        SpecialtyDto dto = new SpecialtyDto(null, "dentistry");
        Specialty saved = new Specialty(3, "dentistry");
        when(specialtyRepository.save(any(Specialty.class))).thenReturn(saved);

        Specialty result = specialtyService.save(dto);
        assertThat(result.getName()).isEqualTo("dentistry");
        assertThat(result.getId()).isEqualTo(3);
        verify(specialtyRepository).save(any(Specialty.class));
    }

    @Test
    void update_existingSpecialty_updatesName() {
        SpecialtyDto dto = new SpecialtyDto(null, "oncology");
        when(specialtyRepository.findById(1)).thenReturn(Optional.of(radiology));
        when(specialtyRepository.save(any(Specialty.class))).thenReturn(radiology);

        Specialty result = specialtyService.update(1, dto);
        assertThat(result).isNotNull();
        verify(specialtyRepository).save(any(Specialty.class));
    }

    @Test
    void update_nonExistingSpecialty_throwsException() {
        SpecialtyDto dto = new SpecialtyDto(null, "oncology");
        when(specialtyRepository.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> specialtyService.update(99, dto))
            .isInstanceOf(ResourceNotFoundException.class)
            .hasMessageContaining("Specialty not found with id: 99");
    }

    @Test
    void delete_existingSpecialty_deletesSuccessfully() {
        when(specialtyRepository.findById(1)).thenReturn(Optional.of(radiology));
        specialtyService.delete(1);
        verify(specialtyRepository).delete(radiology);
    }

    @Test
    void delete_nonExistingSpecialty_throwsException() {
        when(specialtyRepository.findById(99)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> specialtyService.delete(99))
            .isInstanceOf(ResourceNotFoundException.class)
            .hasMessageContaining("Specialty not found with id: 99");
    }
}
