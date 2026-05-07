package com.petclinic.vet.service;

import com.petclinic.vet.dto.SpecialtyRequest;
import com.petclinic.vet.dto.SpecialtyResponse;
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
class SpecialtyServiceTest {

    @Mock
    private SpecialtyRepository repository;

    @Mock
    private SpecialtyMapper mapper;

    @InjectMocks
    private SpecialtyService service;

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
    void findAll_returnsAllSpecialties() {
        Specialty surgery = new Specialty();
        surgery.setId(2);
        surgery.setName("surgery");
        SpecialtyResponse surgeryResponse = new SpecialtyResponse(2, "surgery");

        when(repository.findAll()).thenReturn(List.of(radiology, surgery));
        when(mapper.toResponse(radiology)).thenReturn(radiologyResponse);
        when(mapper.toResponse(surgery)).thenReturn(surgeryResponse);

        List<SpecialtyResponse> result = service.findAll();

        assertThat(result).hasSize(2);
        assertThat(result.get(0).name()).isEqualTo("radiology");
    }

    @Test
    void findById_returnsSpecialty() {
        when(repository.findById(1)).thenReturn(Optional.of(radiology));
        when(mapper.toResponse(radiology)).thenReturn(radiologyResponse);

        SpecialtyResponse result = service.findById(1);

        assertThat(result.id()).isEqualTo(1);
        assertThat(result.name()).isEqualTo("radiology");
    }

    @Test
    void findById_throwsWhenNotFound() {
        when(repository.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.findById(99))
            .isInstanceOf(ResourceNotFoundException.class)
            .hasMessageContaining("Specialty not found with id 99");
    }

    @Test
    void create_savesAndReturnsSpecialty() {
        SpecialtyRequest request = new SpecialtyRequest("radiology");
        Specialty newEntity = new Specialty();
        newEntity.setName("radiology");

        when(mapper.toEntity(request)).thenReturn(newEntity);
        when(repository.save(newEntity)).thenReturn(radiology);
        when(mapper.toResponse(radiology)).thenReturn(radiologyResponse);

        SpecialtyResponse result = service.create(request);

        assertThat(result.name()).isEqualTo("radiology");
        verify(repository).save(newEntity);
    }

    @Test
    void update_updatesAndReturnsSpecialty() {
        SpecialtyRequest request = new SpecialtyRequest("dentistry");
        SpecialtyResponse updatedResponse = new SpecialtyResponse(1, "dentistry");

        when(repository.findById(1)).thenReturn(Optional.of(radiology));
        when(repository.save(radiology)).thenReturn(radiology);
        when(mapper.toResponse(radiology)).thenReturn(updatedResponse);

        SpecialtyResponse result = service.update(1, request);

        assertThat(result.name()).isEqualTo("dentistry");
        verify(mapper).updateEntity(radiology, request);
    }

    @Test
    void update_throwsWhenNotFound() {
        SpecialtyRequest request = new SpecialtyRequest("dentistry");
        when(repository.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.update(99, request))
            .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void delete_deletesAndReturnsSpecialty() {
        when(repository.findById(1)).thenReturn(Optional.of(radiology));
        when(mapper.toResponse(radiology)).thenReturn(radiologyResponse);

        SpecialtyResponse result = service.delete(1);

        assertThat(result.id()).isEqualTo(1);
        verify(repository).delete(radiology);
    }

    @Test
    void delete_throwsWhenNotFound() {
        when(repository.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.delete(99))
            .isInstanceOf(ResourceNotFoundException.class);
    }
}
