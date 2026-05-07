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
import static org.mockito.ArgumentMatchers.anySet;
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
    private VetServiceImpl vetService;

    private Vet vet;
    private VetResponse vetResponse;

    @BeforeEach
    void setUp() {
        Specialty radiology = new Specialty();
        radiology.setId(1);
        radiology.setName("radiology");

        vet = new Vet();
        vet.setId(1);
        vet.setFirstName("James");
        vet.setLastName("Carter");
        vet.setSpecialties(Set.of(radiology));

        vetResponse = new VetResponse(1, "James", "Carter",
            List.of(new com.petclinic.vet.dto.SpecialtyResponse(1, "radiology")));
    }

    @Test
    void findAll_returnsAllVets() {
        when(vetRepository.findAll()).thenReturn(List.of(vet));
        when(vetMapper.toResponseList(any())).thenReturn(List.of(vetResponse));

        List<VetResponse> result = vetService.findAll();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).firstName()).isEqualTo("James");
    }

    @Test
    void findById_existingId_returnsVet() {
        when(vetRepository.findById(1)).thenReturn(Optional.of(vet));
        when(vetMapper.toResponse(vet)).thenReturn(vetResponse);

        VetResponse result = vetService.findById(1);

        assertThat(result.id()).isEqualTo(1);
        assertThat(result.firstName()).isEqualTo("James");
    }

    @Test
    void findById_nonExistingId_throwsNotFound() {
        when(vetRepository.findById(999)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> vetService.findById(999))
            .isInstanceOf(ResourceNotFoundException.class)
            .hasMessageContaining("Vet not found with id 999");
    }

    @Test
    void create_validRequest_returnsCreatedVet() {
        VetRequest request = new VetRequest("James", "Carter",
            List.of(new SpecialtyRequest("radiology")));

        Specialty radiology = new Specialty();
        radiology.setId(1);
        radiology.setName("radiology");

        when(vetMapper.toEntity(request)).thenReturn(vet);
        when(specialtyRepository.findByNameIn(anySet())).thenReturn(List.of(radiology));
        when(vetRepository.save(any(Vet.class))).thenReturn(vet);
        when(vetMapper.toResponse(vet)).thenReturn(vetResponse);

        VetResponse result = vetService.create(request);

        assertThat(result.firstName()).isEqualTo("James");
        verify(vetRepository).save(any(Vet.class));
    }

    @Test
    void create_withNullSpecialties_returnsCreatedVet() {
        VetRequest request = new VetRequest("James", "Carter", null);

        when(vetMapper.toEntity(request)).thenReturn(vet);
        when(vetRepository.save(any(Vet.class))).thenReturn(vet);
        when(vetMapper.toResponse(vet)).thenReturn(vetResponse);

        VetResponse result = vetService.create(request);

        assertThat(result.firstName()).isEqualTo("James");
    }

    @Test
    void create_withEmptySpecialties_returnsCreatedVet() {
        VetRequest request = new VetRequest("James", "Carter", List.of());

        when(vetMapper.toEntity(request)).thenReturn(vet);
        when(vetRepository.save(any(Vet.class))).thenReturn(vet);
        when(vetMapper.toResponse(vet)).thenReturn(vetResponse);

        VetResponse result = vetService.create(request);

        assertThat(result.firstName()).isEqualTo("James");
    }

    @Test
    void update_existingId_returnsUpdatedVet() {
        VetRequest request = new VetRequest("Helen", "Leary",
            List.of(new SpecialtyRequest("surgery")));

        Specialty surgery = new Specialty();
        surgery.setId(2);
        surgery.setName("surgery");

        VetResponse updatedResponse = new VetResponse(1, "Helen", "Leary",
            List.of(new com.petclinic.vet.dto.SpecialtyResponse(2, "surgery")));

        when(vetRepository.findById(1)).thenReturn(Optional.of(vet));
        when(specialtyRepository.findByNameIn(anySet())).thenReturn(List.of(surgery));
        when(vetRepository.save(any(Vet.class))).thenReturn(vet);
        when(vetMapper.toResponse(any(Vet.class))).thenReturn(updatedResponse);

        VetResponse result = vetService.update(1, request);

        assertThat(result.firstName()).isEqualTo("Helen");
    }

    @Test
    void update_nonExistingId_throwsNotFound() {
        VetRequest request = new VetRequest("Helen", "Leary", List.of());

        when(vetRepository.findById(999)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> vetService.update(999, request))
            .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void delete_existingId_returnsDeletedVet() {
        when(vetRepository.findById(1)).thenReturn(Optional.of(vet));
        when(vetMapper.toResponse(vet)).thenReturn(vetResponse);

        VetResponse result = vetService.delete(1);

        assertThat(result.id()).isEqualTo(1);
        verify(vetRepository).delete(vet);
    }

    @Test
    void delete_nonExistingId_throwsNotFound() {
        when(vetRepository.findById(999)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> vetService.delete(999))
            .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void findBySpecialty_returnsList() {
        when(vetRepository.findBySpecialtyName("radiology")).thenReturn(List.of(vet));
        when(vetMapper.toResponseList(any())).thenReturn(List.of(vetResponse));

        List<VetResponse> result = vetService.findBySpecialty("radiology");

        assertThat(result).hasSize(1);
    }

    @Test
    void searchByName_returnsList() {
        when(vetRepository.searchByName("James")).thenReturn(List.of(vet));
        when(vetMapper.toResponseList(any())).thenReturn(List.of(vetResponse));

        List<VetResponse> result = vetService.searchByName("James");

        assertThat(result).hasSize(1);
    }
}
