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

    private Specialty specialty;
    private SpecialtyResponseDto responseDto;
    private SpecialtyRequestDto requestDto;

    @BeforeEach
    void setUp() {
        specialty = new Specialty();
        specialty.setId(1);
        specialty.setName("radiology");

        responseDto = new SpecialtyResponseDto(1, "radiology");
        requestDto = new SpecialtyRequestDto("radiology");
    }

    @Test
    void listAll_returnsAllSpecialties() {
        when(specialtyRepository.findAll()).thenReturn(List.of(specialty));
        when(specialtyMapper.toResponseDto(specialty)).thenReturn(responseDto);

        List<SpecialtyResponseDto> result = specialtyService.listAll();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("radiology");
    }

    @Test
    void getById_existingId_returnsSpecialty() {
        when(specialtyRepository.findById(1)).thenReturn(Optional.of(specialty));
        when(specialtyMapper.toResponseDto(specialty)).thenReturn(responseDto);

        SpecialtyResponseDto result = specialtyService.getById(1);

        assertThat(result.getId()).isEqualTo(1);
        assertThat(result.getName()).isEqualTo("radiology");
    }

    @Test
    void getById_nonExistingId_throwsNotFoundException() {
        when(specialtyRepository.findById(999)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> specialtyService.getById(999))
            .isInstanceOf(ResourceNotFoundException.class)
            .hasMessageContaining("Specialty not found with id: 999");
    }

    @Test
    void create_validRequest_createsSpecialty() {
        when(specialtyRepository.existsByNameIgnoreCase("radiology")).thenReturn(false);
        when(specialtyMapper.toEntity(requestDto)).thenReturn(specialty);
        when(specialtyRepository.save(any(Specialty.class))).thenReturn(specialty);
        when(specialtyMapper.toResponseDto(specialty)).thenReturn(responseDto);

        SpecialtyResponseDto result = specialtyService.create(requestDto);

        assertThat(result.getName()).isEqualTo("radiology");
        verify(specialtyRepository).save(any(Specialty.class));
    }

    @Test
    void create_duplicateName_throwsDuplicateException() {
        when(specialtyRepository.existsByNameIgnoreCase("radiology")).thenReturn(true);

        assertThatThrownBy(() -> specialtyService.create(requestDto))
            .isInstanceOf(DuplicateResourceException.class)
            .hasMessageContaining("Specialty already exists with name: radiology");
    }

    @Test
    void update_existingId_updatesSpecialty() {
        SpecialtyRequestDto updateDto = new SpecialtyRequestDto("updated");
        when(specialtyRepository.findById(1)).thenReturn(Optional.of(specialty));
        when(specialtyRepository.findByNameIgnoreCase("updated")).thenReturn(Optional.empty());
        when(specialtyRepository.save(any(Specialty.class))).thenReturn(specialty);
        when(specialtyMapper.toResponseDto(specialty)).thenReturn(new SpecialtyResponseDto(1, "updated"));

        SpecialtyResponseDto result = specialtyService.update(1, updateDto);

        assertThat(result.getName()).isEqualTo("updated");
        verify(specialtyMapper).updateEntityFromDto(updateDto, specialty);
    }

    @Test
    void update_nonExistingId_throwsNotFoundException() {
        when(specialtyRepository.findById(999)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> specialtyService.update(999, requestDto))
            .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void update_duplicateName_throwsDuplicateException() {
        Specialty other = new Specialty();
        other.setId(2);
        other.setName("radiology");

        when(specialtyRepository.findById(1)).thenReturn(Optional.of(specialty));
        when(specialtyRepository.findByNameIgnoreCase("radiology")).thenReturn(Optional.of(other));

        assertThatThrownBy(() -> specialtyService.update(1, requestDto))
            .isInstanceOf(DuplicateResourceException.class);
    }

    @Test
    void update_sameName_sameEntity_succeeds() {
        when(specialtyRepository.findById(1)).thenReturn(Optional.of(specialty));
        when(specialtyRepository.findByNameIgnoreCase("radiology")).thenReturn(Optional.of(specialty));
        when(specialtyRepository.save(any(Specialty.class))).thenReturn(specialty);
        when(specialtyMapper.toResponseDto(specialty)).thenReturn(responseDto);

        SpecialtyResponseDto result = specialtyService.update(1, requestDto);

        assertThat(result.getName()).isEqualTo("radiology");
    }

    @Test
    void delete_existingId_deletesSpecialty() {
        when(specialtyRepository.findById(1)).thenReturn(Optional.of(specialty));
        when(specialtyMapper.toResponseDto(specialty)).thenReturn(responseDto);

        SpecialtyResponseDto result = specialtyService.delete(1);

        assertThat(result.getId()).isEqualTo(1);
        verify(specialtyRepository).delete(specialty);
    }

    @Test
    void delete_nonExistingId_throwsNotFoundException() {
        when(specialtyRepository.findById(999)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> specialtyService.delete(999))
            .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void searchByName_returnsMatchingSpecialties() {
        when(specialtyRepository.searchByName("rad")).thenReturn(List.of(specialty));
        when(specialtyMapper.toResponseDto(specialty)).thenReturn(responseDto);

        List<SpecialtyResponseDto> result = specialtyService.searchByName("rad");

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("radiology");
    }
}
