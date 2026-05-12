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
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit tests for SpecialtyServiceImpl.
 * Mocks the repository and mapper to isolate service business logic.
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
        radiology = new Specialty(1, "radiology");
        radiologyDto = new SpecialtyResponseDto(1, "radiology");
    }

    @Test
    void getAllSpecialties_returnsAllSpecialties() {
        // Arrange
        when(specialtyRepository.findAll()).thenReturn(List.of(radiology));
        when(specialtyMapper.toResponseDtoList(any())).thenReturn(List.of(radiologyDto));

        // Act
        List<SpecialtyResponseDto> result = specialtyService.getAllSpecialties();

        // Assert
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("radiology");
    }

    @Test
    void getSpecialtyById_existingId_returnsSpecialty() {
        when(specialtyRepository.findById(1)).thenReturn(Optional.of(radiology));
        when(specialtyMapper.toResponseDto(radiology)).thenReturn(radiologyDto);

        SpecialtyResponseDto result = specialtyService.getSpecialtyById(1);

        assertThat(result.getId()).isEqualTo(1);
        assertThat(result.getName()).isEqualTo("radiology");
    }

    @Test
    void getSpecialtyById_nonExistingId_throwsResourceNotFoundException() {
        when(specialtyRepository.findById(999)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> specialtyService.getSpecialtyById(999))
            .isInstanceOf(ResourceNotFoundException.class)
            .hasMessageContaining("Specialty")
            .hasMessageContaining("999");
    }

    @Test
    void createSpecialty_validRequest_createsSuccessfully() {
        SpecialtyRequestDto request = new SpecialtyRequestDto("oncology");
        Specialty newSpecialty = new Specialty(null, "oncology");
        Specialty savedSpecialty = new Specialty(4, "oncology");
        SpecialtyResponseDto savedDto = new SpecialtyResponseDto(4, "oncology");

        when(specialtyRepository.existsByNameIgnoreCase("oncology")).thenReturn(false);
        when(specialtyMapper.toEntity(request)).thenReturn(newSpecialty);
        when(specialtyRepository.save(newSpecialty)).thenReturn(savedSpecialty);
        when(specialtyMapper.toResponseDto(savedSpecialty)).thenReturn(savedDto);

        SpecialtyResponseDto result = specialtyService.createSpecialty(request);

        assertThat(result.getId()).isEqualTo(4);
        assertThat(result.getName()).isEqualTo("oncology");
    }

    @Test
    void createSpecialty_duplicateName_throwsDuplicateResourceException() {
        SpecialtyRequestDto request = new SpecialtyRequestDto("radiology");
        when(specialtyRepository.existsByNameIgnoreCase("radiology")).thenReturn(true);

        assertThatThrownBy(() -> specialtyService.createSpecialty(request))
            .isInstanceOf(DuplicateResourceException.class)
            .hasMessageContaining("radiology");
    }

    @Test
    void updateSpecialty_existingId_updatesSuccessfully() {
        SpecialtyRequestDto request = new SpecialtyRequestDto("cardiology");
        Specialty updated = new Specialty(1, "cardiology");
        SpecialtyResponseDto updatedDto = new SpecialtyResponseDto(1, "cardiology");

        when(specialtyRepository.findById(1)).thenReturn(Optional.of(radiology));
        when(specialtyRepository.findByNameIgnoreCase("cardiology")).thenReturn(List.of());
        when(specialtyRepository.save(radiology)).thenReturn(updated);
        when(specialtyMapper.toResponseDto(updated)).thenReturn(updatedDto);

        SpecialtyResponseDto result = specialtyService.updateSpecialty(1, request);

        assertThat(result.getName()).isEqualTo("cardiology");
        verify(specialtyMapper).updateEntityFromDto(request, radiology);
    }

    @Test
    void updateSpecialty_nonExistingId_throwsResourceNotFoundException() {
        SpecialtyRequestDto request = new SpecialtyRequestDto("cardiology");
        when(specialtyRepository.findById(999)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> specialtyService.updateSpecialty(999, request))
            .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void updateSpecialty_duplicateName_throwsDuplicateResourceException() {
        SpecialtyRequestDto request = new SpecialtyRequestDto("surgery");
        Specialty surgery = new Specialty(2, "surgery");

        when(specialtyRepository.findById(1)).thenReturn(Optional.of(radiology));
        when(specialtyRepository.findByNameIgnoreCase("surgery")).thenReturn(List.of(surgery));

        assertThatThrownBy(() -> specialtyService.updateSpecialty(1, request))
            .isInstanceOf(DuplicateResourceException.class);
    }

    @Test
    void updateSpecialty_sameNameSameId_updatesSuccessfully() {
        // Updating a specialty with its own name should not throw duplicate error
        SpecialtyRequestDto request = new SpecialtyRequestDto("radiology");
        Specialty updated = new Specialty(1, "radiology");
        SpecialtyResponseDto updatedDto = new SpecialtyResponseDto(1, "radiology");

        when(specialtyRepository.findById(1)).thenReturn(Optional.of(radiology));
        when(specialtyRepository.findByNameIgnoreCase("radiology")).thenReturn(List.of(radiology));
        when(specialtyRepository.save(radiology)).thenReturn(updated);
        when(specialtyMapper.toResponseDto(updated)).thenReturn(updatedDto);

        SpecialtyResponseDto result = specialtyService.updateSpecialty(1, request);

        assertThat(result.getName()).isEqualTo("radiology");
    }

    @Test
    void deleteSpecialty_existingId_deletesAndReturnsSpecialty() {
        when(specialtyRepository.findById(1)).thenReturn(Optional.of(radiology));
        when(specialtyMapper.toResponseDto(radiology)).thenReturn(radiologyDto);

        SpecialtyResponseDto result = specialtyService.deleteSpecialty(1);

        assertThat(result.getId()).isEqualTo(1);
        verify(specialtyRepository).delete(radiology);
    }

    @Test
    void deleteSpecialty_nonExistingId_throwsResourceNotFoundException() {
        when(specialtyRepository.findById(999)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> specialtyService.deleteSpecialty(999))
            .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void searchByName_returnsMatchingSpecialties() {
        when(specialtyRepository.searchByName("rad")).thenReturn(List.of(radiology));
        when(specialtyMapper.toResponseDtoList(any())).thenReturn(List.of(radiologyDto));

        List<SpecialtyResponseDto> result = specialtyService.searchByName("rad");

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("radiology");
    }
}
