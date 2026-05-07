package com.petclinic.vet.service;

import com.petclinic.vet.dto.SpecialtyRequestDto;
import com.petclinic.vet.dto.SpecialtyResponseDto;
import com.petclinic.vet.entity.Specialty;
import com.petclinic.vet.exception.ResourceNotFoundException;
import com.petclinic.vet.mapper.SpecialtyMapper;
import com.petclinic.vet.repository.SpecialtyRepository;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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
    void findAll_returnsAllSpecialties() {
        Specialty surgery = new Specialty(2, "surgery");
        SpecialtyResponseDto surgeryDto = new SpecialtyResponseDto(2, "surgery");
        when(specialtyRepository.findAll()).thenReturn(List.of(radiology, surgery));
        when(specialtyMapper.toResponseDto(radiology)).thenReturn(radiologyDto);
        when(specialtyMapper.toResponseDto(surgery)).thenReturn(surgeryDto);

        List<SpecialtyResponseDto> result = specialtyService.findAll();

        assertThat(result).hasSize(2);
        assertThat(result.get(0).name()).isEqualTo("radiology");
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
    void findById_nonExistingId_throwsException() {
        when(specialtyRepository.findById(999)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> specialtyService.findById(999))
            .isInstanceOf(ResourceNotFoundException.class)
            .hasMessageContaining("Specialty");
    }

    @Test
    void create_validDto_returnsCreated() {
        SpecialtyRequestDto request = new SpecialtyRequestDto("oncology");
        Specialty entity = new Specialty(null, "oncology");
        Specialty saved = new Specialty(4, "oncology");
        SpecialtyResponseDto response = new SpecialtyResponseDto(4, "oncology");

        when(specialtyMapper.toEntity(request)).thenReturn(entity);
        when(specialtyRepository.save(entity)).thenReturn(saved);
        when(specialtyMapper.toResponseDto(saved)).thenReturn(response);

        SpecialtyResponseDto result = specialtyService.create(request);

        assertThat(result.id()).isEqualTo(4);
        assertThat(result.name()).isEqualTo("oncology");
    }

    @Test
    void update_existingId_returnsUpdated() {
        SpecialtyRequestDto request = new SpecialtyRequestDto("updated");
        Specialty updated = new Specialty(1, "updated");
        SpecialtyResponseDto response = new SpecialtyResponseDto(1, "updated");

        when(specialtyRepository.findById(1)).thenReturn(Optional.of(radiology));
        when(specialtyRepository.save(radiology)).thenReturn(updated);
        when(specialtyMapper.toResponseDto(updated)).thenReturn(response);

        SpecialtyResponseDto result = specialtyService.update(1, request);

        assertThat(result.name()).isEqualTo("updated");
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
    void delete_existingId_returnsDeleted() {
        when(specialtyRepository.findById(1)).thenReturn(Optional.of(radiology));
        when(specialtyMapper.toResponseDto(radiology)).thenReturn(radiologyDto);

        SpecialtyResponseDto result = specialtyService.delete(1);

        assertThat(result.id()).isEqualTo(1);
        verify(specialtyRepository).delete(radiology);
    }

    @Test
    void delete_nonExistingId_throwsException() {
        when(specialtyRepository.findById(999)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> specialtyService.delete(999))
            .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void searchByName_matchingName_returnsResults() {
        when(specialtyRepository.findByNameContainingIgnoreCase("radio"))
            .thenReturn(List.of(radiology));
        when(specialtyMapper.toResponseDto(radiology)).thenReturn(radiologyDto);

        List<SpecialtyResponseDto> result = specialtyService.searchByName("radio");

        assertThat(result).hasSize(1);
        assertThat(result.get(0).name()).isEqualTo("radiology");
    }
}
