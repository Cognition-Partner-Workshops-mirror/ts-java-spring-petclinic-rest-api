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
    void findAll_returnsList() {
        when(specialtyRepository.findAll()).thenReturn(List.of(radiology));
        when(specialtyMapper.toResponseDtos(List.of(radiology))).thenReturn(List.of(radiologyDto));

        List<SpecialtyResponseDto> result = specialtyService.findAll();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).name()).isEqualTo("radiology");
    }

    @Test
    void findAll_emptyList() {
        when(specialtyRepository.findAll()).thenReturn(List.of());
        when(specialtyMapper.toResponseDtos(List.of())).thenReturn(List.of());

        List<SpecialtyResponseDto> result = specialtyService.findAll();

        assertThat(result).isEmpty();
    }

    @Test
    void findById_found() {
        when(specialtyRepository.findById(1)).thenReturn(Optional.of(radiology));
        when(specialtyMapper.toResponseDto(radiology)).thenReturn(radiologyDto);

        SpecialtyResponseDto result = specialtyService.findById(1);

        assertThat(result.id()).isEqualTo(1);
        assertThat(result.name()).isEqualTo("radiology");
    }

    @Test
    void findById_notFound_throwsException() {
        when(specialtyRepository.findById(999)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> specialtyService.findById(999))
            .isInstanceOf(ResourceNotFoundException.class)
            .hasMessageContaining("Specialty not found with id: 999");
    }

    @Test
    void create_savesAndReturns() {
        SpecialtyRequestDto request = new SpecialtyRequestDto("oncology");
        Specialty newEntity = new Specialty();
        newEntity.setName("oncology");
        Specialty saved = new Specialty();
        saved.setId(4);
        saved.setName("oncology");
        SpecialtyResponseDto savedDto = new SpecialtyResponseDto(4, "oncology");

        when(specialtyMapper.toEntity(request)).thenReturn(newEntity);
        when(specialtyRepository.save(newEntity)).thenReturn(saved);
        when(specialtyMapper.toResponseDto(saved)).thenReturn(savedDto);

        SpecialtyResponseDto result = specialtyService.create(request);

        assertThat(result.id()).isEqualTo(4);
        assertThat(result.name()).isEqualTo("oncology");
        verify(specialtyRepository).save(newEntity);
    }

    @Test
    void update_existingSpecialty() {
        SpecialtyRequestDto request = new SpecialtyRequestDto("updated-radiology");
        Specialty updated = new Specialty();
        updated.setId(1);
        updated.setName("updated-radiology");
        SpecialtyResponseDto updatedDto = new SpecialtyResponseDto(1, "updated-radiology");

        when(specialtyRepository.findById(1)).thenReturn(Optional.of(radiology));
        when(specialtyRepository.save(radiology)).thenReturn(updated);
        when(specialtyMapper.toResponseDto(updated)).thenReturn(updatedDto);

        SpecialtyResponseDto result = specialtyService.update(1, request);

        assertThat(result.name()).isEqualTo("updated-radiology");
        verify(specialtyMapper).updateEntity(eq(request), eq(radiology));
    }

    @Test
    void update_notFound_throwsException() {
        SpecialtyRequestDto request = new SpecialtyRequestDto("updated");
        when(specialtyRepository.findById(999)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> specialtyService.update(999, request))
            .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void delete_existingSpecialty() {
        when(specialtyRepository.findById(1)).thenReturn(Optional.of(radiology));

        specialtyService.delete(1);

        verify(specialtyRepository).delete(radiology);
    }

    @Test
    void delete_notFound_throwsException() {
        when(specialtyRepository.findById(999)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> specialtyService.delete(999))
            .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void searchByName_returnsResults() {
        when(specialtyRepository.findByNameContainingIgnoreCase("radio")).thenReturn(List.of(radiology));
        when(specialtyMapper.toResponseDtos(List.of(radiology))).thenReturn(List.of(radiologyDto));

        List<SpecialtyResponseDto> result = specialtyService.searchByName("radio");

        assertThat(result).hasSize(1);
    }

    @Test
    void searchByName_noResults() {
        when(specialtyRepository.findByNameContainingIgnoreCase("unknown")).thenReturn(List.of());
        when(specialtyMapper.toResponseDtos(List.of())).thenReturn(List.of());

        List<SpecialtyResponseDto> result = specialtyService.searchByName("unknown");

        assertThat(result).isEmpty();
    }
}
