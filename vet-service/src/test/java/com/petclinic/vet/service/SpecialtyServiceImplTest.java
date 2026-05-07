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
class SpecialtyServiceImplTest {

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
    void listSpecialties_returnsAll() {
        when(specialtyRepository.findAll()).thenReturn(List.of(radiology));
        when(specialtyMapper.toResponseDtoList(List.of(radiology))).thenReturn(List.of(radiologyDto));

        List<SpecialtyResponseDto> result = specialtyService.listSpecialties();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).name()).isEqualTo("radiology");
    }

    @Test
    void getSpecialty_found() {
        when(specialtyRepository.findById(1)).thenReturn(Optional.of(radiology));
        when(specialtyMapper.toResponseDto(radiology)).thenReturn(radiologyDto);

        SpecialtyResponseDto result = specialtyService.getSpecialty(1);

        assertThat(result.id()).isEqualTo(1);
        assertThat(result.name()).isEqualTo("radiology");
    }

    @Test
    void getSpecialty_notFound_throwsException() {
        when(specialtyRepository.findById(999)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> specialtyService.getSpecialty(999))
            .isInstanceOf(ResourceNotFoundException.class)
            .hasMessageContaining("Specialty")
            .hasMessageContaining("999");
    }

    @Test
    void addSpecialty_createsAndReturns() {
        SpecialtyRequestDto request = new SpecialtyRequestDto("surgery");
        Specialty surgery = new Specialty(2, "surgery");
        SpecialtyResponseDto surgeryDto = new SpecialtyResponseDto(2, "surgery");

        when(specialtyMapper.toEntity(request)).thenReturn(surgery);
        when(specialtyRepository.save(surgery)).thenReturn(surgery);
        when(specialtyMapper.toResponseDto(surgery)).thenReturn(surgeryDto);

        SpecialtyResponseDto result = specialtyService.addSpecialty(request);

        assertThat(result.name()).isEqualTo("surgery");
        verify(specialtyRepository).save(any(Specialty.class));
    }

    @Test
    void updateSpecialty_found_updatesAndReturns() {
        SpecialtyRequestDto request = new SpecialtyRequestDto("dentistry");
        Specialty updated = new Specialty(1, "dentistry");
        SpecialtyResponseDto updatedDto = new SpecialtyResponseDto(1, "dentistry");

        when(specialtyRepository.findById(1)).thenReturn(Optional.of(radiology));
        when(specialtyRepository.save(radiology)).thenReturn(updated);
        when(specialtyMapper.toResponseDto(updated)).thenReturn(updatedDto);

        SpecialtyResponseDto result = specialtyService.updateSpecialty(1, request);

        assertThat(result.name()).isEqualTo("dentistry");
        verify(specialtyMapper).updateEntity(request, radiology);
    }

    @Test
    void updateSpecialty_notFound_throwsException() {
        when(specialtyRepository.findById(999)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> specialtyService.updateSpecialty(999, new SpecialtyRequestDto("x")))
            .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void deleteSpecialty_found_deletesAndReturns() {
        when(specialtyRepository.findById(1)).thenReturn(Optional.of(radiology));
        when(specialtyMapper.toResponseDto(radiology)).thenReturn(radiologyDto);

        SpecialtyResponseDto result = specialtyService.deleteSpecialty(1);

        assertThat(result.id()).isEqualTo(1);
        verify(specialtyRepository).delete(radiology);
    }

    @Test
    void deleteSpecialty_notFound_throwsException() {
        when(specialtyRepository.findById(999)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> specialtyService.deleteSpecialty(999))
            .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void searchByName_returnsMatches() {
        when(specialtyRepository.findByNameContainingIgnoreCase("rad"))
            .thenReturn(List.of(radiology));
        when(specialtyMapper.toResponseDtoList(List.of(radiology)))
            .thenReturn(List.of(radiologyDto));

        List<SpecialtyResponseDto> result = specialtyService.searchByName("rad");

        assertThat(result).hasSize(1);
    }
}
