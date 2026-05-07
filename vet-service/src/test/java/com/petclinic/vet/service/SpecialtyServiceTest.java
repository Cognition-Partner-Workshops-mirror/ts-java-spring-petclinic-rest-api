package com.petclinic.vet.service;

import com.petclinic.vet.dto.SpecialtyRequestDto;
import com.petclinic.vet.entity.Specialty;
import com.petclinic.vet.exception.ResourceNotFoundException;
import com.petclinic.vet.repository.SpecialtyRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
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

    @InjectMocks
    private SpecialtyServiceImpl specialtyService;

    private Specialty radiology;
    private Specialty surgery;

    @BeforeEach
    void setUp() {
        radiology = new Specialty();
        radiology.setId(1);
        radiology.setName("radiology");

        surgery = new Specialty();
        surgery.setId(2);
        surgery.setName("surgery");
    }

    @Test
    void findAll_returnsAllSpecialties() {
        when(specialtyRepository.findAll()).thenReturn(Arrays.asList(radiology, surgery));

        List<Specialty> result = specialtyService.findAll();

        assertThat(result).hasSize(2);
        assertThat(result).containsExactly(radiology, surgery);
    }

    @Test
    void findById_existingId_returnsSpecialty() {
        when(specialtyRepository.findById(1)).thenReturn(Optional.of(radiology));

        Specialty result = specialtyService.findById(1);

        assertThat(result).isEqualTo(radiology);
        assertThat(result.getName()).isEqualTo("radiology");
    }

    @Test
    void findById_nonExistingId_throwsResourceNotFoundException() {
        when(specialtyRepository.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> specialtyService.findById(99))
            .isInstanceOf(ResourceNotFoundException.class)
            .hasMessageContaining("Specialty")
            .hasMessageContaining("99");
    }

    @Test
    void create_validDto_returnsCreatedSpecialty() {
        SpecialtyRequestDto dto = new SpecialtyRequestDto("oncology");
        Specialty saved = new Specialty();
        saved.setId(3);
        saved.setName("oncology");
        when(specialtyRepository.save(any(Specialty.class))).thenReturn(saved);

        Specialty result = specialtyService.create(dto);

        assertThat(result.getId()).isEqualTo(3);
        assertThat(result.getName()).isEqualTo("oncology");
        verify(specialtyRepository).save(any(Specialty.class));
    }

    @Test
    void update_existingId_returnsUpdatedSpecialty() {
        SpecialtyRequestDto dto = new SpecialtyRequestDto("updated-radiology");
        when(specialtyRepository.findById(1)).thenReturn(Optional.of(radiology));
        Specialty updated = new Specialty();
        updated.setId(1);
        updated.setName("updated-radiology");
        when(specialtyRepository.save(any(Specialty.class))).thenReturn(updated);

        Specialty result = specialtyService.update(1, dto);

        assertThat(result.getName()).isEqualTo("updated-radiology");
        verify(specialtyRepository).save(any(Specialty.class));
    }

    @Test
    void update_nonExistingId_throwsResourceNotFoundException() {
        SpecialtyRequestDto dto = new SpecialtyRequestDto("updated");
        when(specialtyRepository.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> specialtyService.update(99, dto))
            .isInstanceOf(ResourceNotFoundException.class)
            .hasMessageContaining("Specialty")
            .hasMessageContaining("99");
    }

    @Test
    void delete_existingId_deletesSpecialty() {
        when(specialtyRepository.findById(1)).thenReturn(Optional.of(radiology));

        specialtyService.delete(1);

        verify(specialtyRepository).delete(radiology);
    }

    @Test
    void delete_nonExistingId_throwsResourceNotFoundException() {
        when(specialtyRepository.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> specialtyService.delete(99))
            .isInstanceOf(ResourceNotFoundException.class)
            .hasMessageContaining("Specialty")
            .hasMessageContaining("99");
    }
}
