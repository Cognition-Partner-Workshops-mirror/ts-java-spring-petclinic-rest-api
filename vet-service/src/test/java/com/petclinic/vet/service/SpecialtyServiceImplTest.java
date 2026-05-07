package com.petclinic.vet.service;

import com.petclinic.vet.dto.SpecialtyRequest;
import com.petclinic.vet.dto.SpecialtyResponse;
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
    private SpecialtyRepository repository;

    @Mock
    private SpecialtyMapper mapper;

    @InjectMocks
    private SpecialtyServiceImpl service;

    private Specialty radiology;
    private SpecialtyResponse radiologyResponse;

    @BeforeEach
    void setUp() {
        radiology = new Specialty();
        radiology.setId(1);
        radiology.setName("radiology");
        radiologyResponse = new SpecialtyResponse(1, "radiology");
    }

    @Test
    void listAll_returnsAll() {
        when(repository.findAll()).thenReturn(List.of(radiology));
        when(mapper.toResponseList(List.of(radiology))).thenReturn(List.of(radiologyResponse));

        List<SpecialtyResponse> result = service.listAll();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).name()).isEqualTo("radiology");
    }

    @Test
    void getById_found() {
        when(repository.findById(1)).thenReturn(Optional.of(radiology));
        when(mapper.toResponse(radiology)).thenReturn(radiologyResponse);

        SpecialtyResponse result = service.getById(1);

        assertThat(result.id()).isEqualTo(1);
        assertThat(result.name()).isEqualTo("radiology");
    }

    @Test
    void getById_notFound() {
        when(repository.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.getById(99))
            .isInstanceOf(ResourceNotFoundException.class)
            .hasMessageContaining("99");
    }

    @Test
    void create_success() {
        SpecialtyRequest request = new SpecialtyRequest("surgery");
        Specialty surgery = new Specialty();
        surgery.setName("surgery");
        Specialty saved = new Specialty();
        saved.setId(2);
        saved.setName("surgery");
        SpecialtyResponse surgeryResponse = new SpecialtyResponse(2, "surgery");

        when(mapper.toEntity(request)).thenReturn(surgery);
        when(repository.save(surgery)).thenReturn(saved);
        when(mapper.toResponse(saved)).thenReturn(surgeryResponse);

        SpecialtyResponse result = service.create(request);

        assertThat(result.id()).isEqualTo(2);
        assertThat(result.name()).isEqualTo("surgery");
    }

    @Test
    void update_success() {
        SpecialtyRequest request = new SpecialtyRequest("dentistry");

        when(repository.findById(1)).thenReturn(Optional.of(radiology));
        when(repository.save(radiology)).thenReturn(radiology);
        when(mapper.toResponse(radiology)).thenReturn(new SpecialtyResponse(1, "dentistry"));

        SpecialtyResponse result = service.update(1, request);

        verify(mapper).updateEntity(request, radiology);
        assertThat(result.name()).isEqualTo("dentistry");
    }

    @Test
    void update_notFound() {
        when(repository.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.update(99, new SpecialtyRequest("x")))
            .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void delete_success() {
        when(repository.findById(1)).thenReturn(Optional.of(radiology));
        when(mapper.toResponse(radiology)).thenReturn(radiologyResponse);

        SpecialtyResponse result = service.delete(1);

        verify(repository).delete(radiology);
        assertThat(result.id()).isEqualTo(1);
    }

    @Test
    void delete_notFound() {
        when(repository.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.delete(99))
            .isInstanceOf(ResourceNotFoundException.class);
    }
}
