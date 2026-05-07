package com.petclinic.vet.service;

import com.petclinic.vet.dto.SpecialtyRequestDto;
import com.petclinic.vet.dto.SpecialtyResponseDto;
import com.petclinic.vet.entity.Specialty;
import com.petclinic.vet.exception.ResourceNotFoundException;
import com.petclinic.vet.mapper.SpecialtyMapper;
import com.petclinic.vet.repository.SpecialtyRepository;
import java.time.Instant;
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
    private SpecialtyResponseDto radiologyResponse;

    @BeforeEach
    void setUp() {
        radiology = new Specialty();
        radiology.setId(1);
        radiology.setName("radiology");
        radiology.setCreatedAt(Instant.now());
        radiology.setUpdatedAt(Instant.now());

        radiologyResponse = new SpecialtyResponseDto(1, "radiology");
    }

    @Test
    void listAll_returnsAll() {
        when(repository.findAll()).thenReturn(List.of(radiology));
        when(mapper.toResponseList(List.of(radiology))).thenReturn(List.of(radiologyResponse));

        List<SpecialtyResponseDto> result = service.listAll();
        assertThat(result).hasSize(1);
        assertThat(result.get(0).name()).isEqualTo("radiology");
    }

    @Test
    void getById_found() {
        when(repository.findById(1)).thenReturn(Optional.of(radiology));
        when(mapper.toResponse(radiology)).thenReturn(radiologyResponse);

        SpecialtyResponseDto result = service.getById(1);
        assertThat(result.id()).isEqualTo(1);
        assertThat(result.name()).isEqualTo("radiology");
    }

    @Test
    void getById_notFound() {
        when(repository.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.getById(99))
            .isInstanceOf(ResourceNotFoundException.class)
            .hasMessageContaining("Specialty not found with id 99");
    }

    @Test
    void create_success() {
        SpecialtyRequestDto request = new SpecialtyRequestDto("radiology");
        when(mapper.toEntity(request)).thenReturn(radiology);
        when(repository.save(radiology)).thenReturn(radiology);
        when(mapper.toResponse(radiology)).thenReturn(radiologyResponse);

        SpecialtyResponseDto result = service.create(request);
        assertThat(result.name()).isEqualTo("radiology");
        verify(repository).save(any(Specialty.class));
    }

    @Test
    void update_success() {
        SpecialtyRequestDto request = new SpecialtyRequestDto("updated");
        when(repository.findById(1)).thenReturn(Optional.of(radiology));
        when(repository.save(radiology)).thenReturn(radiology);
        when(mapper.toResponse(radiology)).thenReturn(new SpecialtyResponseDto(1, "updated"));

        SpecialtyResponseDto result = service.update(1, request);
        assertThat(result.name()).isEqualTo("updated");
        verify(mapper).updateEntity(request, radiology);
    }

    @Test
    void update_notFound() {
        SpecialtyRequestDto request = new SpecialtyRequestDto("updated");
        when(repository.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.update(99, request))
            .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void delete_success() {
        when(repository.findById(1)).thenReturn(Optional.of(radiology));
        when(mapper.toResponse(radiology)).thenReturn(radiologyResponse);

        SpecialtyResponseDto result = service.delete(1);
        assertThat(result.id()).isEqualTo(1);
        verify(repository).delete(radiology);
    }

    @Test
    void delete_notFound() {
        when(repository.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.delete(99))
            .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void searchByName_returnsResults() {
        when(repository.findByNameContainingIgnoreCase("radio")).thenReturn(List.of(radiology));
        when(mapper.toResponseList(List.of(radiology))).thenReturn(List.of(radiologyResponse));

        List<SpecialtyResponseDto> result = service.searchByName("radio");
        assertThat(result).hasSize(1);
    }
}
