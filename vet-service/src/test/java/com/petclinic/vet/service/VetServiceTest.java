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

    private Vet vet;
    private VetResponse vetResponse;
    private VetRequest vetRequest;
    private Specialty specialty;

    @BeforeEach
    void setUp() {
        specialty = new Specialty(1, "radiology");
        vet = new Vet(1, "James", "Carter");
        vet.setSpecialties(Set.of(specialty));
        vetResponse = new VetResponse(1, "James", "Carter",
            List.of(new SpecialtyResponse(1, "radiology")));
        vetRequest = new VetRequest("James", "Carter",
            List.of(new SpecialtyRequest("radiology")));
    }

    @Test
    void findAll_returnsList() {
        when(vetRepository.findAll()).thenReturn(List.of(vet));
        when(vetMapper.toResponseList(List.of(vet))).thenReturn(List.of(vetResponse));

        List<VetResponse> result = service.findAll();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).firstName()).isEqualTo("James");
    }

    @Test
    void findById_existing_returnsResponse() {
        when(vetRepository.findById(1)).thenReturn(Optional.of(vet));
        when(vetMapper.toResponse(vet)).thenReturn(vetResponse);

        VetResponse result = service.findById(1);

        assertThat(result.id()).isEqualTo(1);
        assertThat(result.firstName()).isEqualTo("James");
    }

    @Test
    void findById_nonExisting_throwsNotFound() {
        when(vetRepository.findById(999)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.findById(999))
            .isInstanceOf(ResourceNotFoundException.class)
            .hasMessageContaining("Vet not found with id 999");
    }

    @Test
    void create_withExistingSpecialty_savesAndReturns() {
        when(specialtyRepository.findByNameContainingIgnoreCase("radiology")).thenReturn(List.of(specialty));
        when(vetRepository.save(any(Vet.class))).thenReturn(vet);
        when(vetMapper.toResponse(vet)).thenReturn(vetResponse);

        VetResponse result = service.create(vetRequest);

        assertThat(result.firstName()).isEqualTo("James");
        verify(vetRepository).save(any(Vet.class));
    }

    @Test
    void create_withNewSpecialty_createsSpecialtyAndSaves() {
        when(specialtyRepository.findByNameContainingIgnoreCase("radiology")).thenReturn(List.of());
        when(specialtyRepository.save(any(Specialty.class))).thenReturn(specialty);
        when(vetRepository.save(any(Vet.class))).thenReturn(vet);
        when(vetMapper.toResponse(vet)).thenReturn(vetResponse);

        VetResponse result = service.create(vetRequest);

        assertThat(result.firstName()).isEqualTo("James");
        verify(specialtyRepository).save(any(Specialty.class));
    }

    @Test
    void create_withNullSpecialties_savesWithEmptySet() {
        VetRequest requestNoSpecialties = new VetRequest("James", "Carter", null);
        Vet vetNoSpec = new Vet(1, "James", "Carter");
        VetResponse responseNoSpec = new VetResponse(1, "James", "Carter", List.of());
        when(vetRepository.save(any(Vet.class))).thenReturn(vetNoSpec);
        when(vetMapper.toResponse(vetNoSpec)).thenReturn(responseNoSpec);

        VetResponse result = service.create(requestNoSpecialties);

        assertThat(result.specialties()).isEmpty();
    }

    @Test
    void update_existing_updatesAndReturns() {
        when(vetRepository.findById(1)).thenReturn(Optional.of(vet));
        when(specialtyRepository.findByNameContainingIgnoreCase("radiology")).thenReturn(List.of(specialty));
        when(vetRepository.save(vet)).thenReturn(vet);
        when(vetMapper.toResponse(vet)).thenReturn(vetResponse);

        VetResponse result = service.update(1, vetRequest);

        assertThat(result.firstName()).isEqualTo("James");
    }

    @Test
    void update_nonExisting_throwsNotFound() {
        when(vetRepository.findById(999)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.update(999, vetRequest))
            .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void delete_existing_deletesAndReturns() {
        when(vetRepository.findById(1)).thenReturn(Optional.of(vet));
        when(vetMapper.toResponse(vet)).thenReturn(vetResponse);

        VetResponse result = service.delete(1);

        assertThat(result.id()).isEqualTo(1);
        verify(vetRepository).delete(vet);
    }

    @Test
    void delete_nonExisting_throwsNotFound() {
        when(vetRepository.findById(999)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.delete(999))
            .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void findBySpecialtyId_returnsList() {
        when(vetRepository.findBySpecialtyId(1)).thenReturn(List.of(vet));
        when(vetMapper.toResponseList(List.of(vet))).thenReturn(List.of(vetResponse));

        List<VetResponse> result = service.findBySpecialtyId(1);

        assertThat(result).hasSize(1);
    }

    @Test
    void searchByLastName_returnsList() {
        when(vetRepository.findByLastNameContainingIgnoreCase("Carter")).thenReturn(List.of(vet));
        when(vetMapper.toResponseList(List.of(vet))).thenReturn(List.of(vetResponse));

        List<VetResponse> result = service.searchByLastName("Carter");

        assertThat(result).hasSize(1);
    }
}
