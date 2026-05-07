package com.petclinic.vet.service;

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

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class SpecialtyServiceTest {

    @Mock
    private SpecialtyRepository specialtyRepository;

    @Mock
    private SpecialtyMapper specialtyMapper;

    @InjectMocks
    private SpecialtyService specialtyService;

    private Specialty radiology;
    private Specialty surgery;

    @BeforeEach
    void setUp() {
        radiology = new Specialty("radiology");
        radiology.setId(1);
        surgery = new Specialty("surgery");
        surgery.setId(2);
    }

    @Test
    void findAll_returnsAllSpecialties() {
        given(specialtyRepository.findAll()).willReturn(List.of(radiology, surgery));

        List<Specialty> result = specialtyService.findAll();

        assertThat(result).hasSize(2);
        assertThat(result).containsExactly(radiology, surgery);
    }

    @Test
    void findById_existingId_returnsSpecialty() {
        given(specialtyRepository.findById(1)).willReturn(Optional.of(radiology));

        Specialty result = specialtyService.findById(1);

        assertThat(result.getName()).isEqualTo("radiology");
    }

    @Test
    void findById_nonExistingId_throwsNotFound() {
        given(specialtyRepository.findById(999)).willReturn(Optional.empty());

        assertThatThrownBy(() -> specialtyService.findById(999))
            .isInstanceOf(ResourceNotFoundException.class)
            .hasMessageContaining("Specialty")
            .hasMessageContaining("999");
    }

    @Test
    void create_validDto_returnsCreatedSpecialty() {
        SpecialtyRequestDto dto = new SpecialtyRequestDto("dentistry");
        Specialty dentistry = new Specialty("dentistry");
        dentistry.setId(3);

        given(specialtyMapper.toEntity(dto)).willReturn(dentistry);
        given(specialtyRepository.save(dentistry)).willReturn(dentistry);

        Specialty result = specialtyService.create(dto);

        assertThat(result.getId()).isEqualTo(3);
        assertThat(result.getName()).isEqualTo("dentistry");
    }

    @Test
    void update_existingId_returnsUpdatedSpecialty() {
        SpecialtyRequestDto dto = new SpecialtyRequestDto("updated-radiology");

        given(specialtyRepository.findById(1)).willReturn(Optional.of(radiology));
        given(specialtyRepository.save(radiology)).willReturn(radiology);

        Specialty result = specialtyService.update(1, dto);

        verify(specialtyMapper).updateEntity(dto, radiology);
        assertThat(result).isEqualTo(radiology);
    }

    @Test
    void update_nonExistingId_throwsNotFound() {
        SpecialtyRequestDto dto = new SpecialtyRequestDto("updated");

        given(specialtyRepository.findById(999)).willReturn(Optional.empty());

        assertThatThrownBy(() -> specialtyService.update(999, dto))
            .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void delete_existingId_deletesSuccessfully() {
        given(specialtyRepository.findById(1)).willReturn(Optional.of(radiology));

        specialtyService.delete(1);

        verify(specialtyRepository).delete(radiology);
    }

    @Test
    void delete_nonExistingId_throwsNotFound() {
        given(specialtyRepository.findById(999)).willReturn(Optional.empty());

        assertThatThrownBy(() -> specialtyService.delete(999))
            .isInstanceOf(ResourceNotFoundException.class);
    }
}
