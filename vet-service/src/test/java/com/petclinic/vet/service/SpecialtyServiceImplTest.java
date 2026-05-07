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
    private SpecialtyRepository repository;

    @Mock
    private SpecialtyMapper mapper;

    @InjectMocks
    private SpecialtyServiceImpl service;

    private Specialty radiology;
    private SpecialtyResponseDto radiologyDto;

    @BeforeEach
    void setUp() {
        radiology = new Specialty(1, "radiology");
        radiologyDto = new SpecialtyResponseDto(1, "radiology");
    }

    @Test
    void listAll_returnsAllSpecialties() {
        when(repository.findAll()).thenReturn(List.of(radiology));
        when(mapper.toResponseDtoList(List.of(radiology))).thenReturn(List.of(radiologyDto));

        List<SpecialtyResponseDto> result = service.listAll();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).name()).isEqualTo("radiology");
    }

    @Test
    void getById_existingId_returnsSpecialty() {
        when(repository.findById(1)).thenReturn(Optional.of(radiology));
        when(mapper.toResponseDto(radiology)).thenReturn(radiologyDto);

        SpecialtyResponseDto result = service.getById(1);

        assertThat(result.name()).isEqualTo("radiology");
    }

    @Test
    void getById_nonExistingId_throwsException() {
        when(repository.findById(999)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.getById(999))
            .isInstanceOf(ResourceNotFoundException.class)
            .hasMessageContaining("Specialty not found with id: 999");
    }

    @Test
    void create_validDto_returnsCreated() {
        SpecialtyRequestDto request = new SpecialtyRequestDto("radiology");
        Specialty entity = new Specialty();
        entity.setName("radiology");

        when(mapper.toEntity(request)).thenReturn(entity);
        when(repository.save(entity)).thenReturn(radiology);
        when(mapper.toResponseDto(radiology)).thenReturn(radiologyDto);

        SpecialtyResponseDto result = service.create(request);

        assertThat(result.name()).isEqualTo("radiology");
        verify(repository).save(entity);
    }

    @Test
    void update_existingId_returnsUpdated() {
        SpecialtyRequestDto request = new SpecialtyRequestDto("surgery");
        Specialty updated = new Specialty(1, "surgery");
        SpecialtyResponseDto updatedDto = new SpecialtyResponseDto(1, "surgery");

        when(repository.findById(1)).thenReturn(Optional.of(radiology));
        when(repository.save(radiology)).thenReturn(updated);
        when(mapper.toResponseDto(updated)).thenReturn(updatedDto);

        SpecialtyResponseDto result = service.update(1, request);

        assertThat(result.name()).isEqualTo("surgery");
        verify(mapper).updateEntity(request, radiology);
    }

    @Test
    void update_nonExistingId_throwsException() {
        SpecialtyRequestDto request = new SpecialtyRequestDto("surgery");
        when(repository.findById(999)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.update(999, request))
            .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void delete_existingId_returnsDeleted() {
        when(repository.findById(1)).thenReturn(Optional.of(radiology));
        when(mapper.toResponseDto(radiology)).thenReturn(radiologyDto);

        SpecialtyResponseDto result = service.delete(1);

        assertThat(result.name()).isEqualTo("radiology");
        verify(repository).delete(radiology);
    }

    @Test
    void delete_nonExistingId_throwsException() {
        when(repository.findById(999)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.delete(999))
            .isInstanceOf(ResourceNotFoundException.class);
    }
}
