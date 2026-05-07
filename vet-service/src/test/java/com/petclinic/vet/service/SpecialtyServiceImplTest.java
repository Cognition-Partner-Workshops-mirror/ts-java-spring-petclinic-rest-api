package com.petclinic.vet.service;

import com.petclinic.vet.dto.request.SpecialtyRequestDto;
import com.petclinic.vet.dto.response.SpecialtyResponseDto;
import com.petclinic.vet.entity.Specialty;
import com.petclinic.vet.exception.ResourceNotFoundException;
import com.petclinic.vet.mapper.SpecialtyMapper;
import com.petclinic.vet.repository.SpecialtyRepository;
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

    @Test
    void findAll_returnsAllSpecialties() {
        Specialty specialty = buildSpecialty(1, "radiology");
        SpecialtyResponseDto dto = new SpecialtyResponseDto(1, "radiology");
        when(specialtyRepository.findAll()).thenReturn(List.of(specialty));
        when(specialtyMapper.toResponseDtos(anyCollection())).thenReturn(List.of(dto));

        List<SpecialtyResponseDto> result = specialtyService.findAll();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).name()).isEqualTo("radiology");
    }

    @Test
    void findById_existing_returnsSpecialty() {
        Specialty specialty = buildSpecialty(1, "radiology");
        SpecialtyResponseDto dto = new SpecialtyResponseDto(1, "radiology");
        when(specialtyRepository.findById(1)).thenReturn(Optional.of(specialty));
        when(specialtyMapper.toResponseDto(specialty)).thenReturn(dto);

        SpecialtyResponseDto result = specialtyService.findById(1);

        assertThat(result.id()).isEqualTo(1);
    }

    @Test
    void findById_nonExisting_throwsNotFound() {
        when(specialtyRepository.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> specialtyService.findById(99))
            .isInstanceOf(ResourceNotFoundException.class)
            .hasMessageContaining("Specialty");
    }

    @Test
    void create_savesAndReturns() {
        SpecialtyRequestDto request = new SpecialtyRequestDto(null, "orthopedics");
        Specialty entity = buildSpecialty(null, "orthopedics");
        Specialty saved = buildSpecialty(4, "orthopedics");
        SpecialtyResponseDto responseDto = new SpecialtyResponseDto(4, "orthopedics");

        when(specialtyMapper.toEntity(request)).thenReturn(entity);
        when(specialtyRepository.save(entity)).thenReturn(saved);
        when(specialtyMapper.toResponseDto(saved)).thenReturn(responseDto);

        SpecialtyResponseDto result = specialtyService.create(request);

        assertThat(result.id()).isEqualTo(4);
        assertThat(result.name()).isEqualTo("orthopedics");
    }

    @Test
    void update_existing_updatesAndReturns() {
        SpecialtyRequestDto request = new SpecialtyRequestDto(null, "updated");
        Specialty existing = buildSpecialty(1, "radiology");
        Specialty saved = buildSpecialty(1, "updated");
        SpecialtyResponseDto responseDto = new SpecialtyResponseDto(1, "updated");

        when(specialtyRepository.findById(1)).thenReturn(Optional.of(existing));
        when(specialtyRepository.save(existing)).thenReturn(saved);
        when(specialtyMapper.toResponseDto(saved)).thenReturn(responseDto);

        SpecialtyResponseDto result = specialtyService.update(1, request);

        assertThat(result.name()).isEqualTo("updated");
        verify(specialtyMapper).updateEntity(request, existing);
    }

    @Test
    void update_nonExisting_throwsNotFound() {
        SpecialtyRequestDto request = new SpecialtyRequestDto(null, "xyz");
        when(specialtyRepository.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> specialtyService.update(99, request))
            .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void delete_existing_deletesAndReturns() {
        Specialty specialty = buildSpecialty(1, "radiology");
        SpecialtyResponseDto dto = new SpecialtyResponseDto(1, "radiology");
        when(specialtyRepository.findById(1)).thenReturn(Optional.of(specialty));
        when(specialtyMapper.toResponseDto(specialty)).thenReturn(dto);

        SpecialtyResponseDto result = specialtyService.delete(1);

        assertThat(result.id()).isEqualTo(1);
        verify(specialtyRepository).delete(specialty);
    }

    @Test
    void delete_nonExisting_throwsNotFound() {
        when(specialtyRepository.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> specialtyService.delete(99))
            .isInstanceOf(ResourceNotFoundException.class);
    }

    private Specialty buildSpecialty(Integer id, String name) {
        Specialty specialty = new Specialty();
        specialty.setId(id);
        specialty.setName(name);
        return specialty;
    }
}
