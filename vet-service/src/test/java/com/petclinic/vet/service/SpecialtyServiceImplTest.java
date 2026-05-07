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
        radiology = new Specialty();
        radiology.setId(1);
        radiology.setName("radiology");

        radiologyDto = new SpecialtyResponseDto(1, "radiology");
    }

    @Test
    void findAll_returnsAllSpecialties() {
        Specialty surgery = new Specialty();
        surgery.setId(2);
        surgery.setName("surgery");
        SpecialtyResponseDto surgeryDto = new SpecialtyResponseDto(2, "surgery");

        when(specialtyRepository.findAll()).thenReturn(List.of(radiology, surgery));
        when(specialtyMapper.toResponseDtos(List.of(radiology, surgery))).thenReturn(List.of(radiologyDto, surgeryDto));

        List<SpecialtyResponseDto> result = specialtyService.findAll();
        assertThat(result).hasSize(2);
        verify(specialtyRepository).findAll();
    }

    @Test
    void findById_existingId_returnsSpecialty() {
        when(specialtyRepository.findById(1)).thenReturn(Optional.of(radiology));
        when(specialtyMapper.toResponseDto(radiology)).thenReturn(radiologyDto);

        SpecialtyResponseDto result = specialtyService.findById(1);
        assertThat(result.getId()).isEqualTo(1);
        assertThat(result.getName()).isEqualTo("radiology");
    }

    @Test
    void findById_nonExistingId_throwsException() {
        when(specialtyRepository.findById(999)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> specialtyService.findById(999))
            .isInstanceOf(ResourceNotFoundException.class)
            .hasMessageContaining("Specialty not found with id: 999");
    }

    @Test
    void create_savesAndReturnsSpecialty() {
        SpecialtyRequestDto request = new SpecialtyRequestDto("oncology");
        Specialty oncology = new Specialty();
        oncology.setName("oncology");
        Specialty savedOncology = new Specialty();
        savedOncology.setId(4);
        savedOncology.setName("oncology");
        SpecialtyResponseDto oncologyDto = new SpecialtyResponseDto(4, "oncology");

        when(specialtyMapper.toEntity(request)).thenReturn(oncology);
        when(specialtyRepository.save(oncology)).thenReturn(savedOncology);
        when(specialtyMapper.toResponseDto(savedOncology)).thenReturn(oncologyDto);

        SpecialtyResponseDto result = specialtyService.create(request);
        assertThat(result.getId()).isEqualTo(4);
        assertThat(result.getName()).isEqualTo("oncology");
        verify(specialtyRepository).save(oncology);
    }

    @Test
    void update_existingId_updatesAndReturns() {
        SpecialtyRequestDto request = new SpecialtyRequestDto("updated-radiology");
        Specialty updated = new Specialty();
        updated.setId(1);
        updated.setName("updated-radiology");
        SpecialtyResponseDto updatedDto = new SpecialtyResponseDto(1, "updated-radiology");

        when(specialtyRepository.findById(1)).thenReturn(Optional.of(radiology));
        when(specialtyRepository.save(radiology)).thenReturn(updated);
        when(specialtyMapper.toResponseDto(updated)).thenReturn(updatedDto);

        SpecialtyResponseDto result = specialtyService.update(1, request);
        assertThat(result.getName()).isEqualTo("updated-radiology");
        verify(specialtyMapper).updateEntity(request, radiology);
    }

    @Test
    void update_nonExistingId_throwsException() {
        SpecialtyRequestDto request = new SpecialtyRequestDto("updated");
        when(specialtyRepository.findById(999)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> specialtyService.update(999, request))
            .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void delete_existingId_deletesSpecialty() {
        when(specialtyRepository.findById(1)).thenReturn(Optional.of(radiology));

        specialtyService.delete(1);
        verify(specialtyRepository).delete(radiology);
    }

    @Test
    void delete_nonExistingId_throwsException() {
        when(specialtyRepository.findById(999)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> specialtyService.delete(999))
            .isInstanceOf(ResourceNotFoundException.class);
    }
}
