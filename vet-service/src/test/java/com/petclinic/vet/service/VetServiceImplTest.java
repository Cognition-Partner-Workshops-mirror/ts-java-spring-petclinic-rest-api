package com.petclinic.vet.service;

import com.petclinic.vet.dto.SpecialtyRequest;
import com.petclinic.vet.dto.SpecialtyResponse;
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

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class VetServiceImplTest {

    @Mock
    private VetRepository vetRepository;

    @Mock
    private SpecialtyRepository specialtyRepository;

    @Mock
    private VetMapper vetMapper;

    @InjectMocks
    private VetServiceImpl service;

    private Vet vet;
    private VetResponse vetResponse;
    private Specialty radiology;

    @BeforeEach
    void setUp() {
        radiology = new Specialty();
        radiology.setId(1);
        radiology.setName("radiology");

        vet = new Vet();
        vet.setId(1);
        vet.setFirstName("James");
        vet.setLastName("Carter");
        vet.setSpecialties(new HashSet<>(Set.of(radiology)));

        vetResponse = new VetResponse(1, "James", "Carter",
            List.of(new SpecialtyResponse(1, "radiology")));
    }

    @Test
    void listAll_returnsAll() {
        when(vetRepository.findAllWithSpecialties()).thenReturn(List.of(vet));
        when(vetMapper.toResponseList(List.of(vet))).thenReturn(List.of(vetResponse));

        List<VetResponse> result = service.listAll();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).firstName()).isEqualTo("James");
    }

    @Test
    void getById_found() {
        when(vetRepository.findById(1)).thenReturn(Optional.of(vet));
        when(vetMapper.toResponse(vet)).thenReturn(vetResponse);

        VetResponse result = service.getById(1);

        assertThat(result.id()).isEqualTo(1);
    }

    @Test
    void getById_notFound() {
        when(vetRepository.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.getById(99))
            .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void create_withExistingSpecialty() {
        VetRequest request = new VetRequest("James", "Carter",
            List.of(new SpecialtyRequest("radiology")));
        Vet newVet = new Vet();

        when(vetMapper.toEntity(request)).thenReturn(newVet);
        when(specialtyRepository.findByNameIgnoreCase("radiology")).thenReturn(Optional.of(radiology));
        when(vetRepository.save(newVet)).thenReturn(vet);
        when(vetMapper.toResponse(vet)).thenReturn(vetResponse);

        VetResponse result = service.create(request);

        assertThat(result.firstName()).isEqualTo("James");
        assertThat(newVet.getSpecialties()).contains(radiology);
    }

    @Test
    void create_withNewSpecialty() {
        VetRequest request = new VetRequest("James", "Carter",
            List.of(new SpecialtyRequest("surgery")));
        Vet newVet = new Vet();
        Specialty surgery = new Specialty();
        surgery.setId(2);
        surgery.setName("surgery");

        when(vetMapper.toEntity(request)).thenReturn(newVet);
        when(specialtyRepository.findByNameIgnoreCase("surgery")).thenReturn(Optional.empty());
        when(specialtyRepository.save(any(Specialty.class))).thenReturn(surgery);
        when(vetRepository.save(newVet)).thenReturn(vet);
        when(vetMapper.toResponse(vet)).thenReturn(vetResponse);

        VetResponse result = service.create(request);

        assertThat(result).isNotNull();
    }

    @Test
    void update_success() {
        VetRequest request = new VetRequest("Helen", "Leary",
            List.of(new SpecialtyRequest("radiology")));

        when(vetRepository.findById(1)).thenReturn(Optional.of(vet));
        when(specialtyRepository.findByNameIgnoreCase("radiology")).thenReturn(Optional.of(radiology));
        when(vetRepository.save(vet)).thenReturn(vet);
        when(vetMapper.toResponse(vet)).thenReturn(
            new VetResponse(1, "Helen", "Leary", List.of(new SpecialtyResponse(1, "radiology"))));

        VetResponse result = service.update(1, request);

        assertThat(result.firstName()).isEqualTo("Helen");
    }

    @Test
    void update_notFound() {
        when(vetRepository.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.update(99,
            new VetRequest("A", "B", List.of())))
            .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void delete_success() {
        when(vetRepository.findById(1)).thenReturn(Optional.of(vet));
        when(vetMapper.toResponse(vet)).thenReturn(vetResponse);

        VetResponse result = service.delete(1);

        verify(vetRepository).delete(vet);
        assertThat(result.id()).isEqualTo(1);
    }

    @Test
    void delete_notFound() {
        when(vetRepository.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.delete(99))
            .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void findByLastName_returnsList() {
        when(vetRepository.findByLastNameContainingIgnoreCase("Carter")).thenReturn(List.of(vet));
        when(vetMapper.toResponseList(List.of(vet))).thenReturn(List.of(vetResponse));

        List<VetResponse> result = service.findByLastName("Carter");

        assertThat(result).hasSize(1);
    }

    @Test
    void findBySpecialty_returnsList() {
        when(vetRepository.findBySpecialtyName("radiology")).thenReturn(List.of(vet));
        when(vetMapper.toResponseList(List.of(vet))).thenReturn(List.of(vetResponse));

        List<VetResponse> result = service.findBySpecialty("radiology");

        assertThat(result).hasSize(1);
    }
}
