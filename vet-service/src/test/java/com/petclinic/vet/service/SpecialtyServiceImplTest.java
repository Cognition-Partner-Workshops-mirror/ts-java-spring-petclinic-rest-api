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

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit tests for SpecialtyServiceImpl.
 * Uses Mockito to isolate service logic from the repository and mapper layers.
 */
@ExtendWith(MockitoExtension.class)
class SpecialtyServiceImplTest {

    @Mock
    private SpecialtyRepository specialtyRepository;

    @Mock
    private SpecialtyMapper specialtyMapper;

    @InjectMocks
    private SpecialtyServiceImpl specialtyService;

    private Specialty radiology;
    private SpecialtyDto radiologyDto;

    @BeforeEach
    void setUp() {
        radiology = new Specialty();
        radiology.setId(1);
        radiology.setName("radiology");
        radiology.setCreatedAt(Instant.now());
        radiology.setUpdatedAt(Instant.now());

        radiologyDto = new SpecialtyDto(1, "radiology");
    }

    @Test
    void listSpecialties_returnsAllSpecialties() {
        when(specialtyRepository.findAll()).thenReturn(List.of(radiology));
        when(specialtyMapper.toDtoList(List.of(radiology))).thenReturn(List.of(radiologyDto));

        List<SpecialtyDto> result = specialtyService.listSpecialties();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("radiology");
    }

    @Test
    void getSpecialty_existingId_returnsDto() {
        when(specialtyRepository.findById(1)).thenReturn(Optional.of(radiology));
        when(specialtyMapper.toDto(radiology)).thenReturn(radiologyDto);

        SpecialtyDto result = specialtyService.getSpecialty(1);

        assertThat(result.getId()).isEqualTo(1);
        assertThat(result.getName()).isEqualTo("radiology");
    }

    @Test
    void getSpecialty_nonExistingId_throwsNotFound() {
        when(specialtyRepository.findById(999)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> specialtyService.getSpecialty(999))
            .isInstanceOf(ResourceNotFoundException.class)
            .hasMessageContaining("Specialty not found with id: 999");
    }

    @Test
    void createSpecialty_savesAndReturnsDto() {
        SpecialtyRequestDto request = new SpecialtyRequestDto("cardiology");
        Specialty entity = new Specialty();
        entity.setName("cardiology");
        Specialty saved = new Specialty();
        saved.setId(4);
        saved.setName("cardiology");
        SpecialtyDto expectedDto = new SpecialtyDto(4, "cardiology");

        when(specialtyMapper.toEntity(request)).thenReturn(entity);
        when(specialtyRepository.save(entity)).thenReturn(saved);
        when(specialtyMapper.toDto(saved)).thenReturn(expectedDto);

        SpecialtyDto result = specialtyService.createSpecialty(request);

        assertThat(result.getId()).isEqualTo(4);
        assertThat(result.getName()).isEqualTo("cardiology");
    }

    @Test
    void updateSpecialty_existingId_updatesAndReturnsDto() {
        SpecialtyRequestDto request = new SpecialtyRequestDto("updated-radiology");
        Specialty updated = new Specialty();
        updated.setId(1);
        updated.setName("updated-radiology");
        SpecialtyDto updatedDto = new SpecialtyDto(1, "updated-radiology");

        when(specialtyRepository.findById(1)).thenReturn(Optional.of(radiology));
        when(specialtyRepository.save(radiology)).thenReturn(updated);
        when(specialtyMapper.toDto(updated)).thenReturn(updatedDto);

        SpecialtyDto result = specialtyService.updateSpecialty(1, request);

        assertThat(result.getName()).isEqualTo("updated-radiology");
        verify(specialtyMapper).updateEntity(request, radiology);
    }

    @Test
    void updateSpecialty_nonExistingId_throwsNotFound() {
        SpecialtyRequestDto request = new SpecialtyRequestDto("updated");
        when(specialtyRepository.findById(999)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> specialtyService.updateSpecialty(999, request))
            .isInstanceOf(ResourceNotFoundException.class)
            .hasMessageContaining("Specialty not found with id: 999");
    }

    @Test
    void deleteSpecialty_existingId_deletesAndReturnsDto() {
        when(specialtyRepository.findById(1)).thenReturn(Optional.of(radiology));
        when(specialtyMapper.toDto(radiology)).thenReturn(radiologyDto);

        SpecialtyDto result = specialtyService.deleteSpecialty(1);

        assertThat(result.getId()).isEqualTo(1);
        verify(specialtyRepository).delete(radiology);
    }

    @Test
    void deleteSpecialty_nonExistingId_throwsNotFound() {
        when(specialtyRepository.findById(999)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> specialtyService.deleteSpecialty(999))
            .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void searchByName_delegatesToRepository() {
        when(specialtyRepository.findByNameContainingIgnoreCase("rad"))
            .thenReturn(List.of(radiology));
        when(specialtyMapper.toDtoList(List.of(radiology))).thenReturn(List.of(radiologyDto));

        List<SpecialtyDto> result = specialtyService.searchByName("rad");

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("radiology");
    }
}
