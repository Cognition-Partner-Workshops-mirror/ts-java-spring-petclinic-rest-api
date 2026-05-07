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
        radiology = new Specialty(1, "radiology");
        radiologyResponse = new SpecialtyResponse(1, "radiology");
    }

    @Test
    void listAll_returnsAllSpecialties() {
        when(repository.findAll()).thenReturn(List.of(radiology));
        when(mapper.toResponseList(List.of(radiology))).thenReturn(List.of(radiologyResponse));

        List<SpecialtyResponse> result = service.listAll();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).name()).isEqualTo("radiology");
    }

    @Test
    void getById_existingId_returnsSpecialty() {
        when(repository.findById(1)).thenReturn(Optional.of(radiology));
        when(mapper.toResponse(radiology)).thenReturn(radiologyResponse);

        SpecialtyResponse result = service.getById(1);

        assertThat(result.id()).isEqualTo(1);
        assertThat(result.name()).isEqualTo("radiology");
    }

    @Test
    void getById_nonExistingId_throwsNotFound() {
        when(repository.findById(999)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.getById(999))
            .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void create_validRequest_returnsCreated() {
        SpecialtyRequest request = new SpecialtyRequest("oncology");
        Specialty oncology = new Specialty(null, "oncology");
        Specialty saved = new Specialty(4, "oncology");
        SpecialtyResponse response = new SpecialtyResponse(4, "oncology");

        when(mapper.toEntity(request)).thenReturn(oncology);
        when(repository.save(oncology)).thenReturn(saved);
        when(mapper.toResponse(saved)).thenReturn(response);

        SpecialtyResponse result = service.create(request);

        assertThat(result.id()).isEqualTo(4);
        assertThat(result.name()).isEqualTo("oncology");
    }

    @Test
    void update_existingId_updatesAndReturns() {
        SpecialtyRequest request = new SpecialtyRequest("updated");
        when(repository.findById(1)).thenReturn(Optional.of(radiology));
        when(repository.save(radiology)).thenReturn(radiology);
        when(mapper.toResponse(radiology)).thenReturn(new SpecialtyResponse(1, "updated"));

        SpecialtyResponse result = service.update(1, request);

        assertThat(result.name()).isEqualTo("updated");
        verify(mapper).updateEntity(request, radiology);
    }

    @Test
    void update_nonExistingId_throwsNotFound() {
        when(repository.findById(999)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.update(999, new SpecialtyRequest("x")))
            .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void delete_existingId_deletesAndReturns() {
        when(repository.findById(1)).thenReturn(Optional.of(radiology));
        when(mapper.toResponse(radiology)).thenReturn(radiologyResponse);

        SpecialtyResponse result = service.delete(1);

        assertThat(result.id()).isEqualTo(1);
        verify(repository).delete(radiology);
    }

    @Test
    void delete_nonExistingId_throwsNotFound() {
        when(repository.findById(999)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.delete(999))
            .isInstanceOf(ResourceNotFoundException.class);
    }
}
