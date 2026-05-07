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
import static org.mockito.ArgumentMatchers.anyCollection;
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
        when(specialtyRepository.findAll()).thenReturn(List.of(radiology));
        when(specialtyMapper.toResponseDtos(anyCollection())).thenReturn(List.of(radiologyDto));

        List<SpecialtyResponseDto> result = specialtyService.findAll();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("radiology");
        verify(specialtyRepository).findAll();
    }

    @Test
    void findById_existingId_returnsSpecialty() {
        when(specialtyRepository.findById(1)).thenReturn(Optional.of(radiology));
        when(specialtyMapper.toResponseDto(radiology)).thenReturn(radiologyDto);

        SpecialtyResponseDto result = specialtyService.findById(1);

        assertThat(result.getName()).isEqualTo("radiology");
    }

    @Test
    void findById_nonExistingId_throwsResourceNotFoundException() {
        when(specialtyRepository.findById(999)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> specialtyService.findById(999))
            .isInstanceOf(ResourceNotFoundException.class)
            .hasMessageContaining("Specialty");
    }

    @Test
    void create_validDto_createsAndReturns() {
        SpecialtyRequestDto requestDto = new SpecialtyRequestDto("oncology");
        Specialty newSpecialty = new Specialty();
        newSpecialty.setName("oncology");
        Specialty saved = new Specialty();
        saved.setId(4);
        saved.setName("oncology");
        SpecialtyResponseDto savedDto = new SpecialtyResponseDto(4, "oncology");

        when(specialtyMapper.toEntity(requestDto)).thenReturn(newSpecialty);
        when(specialtyRepository.save(newSpecialty)).thenReturn(saved);
        when(specialtyMapper.toResponseDto(saved)).thenReturn(savedDto);

        SpecialtyResponseDto result = specialtyService.create(requestDto);

        assertThat(result.getId()).isEqualTo(4);
        assertThat(result.getName()).isEqualTo("oncology");
        verify(specialtyRepository).save(any(Specialty.class));
    }

    @Test
    void update_existingId_updatesAndReturns() {
        SpecialtyRequestDto requestDto = new SpecialtyRequestDto("updated");
        Specialty updated = new Specialty();
        updated.setId(1);
        updated.setName("updated");
        SpecialtyResponseDto updatedDto = new SpecialtyResponseDto(1, "updated");

        when(specialtyRepository.findById(1)).thenReturn(Optional.of(radiology));
        when(specialtyRepository.save(radiology)).thenReturn(updated);
        when(specialtyMapper.toResponseDto(updated)).thenReturn(updatedDto);

        SpecialtyResponseDto result = specialtyService.update(1, requestDto);

        assertThat(result.getName()).isEqualTo("updated");
        verify(specialtyMapper).updateEntity(requestDto, radiology);
    }

    @Test
    void update_nonExistingId_throwsResourceNotFoundException() {
        when(specialtyRepository.findById(999)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> specialtyService.update(999, new SpecialtyRequestDto("x")))
            .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void delete_existingId_deletesSuccessfully() {
        when(specialtyRepository.findById(1)).thenReturn(Optional.of(radiology));

        specialtyService.delete(1);

        verify(specialtyRepository).delete(radiology);
    }

    @Test
    void delete_nonExistingId_throwsResourceNotFoundException() {
        when(specialtyRepository.findById(999)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> specialtyService.delete(999))
            .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void searchByName_returnsMatchingSpecialties() {
        when(specialtyRepository.findByNameContainingIgnoreCase("radio")).thenReturn(List.of(radiology));
        when(specialtyMapper.toResponseDtos(anyCollection())).thenReturn(List.of(radiologyDto));

        List<SpecialtyResponseDto> result = specialtyService.searchByName("radio");

        assertThat(result).hasSize(1);
        verify(specialtyRepository).findByNameContainingIgnoreCase("radio");
    }
}
