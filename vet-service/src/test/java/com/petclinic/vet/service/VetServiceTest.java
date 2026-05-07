package com.petclinic.vet.service;

import com.petclinic.vet.dto.SpecialtyRequest;
import com.petclinic.vet.dto.VetRequest;
import com.petclinic.vet.dto.VetResponse;
import com.petclinic.vet.entity.Specialty;
import com.petclinic.vet.entity.Vet;
import com.petclinic.vet.exception.ResourceNotFoundException;
import com.petclinic.vet.mapper.VetMapper;
import com.petclinic.vet.repository.SpecialtyRepository;
import com.petclinic.vet.repository.VetRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class VetServiceTest {

    @Mock
    private VetRepository vetRepository;

    @Mock
    private SpecialtyRepository specialtyRepository;

    @Mock
    private VetMapper vetMapper;

    @InjectMocks
    private VetService service;

    private Vet vetEntity;
    private Specialty specialtyEntity;
    private VetResponse vetResponse;
    private VetRequest vetRequest;

    @BeforeEach
    void setUp() {
        specialtyEntity = new Specialty();
        specialtyEntity.setId(1);
        specialtyEntity.setName("radiology");

        vetEntity = new Vet();
        vetEntity.setId(1);
        vetEntity.setFirstName("James");
        vetEntity.setLastName("Carter");
        vetEntity.setSpecialties(Set.of(specialtyEntity));

        vetResponse = new VetResponse(1, "James", "Carter", List.of());
        vetRequest = new VetRequest("James", "Carter", List.of(new SpecialtyRequest("radiology")));
    }

    @Test
    void findAll_returnsList() {
        when(vetRepository.findAll()).thenReturn(List.of(vetEntity));
        when(vetMapper.toResponseList(List.of(vetEntity))).thenReturn(List.of(vetResponse));

        List<VetResponse> result = service.findAll();
        assertThat(result).hasSize(1);
    }

    @Test
    void findById_found() {
        when(vetRepository.findById(1)).thenReturn(Optional.of(vetEntity));
        when(vetMapper.toResponse(vetEntity)).thenReturn(vetResponse);

        VetResponse result = service.findById(1);
        assertThat(result.id()).isEqualTo(1);
        assertThat(result.firstName()).isEqualTo("James");
    }

    @Test
    void findById_notFound_throwsException() {
        when(vetRepository.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.findById(99))
            .isInstanceOf(ResourceNotFoundException.class)
            .hasMessageContaining("Vet not found with id: 99");
    }

    @Test
    void create_savesVetWithExistingSpecialty() {
        when(specialtyRepository.findByNameIgnoreCase("radiology")).thenReturn(Optional.of(specialtyEntity));
        when(vetRepository.save(any(Vet.class))).thenReturn(vetEntity);
        when(vetMapper.toResponse(vetEntity)).thenReturn(vetResponse);

        VetResponse result = service.create(vetRequest);
        assertThat(result.firstName()).isEqualTo("James");
        verify(vetRepository).save(any(Vet.class));
    }

    @Test
    void create_savesVetWithNewSpecialty() {
        when(specialtyRepository.findByNameIgnoreCase("radiology")).thenReturn(Optional.empty());
        when(specialtyRepository.save(any(Specialty.class))).thenReturn(specialtyEntity);
        when(vetRepository.save(any(Vet.class))).thenReturn(vetEntity);
        when(vetMapper.toResponse(vetEntity)).thenReturn(vetResponse);

        VetResponse result = service.create(vetRequest);
        assertThat(result.firstName()).isEqualTo("James");
        verify(specialtyRepository).save(any(Specialty.class));
    }

    @Test
    void create_savesVetWithNullSpecialties() {
        VetRequest requestNoSpecs = new VetRequest("James", "Carter", null);
        when(vetRepository.save(any(Vet.class))).thenReturn(vetEntity);
        when(vetMapper.toResponse(vetEntity)).thenReturn(vetResponse);

        VetResponse result = service.create(requestNoSpecs);
        assertThat(result.firstName()).isEqualTo("James");
    }

    @Test
    void update_found_updatesAndReturns() {
        when(vetRepository.findById(1)).thenReturn(Optional.of(vetEntity));
        when(specialtyRepository.findByNameIgnoreCase("radiology")).thenReturn(Optional.of(specialtyEntity));
        when(vetRepository.save(vetEntity)).thenReturn(vetEntity);
        when(vetMapper.toResponse(vetEntity)).thenReturn(vetResponse);

        VetResponse result = service.update(1, vetRequest);
        assertThat(result.firstName()).isEqualTo("James");
    }

    @Test
    void update_notFound_throwsException() {
        when(vetRepository.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.update(99, vetRequest))
            .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void delete_found_deletesAndReturns() {
        when(vetRepository.findById(1)).thenReturn(Optional.of(vetEntity));
        when(vetMapper.toResponse(vetEntity)).thenReturn(vetResponse);

        VetResponse result = service.delete(1);
        assertThat(result.id()).isEqualTo(1);
        verify(vetRepository).delete(vetEntity);
    }

    @Test
    void delete_notFound_throwsException() {
        when(vetRepository.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.delete(99))
            .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void findByLastName_returnsList() {
        when(vetRepository.findByLastNameContainingIgnoreCase("Carter")).thenReturn(List.of(vetEntity));
        when(vetMapper.toResponseList(List.of(vetEntity))).thenReturn(List.of(vetResponse));

        List<VetResponse> result = service.findByLastName("Carter");
        assertThat(result).hasSize(1);
    }

    @Test
    void findBySpecialtyName_returnsList() {
        when(vetRepository.findBySpecialtyName("radiology")).thenReturn(List.of(vetEntity));
        when(vetMapper.toResponseList(List.of(vetEntity))).thenReturn(List.of(vetResponse));

        List<VetResponse> result = service.findBySpecialtyName("radiology");
        assertThat(result).hasSize(1);
    }
}
