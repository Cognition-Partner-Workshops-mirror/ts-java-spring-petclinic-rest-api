package com.petclinic.vet.service;

import com.petclinic.vet.dto.SpecialtyDto;
import com.petclinic.vet.dto.SpecialtyRequestDto;
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
import static org.mockito.ArgumentMatchers.anyList;
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
    private SpecialtyDto dto;
    private SpecialtyRequestDto requestDto;

    @BeforeEach
    void setUp() {
        specialty = new Specialty();
        specialty.setId(1);
        specialty.setName("radiology");

        dto = new SpecialtyDto(1, "radiology");
        requestDto = new SpecialtyRequestDto("radiology");
    }

    @Test
    void findAll_returnsAllSpecialties() {
        when(specialtyRepository.findAll()).thenReturn(List.of(specialty));
        when(specialtyMapper.toDtos(anyList())).thenReturn(List.of(dto));

        List<SpecialtyDto> result = specialtyService.findAll();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).name()).isEqualTo("radiology");
    }

    @Test
    void findById_returnsSpecialty() {
        when(specialtyRepository.findById(1)).thenReturn(Optional.of(specialty));
        when(specialtyMapper.toDto(specialty)).thenReturn(dto);

        SpecialtyDto result = specialtyService.findById(1);

        assertThat(result.id()).isEqualTo(1);
    }

    @Test
    void findById_throwsWhenNotFound() {
        when(specialtyRepository.findById(999)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> specialtyService.findById(999))
            .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void create_savesAndReturnsSpecialty() {
        when(specialtyMapper.toEntity(requestDto)).thenReturn(specialty);
        when(specialtyRepository.save(any(Specialty.class))).thenReturn(specialty);
        when(specialtyMapper.toDto(specialty)).thenReturn(dto);

        SpecialtyDto result = specialtyService.create(requestDto);

        assertThat(result.name()).isEqualTo("radiology");
        verify(specialtyRepository).save(any(Specialty.class));
    }

    @Test
    void update_updatesAndReturnsSpecialty() {
        when(specialtyRepository.findById(1)).thenReturn(Optional.of(specialty));
        when(specialtyRepository.save(any(Specialty.class))).thenReturn(specialty);
        when(specialtyMapper.toDto(specialty)).thenReturn(dto);

        SpecialtyDto result = specialtyService.update(1, requestDto);

        assertThat(result.name()).isEqualTo("radiology");
        verify(specialtyMapper).updateEntity(requestDto, specialty);
    }

    @Test
    void update_throwsWhenNotFound() {
        when(specialtyRepository.findById(999)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> specialtyService.update(999, requestDto))
            .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void delete_removesSpecialty() {
        when(specialtyRepository.findById(1)).thenReturn(Optional.of(specialty));

        specialtyService.delete(1);

        verify(specialtyRepository).delete(specialty);
    }

    @Test
    void delete_throwsWhenNotFound() {
        when(specialtyRepository.findById(999)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> specialtyService.delete(999))
            .isInstanceOf(ResourceNotFoundException.class);
    }
}
