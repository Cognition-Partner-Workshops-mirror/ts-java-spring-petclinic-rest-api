package com.petclinic.vet.service;

import com.petclinic.vet.dto.SpecialtyResponse;
import com.petclinic.vet.dto.VetRequest;
import com.petclinic.vet.dto.VetResponse;
import com.petclinic.vet.entity.Specialty;
import com.petclinic.vet.entity.Vet;
import com.petclinic.vet.exception.ResourceNotFoundException;
import com.petclinic.vet.mapper.VetMapper;
import com.petclinic.vet.repository.SpecialtyRepository;
import com.petclinic.vet.repository.VetRepository;
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
    private Specialty specialty;
    private VetResponse vetResponse;

    @BeforeEach
    void setUp() {
        specialty = new Specialty();
        specialty.setId(1);
        specialty.setName("radiology");

        vet = new Vet();
        vet.setId(1);
        vet.setFirstName("James");
        vet.setLastName("Carter");

        vetResponse = new VetResponse(1, "James", "Carter",
            List.of(new SpecialtyResponse(1, "radiology")));
    }

    @Test
    void listAll_returnsAllVets() {
        when(vetRepository.findAll()).thenReturn(List.of(vet));
        when(vetMapper.toResponse(vet)).thenReturn(vetResponse);

        List<VetResponse> result = service.listAll();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).firstName()).isEqualTo("James");
    }

    @Test
    void listAll_returnsEmpty() {
        when(vetRepository.findAll()).thenReturn(List.of());

        List<VetResponse> result = service.listAll();

        assertThat(result).isEmpty();
    }

    @Test
    void getById_returnsVet() {
        when(vetRepository.findById(1)).thenReturn(Optional.of(vet));
        when(vetMapper.toResponse(vet)).thenReturn(vetResponse);

        VetResponse result = service.getById(1);

        assertThat(result.firstName()).isEqualTo("James");
    }

    @Test
    void getById_throwsWhenNotFound() {
        when(vetRepository.findById(999)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.getById(999))
            .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void create_savesVetWithSpecialties() {
        VetRequest request = new VetRequest("James", "Carter", List.of(1));
        when(specialtyRepository.findAllById(List.of(1))).thenReturn(List.of(specialty));
        when(vetRepository.save(any(Vet.class))).thenReturn(vet);
        when(vetMapper.toResponse(any(Vet.class))).thenReturn(vetResponse);

        VetResponse result = service.create(request);

        assertThat(result.firstName()).isEqualTo("James");
        verify(vetRepository).save(any(Vet.class));
    }

    @Test
    void create_savesVetWithEmptySpecialties() {
        VetRequest request = new VetRequest("James", "Carter", List.of());
        when(vetRepository.save(any(Vet.class))).thenReturn(vet);
        when(vetMapper.toResponse(any(Vet.class))).thenReturn(vetResponse);

        VetResponse result = service.create(request);

        assertThat(result).isNotNull();
    }

    @Test
    void create_savesVetWithNullSpecialties() {
        VetRequest request = new VetRequest("James", "Carter", null);
        when(vetRepository.save(any(Vet.class))).thenReturn(vet);
        when(vetMapper.toResponse(any(Vet.class))).thenReturn(vetResponse);

        VetResponse result = service.create(request);

        assertThat(result).isNotNull();
    }

    @Test
    void create_throwsWhenSpecialtyNotFound() {
        VetRequest request = new VetRequest("James", "Carter", List.of(999));
        when(specialtyRepository.findAllById(List.of(999))).thenReturn(List.of());

        assertThatThrownBy(() -> service.create(request))
            .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void update_updatesVet() {
        VetRequest request = new VetRequest("Updated", "Name", List.of(1));
        when(vetRepository.findById(1)).thenReturn(Optional.of(vet));
        when(specialtyRepository.findAllById(List.of(1))).thenReturn(List.of(specialty));
        when(vetRepository.save(any(Vet.class))).thenReturn(vet);
        when(vetMapper.toResponse(any(Vet.class))).thenReturn(vetResponse);

        VetResponse result = service.update(1, request);

        assertThat(result).isNotNull();
    }

    @Test
    void update_throwsWhenVetNotFound() {
        VetRequest request = new VetRequest("Updated", "Name", List.of());
        when(vetRepository.findById(999)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.update(999, request))
            .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void delete_deletesAndReturns() {
        when(vetRepository.findById(1)).thenReturn(Optional.of(vet));
        when(vetMapper.toResponse(vet)).thenReturn(vetResponse);

        VetResponse result = service.delete(1);

        assertThat(result.firstName()).isEqualTo("James");
        verify(vetRepository).delete(vet);
    }

    @Test
    void delete_throwsWhenNotFound() {
        when(vetRepository.findById(999)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.delete(999))
            .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void findBySpecialty_returnsMatches() {
        when(vetRepository.findBySpecialtyId(1)).thenReturn(List.of(vet));
        when(vetMapper.toResponse(vet)).thenReturn(vetResponse);

        List<VetResponse> result = service.findBySpecialty(1);

        assertThat(result).hasSize(1);
    }

    @Test
    void searchByLastName_returnsMatches() {
        when(vetRepository.findByLastNameContainingIgnoreCase("Cart")).thenReturn(List.of(vet));
        when(vetMapper.toResponse(vet)).thenReturn(vetResponse);

        List<VetResponse> result = service.searchByLastName("Cart");

        assertThat(result).hasSize(1);
    }

    @Test
    void findBySpecialtyName_returnsMatches() {
        when(vetRepository.findBySpecialtyName("radiology")).thenReturn(List.of(vet));
        when(vetMapper.toResponse(vet)).thenReturn(vetResponse);

        List<VetResponse> result = service.findBySpecialtyName("radiology");

        assertThat(result).hasSize(1);
    }

    @Test
    void findBySpecialtyName_returnsEmpty() {
        when(vetRepository.findBySpecialtyName("nonexistent")).thenReturn(List.of());

        List<VetResponse> result = service.findBySpecialtyName("nonexistent");

        assertThat(result).isEmpty();
    }
}
