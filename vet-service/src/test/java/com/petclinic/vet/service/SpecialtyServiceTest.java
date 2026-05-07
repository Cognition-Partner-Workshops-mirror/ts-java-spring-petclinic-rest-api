package com.petclinic.vet.service;

import com.petclinic.vet.dto.SpecialtyRequestDto;
import com.petclinic.vet.dto.SpecialtyResponseDto;
import com.petclinic.vet.entity.Specialty;
import com.petclinic.vet.exception.DuplicateResourceException;
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
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
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
        radiology = new Specialty("radiology");
        radiology.setId(1);
        radiologyDto = new SpecialtyResponseDto(1, "radiology");
    }

    @Test
    void findAll_returnsAllSpecialties() {
        Specialty surgery = new Specialty("surgery");
        surgery.setId(2);
        when(specialtyRepository.findAll()).thenReturn(List.of(radiology, surgery));
        when(specialtyMapper.toResponseDto(radiology)).thenReturn(radiologyDto);
        when(specialtyMapper.toResponseDto(surgery)).thenReturn(new SpecialtyResponseDto(2, "surgery"));

        List<SpecialtyResponseDto> result = specialtyService.findAll();

        assertThat(result).hasSize(2);
        verify(specialtyRepository).findAll();
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
    void findById_nonExistingId_throwsNotFoundException() {
        when(specialtyRepository.findById(999)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> specialtyService.findById(999))
            .isInstanceOf(ResourceNotFoundException.class)
            .hasMessageContaining("Specialty")
            .hasMessageContaining("999");
    }

    @Test
    void create_validRequest_createsAndReturnsSpecialty() {
        SpecialtyRequestDto request = new SpecialtyRequestDto("oncology");
        Specialty oncology = new Specialty("oncology");
        oncology.setId(4);
        SpecialtyResponseDto oncologyDto = new SpecialtyResponseDto(4, "oncology");

        when(specialtyRepository.existsByNameIgnoreCase("oncology")).thenReturn(false);
        when(specialtyRepository.save(any(Specialty.class))).thenReturn(oncology);
        when(specialtyMapper.toResponseDto(oncology)).thenReturn(oncologyDto);

        SpecialtyResponseDto result = specialtyService.create(request);

        assertThat(result.name()).isEqualTo("oncology");
        verify(specialtyRepository).save(any(Specialty.class));
    }

    @Test
    void create_duplicateName_throwsDuplicateException() {
        SpecialtyRequestDto request = new SpecialtyRequestDto("radiology");
        when(specialtyRepository.existsByNameIgnoreCase("radiology")).thenReturn(true);

        assertThatThrownBy(() -> specialtyService.create(request))
            .isInstanceOf(DuplicateResourceException.class)
            .hasMessageContaining("radiology");

        verify(specialtyRepository, never()).save(any());
    }

    @Test
    void update_existingId_updatesAndReturnsSpecialty() {
        SpecialtyRequestDto request = new SpecialtyRequestDto("radiology-updated");
        Specialty updated = new Specialty("radiology-updated");
        updated.setId(1);
        SpecialtyResponseDto updatedDto = new SpecialtyResponseDto(1, "radiology-updated");

        when(specialtyRepository.findById(1)).thenReturn(Optional.of(radiology));
        when(specialtyRepository.findByNameIgnoreCase("radiology-updated")).thenReturn(Optional.empty());
        when(specialtyRepository.save(any(Specialty.class))).thenReturn(updated);
        when(specialtyMapper.toResponseDto(updated)).thenReturn(updatedDto);

        SpecialtyResponseDto result = specialtyService.update(1, request);

        assertThat(result.name()).isEqualTo("radiology-updated");
    }

    @Test
    void update_nonExistingId_throwsNotFoundException() {
        SpecialtyRequestDto request = new SpecialtyRequestDto("oncology");
        when(specialtyRepository.findById(999)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> specialtyService.update(999, request))
            .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void update_duplicateNameDifferentId_throwsDuplicateException() {
        SpecialtyRequestDto request = new SpecialtyRequestDto("surgery");
        Specialty surgery = new Specialty("surgery");
        surgery.setId(2);

        when(specialtyRepository.findById(1)).thenReturn(Optional.of(radiology));
        when(specialtyRepository.findByNameIgnoreCase("surgery")).thenReturn(Optional.of(surgery));

        assertThatThrownBy(() -> specialtyService.update(1, request))
            .isInstanceOf(DuplicateResourceException.class);
    }

    @Test
    void update_sameNameSameId_doesNotThrow() {
        SpecialtyRequestDto request = new SpecialtyRequestDto("radiology");
        when(specialtyRepository.findById(1)).thenReturn(Optional.of(radiology));
        when(specialtyRepository.findByNameIgnoreCase("radiology")).thenReturn(Optional.of(radiology));
        when(specialtyRepository.save(any(Specialty.class))).thenReturn(radiology);
        when(specialtyMapper.toResponseDto(radiology)).thenReturn(radiologyDto);

        SpecialtyResponseDto result = specialtyService.update(1, request);
        assertThat(result.name()).isEqualTo("radiology");
    }

    @Test
    void delete_existingId_deletesAndReturnsSpecialty() {
        when(specialtyRepository.findById(1)).thenReturn(Optional.of(radiology));
        when(specialtyMapper.toResponseDto(radiology)).thenReturn(radiologyDto);

        SpecialtyResponseDto result = specialtyService.delete(1);

        assertThat(result.id()).isEqualTo(1);
        verify(specialtyRepository).delete(radiology);
    }

    @Test
    void delete_nonExistingId_throwsNotFoundException() {
        when(specialtyRepository.findById(999)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> specialtyService.delete(999))
            .isInstanceOf(ResourceNotFoundException.class);

        verify(specialtyRepository, never()).delete(any());
    }
}
